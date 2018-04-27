/**********************************************************************/
/*                                                                    */
/* File name: drv_rs.c                                                */
/*                                                                    */
/* Since:     2005-Jan-08                                             */
/*                                                                    */
/* Version:   PICos18 v2.10                                           */
/*            Copyright (C) 2003, 2004, 2005 Pragmatec.               */
/*            RS232 driver v1.05                                      */
/*                                                                    */
/* Author:    Designed by Pragmatec S.A.R.L.        www.pragmatec.net */
/*            MONTAGNE Xavier [XM]      xavier.montagne@pragmatec.net */
/*                                                                    */
/* Purpose:   Manage RS232 generic buffers through the PIC18 USART.   */
/*            Can send ASCII (with printf) or binary string of data.  */
/*                                                                    */
/* Distribution: This file is part of PICos18.                        */
/*            PICos18 is free software; you can redistribute it       */
/*            and/or modify it under the terms of the GNU General     */
/*            Public License as published by the Free Software        */
/*            Foundation; either version 2, or (at your option)       */
/*            any later version.                                      */
/*                                                                    */
/*            PICos18 is distributed in the hope that it will be      */
/*            useful, but WITHOUT ANY WARRANTY; without even the      */
/*            implied warranty of MERCHANTABILITY or FITNESS FOR A    */
/*            PARTICULAR PURPOSE.  See the GNU General Public         */
/*            License for more details.                               */
/*                                                                    */
/*            You should have received a copy of the GNU General      */
/*            Public License along with gpsim; see the file           */
/*            COPYING.txt. If not, write to the Free Software         */
/*            Foundation, 59 Temple Place - Suite 330,                */
/*            Boston, MA 02111-1307, USA.                             */
/*                                                                    */
/*          > A special exception to the GPL can be applied should    */
/*            you wish to distribute a combined work that includes    */
/*            PICos18, without being obliged to provide the source    */
/*            code for any proprietary components.                    */
/*                                                                    */
/* History:                                                           */
/* 2005/01/08 [XM]  Create this file.                                 */
/* 2005/01/11 [XM]  Modified RS_TX_INT to avoid space character at    */
/*                  the beginning of the new line.                    */
/* 2005/02/27 [XM]  Set TXREG before "TXSTAbits.TXEN = 1;" in the task*/
/*                  code to avoid a NULL character to be transmited   */
/*                  when the transfer starts.                         */
/* 2005/03/10 [XM]  Used PIR1bits.TXIF and PIE1bits.TXIE instead of   */
/*                  TXEN to enable or disable the transfer.           */
/*                                                                    */
/**********************************************************************/

#include "define.h"
#include "drv_rs.h"

#include <stdio.h>
#include <string.h>

/* Callback function dedicated to printf output interface */
int _user_putc (char c);


/**********************************************************************
 * Definition dedicated to the local functions.
 **********************************************************************/
EventMaskType   RS_event;

RS_message_tRef	RS_list_head;			//	Start of message queue
RS_message_tRef	RS_current_message;		//	Current message
unsigned char	RS_list_count = 0;		//	Number of items currently in queue

RS_message_tRef	RS_list_head_rcv;		//	Start of message queue
unsigned char	RS_list_count_rcv = 0;	//	Number of items currently in queue

unsigned int    TX_ByteNumber = 0;
unsigned int    RX_ByteNumber = 0;

/**********************************************************************
 * ----------------- RS DRIVER - PIC18 USART MANAGEMENT ---------------
 *
 * void
 *
 **********************************************************************/
TASK(RS_Drv)
{  
  /* Reset USART registers to POR state */
  TXSTA            = 0; 
  RCSTA            = 0;
  
  /* Change baud rate here  [cf datasheet]*/
  /* SPBRG = 255; for   9600 [40MHZ only]*/
  /* SPBRG =  21; for 115200 [40MHZ only]*/
  SPBRG            = 21;
  TXSTAbits.BRGH   = 1;
  
  TXSTAbits.SYNC   = 0;
  RCSTAbits.SPEN   = 1;
  TXSTAbits.TXEN   = 0;
  RCSTAbits.CREN   = 1;
  TRISCbits.TRISC7 = 1;
  TRISCbits.TRISC6 = 0;
  
  PIR1bits.TXIF    = 0;
  PIR1bits.RCIF    = 0;
  PIE1bits.RCIE    = 1;
  PIE1bits.TXIE    = 0;  
  RS_current_message = NULL;

  /* for 'printf' function redirection */
  stdout = _H_USER;

  while(1)
  {
    WaitEvent(RS_NEW_MSG | RS_RCV_MSG);
    GetEvent(RS_DRV_ID, &RS_event);
    
    if (RS_event & RS_RCV_MSG)
      ClearEvent(RS_RCV_MSG);

    if (RS_event & RS_NEW_MSG)
    {
      ClearEvent(RS_NEW_MSG);
      if (PIE1bits.TXIE == 0)
      {
        if (RS_current_message != NULL)
        {
          RS_current_message->length = 0;
          SetEvent(RS_current_message->CallerID, RS_QUEUE_EMPTY);
        }
        RS_current_message = RS_deqMsg();
        if (RS_current_message != NULL)
        {
          TX_ByteNumber = 0;
          TXREG = RS_current_message->data[0];
          TXSTAbits.TXEN = 1;
          PIE1bits.TXIE = 1;
        }
      }
    }
  }
}

