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
#include "driverRS.h"

/**********************************************************************
 * Definition dedicated to the local functions.
 **********************************************************************/

/**********************************************************************
 * ----------------- RS DRIVER - PIC18 USART MANAGEMENT ---------------
 *
 * void
 *
 **********************************************************************/

void initRS()
{
  /* Reset USART registers to POR state */
  TXSTA1            = 0; 
  RCSTA1            = 0;
  
  /* Change baud rate here  [cf datasheet]*/
  /* SPBRG = 255; for   9600 [40MHZ only]*/
  /* SPBRG =  21; for 115200 [40MHZ only]*/
  SPBRG1            = 21;
  TXSTA1 = 0x04; //8bit, transmitter disabled, async, highspeed
  RCSTA1 = 0x90; //8bit, receiver enabled, CREN, SPEN
  TRISC  = 0x80; //C7 input rest output
  
  PIR1 = 0;
  PIE1bits.RC1IE    = 1;
  PIE1bits.TX1IE    = 1;
  
  INTCONbits.PEIE = 1;
}

TASK(RSTASK)
{  
    initRS();
    SetRelAlarm(2, 5000, 1000);
    while(1)
    {
        WaitEvent(ALARM_EVENT);
        ClearEvent(ALARM_EVENT);
    }
}

/**********************************************************************
 * Part of the transmiter interrupt.
 *
 * @param  void
 * @return void 
 **********************************************************************/
void RS_TX_INT(void)
{   
  PIE1bits.TXIE = 0;
  LATA ^= 0x04;
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
  LATA^=0x01;
  //LATA = RCREG;
  //LcdPrintData(RCREG, 0, 0);
  return;
}

/* End of file : drv_rs.c */