/**********************************************************************
 *	Enqueue a client packet object into the RS task queue.
 *
 *	Once placed in queue, client must not modify the data
 *	otherwise unpredictable results. To safely change the object,
 *	dequeue, modify, re-enqueue.
 *
 *  The code in mainly executed in critical region [SuspendAllInterrupt]
 *  because many tasks can call this function at the same time and break
 *  the FIFO list.
 *
 * @param  toEnqueue  IN  New message structure reference
 * @param  data       IN  Buffer which contains the string of data
 * @param  length     IN  Length of the Buffer field
 * @return Status         E_OK if toEnqueue is well attached to the list
 *                        E_OS_STATE if message could not be enqueued
 **********************************************************************/
StatusType RS_enqMsg(RS_message_tRef toEnqueue, unsigned char *data, unsigned int length)
{
  RS_message_tRef RS_list_itor;

  if (toEnqueue != NULL)
  {
    SuspendOSInterrupts();
    if (RS_list_head == NULL)
      RS_list_head = toEnqueue;
    else
    {
      RS_list_itor = RS_list_head;
      while (RS_list_itor->next != NULL)
        RS_list_itor = RS_list_itor->next;
      RS_list_itor->next = toEnqueue;
    }
    toEnqueue->next     = NULL;
    toEnqueue->CallerID = id_tsk_run;
    toEnqueue->length   = length;
    toEnqueue->data     = data;
    RS_list_count++;
    ResumeOSInterrupts();
    return E_OK;
  }
  else
    return E_OS_STATE;
}

/**********************************************************************
 *	Dequeue a client message from the RS task queue.
 *
 * @param  void
 * @return RS_list_itor   Reference of the top of the FIFO list
 *                        (next enqueued message)
 *********************************************************************/
RS_message_tRef RS_deqMsg(void)
{
  RS_message_tRef RS_list_itor;

  RS_list_itor = NULL;
  if (RS_list_head != NULL)
  {
    RS_list_itor = RS_list_head;
    RS_list_head = RS_list_head->next;
    RS_list_count--;
  }
  return RS_list_itor;
} 

/**********************************************************************
 *	Enqueue a client packet object into the RS task queue.
 *
 *	Once placed in queue, client must not modify the data
 *	otherwise unpredictable results. To safely change the object,
 *	dequeue, modify, re-enqueue.
 *
 *  The code in mainly executed in critical region [SuspendAllInterrupt]
 *  because many tasks can call this function at the same time and break
 *  the FIFO list.
 *
 * @param  toEnqueue  IN  New message structure reference
 * @return Status         E_OK if toEnqueue is well attached to the list
 *                        E_OS_STATE if message could not be enqueued
 **********************************************************************/
StatusType RS_RCV_Register(RS_message_tRef toEnqueue, unsigned char *data, 
                           unsigned int length)
{
  RS_message_tRef RS_list_itor;

  if (toEnqueue != NULL)
  {
    SuspendOSInterrupts();
    if (RS_list_head_rcv == NULL)
      RS_list_head_rcv = toEnqueue;
    else
    {
      RS_list_itor = RS_list_head_rcv;
      while (RS_list_itor->next != NULL)
        RS_list_itor = RS_list_itor->next;
      RS_list_itor->next = toEnqueue;
    }
    toEnqueue->next     = NULL;
    toEnqueue->CallerID = id_tsk_run;
    toEnqueue->length   = length;
    toEnqueue->data     = data;
    RS_list_count_rcv++;
    ResumeOSInterrupts();
    return E_OK;
  }
  else
    return E_OS_STATE;
}

/**********************************************************************
 *	Fill the structure with the printf content.
 *
 *  The code in mainly executed in critical region [SuspendAllInterrupt]
 *  because many tasks can call this function at the same time and break
 *  the FIFO list.
 *
 * @param  c          IN  Next character coming from the printf function
 * @return ByteNumber     Current char index in the buffer
 *********************************************************************/
int _user_putc (char c)
{
  RS_message_tRef RS_list_itor;

  SuspendOSInterrupts();
  RS_list_itor = RS_list_head;
  while (RS_list_itor->CallerID != id_tsk_run)
  {
    if (RS_list_itor->next == NULL)
      return 0;
    RS_list_itor = RS_list_itor->next;
  }
  RS_list_itor->data[RS_list_itor->length] = c;
  RS_list_itor->length++;
  ResumeOSInterrupts();
  return RS_list_itor->length;
}

/**********************************************************************
 * Part of the transmiter interrupt.
 *
 * @param  void
 * @return void 
 **********************************************************************/
void RS_TX_INT(void)
{   
  if ((TX_ByteNumber > 0) && (TX_ByteNumber < RS_current_message->length))
  {
    TXREG = RS_current_message->data[TX_ByteNumber];
  }
  if (TX_ByteNumber  == (RS_current_message->length))
  {
    PIE1bits.TXIE = 0;
    SetEvent(RS_DRV_ID, RS_NEW_MSG);
    TX_ByteNumber = -1;
  }
  TX_ByteNumber++;
  return;
}

/**********************************************************************
 * Part of the recepteur interrupt.
 *
 * @param  void
 * @return void 
 **********************************************************************/
void RS_RX_INT(void)
{
  PIR1bits.RCIF = 0;
  if ((RX_ByteNumber >= 0) && 
      (RX_ByteNumber < RS_list_head_rcv->length))
    RS_list_head_rcv->data[RX_ByteNumber] = RCREG;
  if (RX_ByteNumber == (RS_list_head_rcv->length))
  {
    SetEvent(RS_list_head_rcv->CallerID, RS_QUEUE_FULL);
    RX_ByteNumber = -1;
  }
  RX_ByteNumber++;
  return;
}

/* End of file : drv_rs.c */
