/**********************************************************************/
/*                                                                    */
/* File name: LCD.c                                             */
/*                                                                    */
/* Since:     2004/11/11                                              */
/*                                                                    */
/* Version:   PICos18 v2.00                                           */
/*            Copyright (C) 2003, 2004, 2005 Pragmatec.               */
/*            LCD driver v1.2                                         */
/*                                                                    */
/* Author:    Designed by Pragmatec S.A.R.L.        www.pragmatec.net */
/*            MONTAGNE Xavier [XM]      xavier.montagne@pragmatec.net */
/*            ROZIER Bertrand [RZR]     bertrand.rozier@pragmatec.net */				
/*                                                                    */
/* Purpose:   HITACHI LCD driver in 8 bits mode                       */
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
/* 2004/11/11 [RZR] Create the original file                          */
/*                                                                    */
/**********************************************************************/

#include "define.h"
#include <p18cxxx.h>
#include <p18f8722.h>
#include "driverLCD.h"

/**********************************************************************
 * ----------------------- LCD USER SETTINGS --------------------------
 **********************************************************************/
#define COLUMN            16              /* Character number per line */
#define LINE              2               /*            Number of line */

#define LCD_ALARM_ID      0               /*     Alarm ID in tascdesc.c*/

/**********************************************************************
 * --------------- LCD function prototypes -----------------
 **********************************************************************/
 
void InitLCD(void);
//void ToggleEpinOfLCD(void);
void Delay_LCD_ms(unsigned int delay);
//void Lcd_refresh(void);
//void WaitState(void);
//void Lcd_position(unsigned char type, unsigned char value);

/**********************************************************************
 * ---------------------- LCD global variables ------------------------
 **********************************************************************/
EventMaskType LCDevent;
unsigned char LCDchar[LINE][COLUMN];

/**********************************************************************
 * ----------------------------- LCD TASK -----------------------------
 **********************************************************************/
TASK(LCDTASK)
{
    unsigned char i, j;
    InitLCD();
    while(1)
    {
        WaitEvent(LCD_EVENT);
        ClearEvent(LCD_EVENT);

        /* GO HOME LCD command */
        //WriteCommandToLCD(0x02);
        for (i = 0; i < LINE; i++)
        {
          for (j = 0; j < COLUMN; j++)
              if(LCDchar[i][j]&0x80)//update char if dirty bit is set
              {
                  LCDchar[i][j]&=0x7f;//clear dirty bit
                  switch(i)
                {
                    case 0:
                        WriteCommandToLCD(0x80+j);
                        break;
                    case 1:
                        WriteCommandToLCD(0xc0+j);
                        break;
                };
                  WriteDataToLCD(LCDchar[i][j]);
              }
        }
    }
    TerminateTask();
    return;
}

/**********************************************************************
 * Print at screen what is on the LCDchar buffer (both lines).
 *
 * @param  void
 * @return void 
 **********************************************************************/
/*void Lcd_position(unsigned char line, unsigned char pos)
{
    switch(line)
    {
        case 0:
            WriteCommandToLCD(0x80+pos);
            break;
        case 1:
            WriteCommandToLCD(0xc0+pos);
            break;
    };
    return;
}*/

/*void Lcd_refresh(void)
{  
    unsigned char i, j;
  for (i = 0; i < LINE; i++)
  {
    for (j = 0; j < COLUMN; j++)
        if(LCDchar[i][j]&0x80)//update char if dirty bit is set
        {
            LCDchar[i][j]&=0x7f;//clear dirty bit
            Lcd_position(i,j);
            WriteDataToLCD(LCDchar[i][j]);
        }
  }
    return;
}*/

/**********************************************************************
 * Write a command or data on the LCD bus.
 *
 * @param  value     IN  value of the data placed on the bus
 * @return void 
 **********************************************************************/
void WriteCommandToLCD(unsigned char Command)
{
    Delay_LCD_ms(1);
    Nop();
    LATBbits.LATB2 = 0;                 // It is a command

    PORTD = PORTD & 0x0F;               // Make Data pins zero
    PORTD = PORTD |(Command&0xF0);      // Write Upper nibble of data
    
    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);

    PORTD = PORTD & 0x0F;               // Make Data pins zero
    PORTD = PORTD | ((Command<<4)&0xF0);// Write Lower nibble of data

    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);
    return;
}

/*void WriteStringToLCD(const char *s)
{
    Delay_LCD_ms(1);
    Nop();
    while(*s)
        WriteDataToLCD(*s++);   // print first character on LCD
    return;
}*/

/**********************************************************************
 * Write the data on the bus (8 bits mode only).
 *
 * @param  c         IN  data written 
 * @return void 
 **********************************************************************/
void WriteDataToLCD(char value)
{
    Delay_LCD_ms(1);
    Nop();
    LATBbits.LATB2 = 1;                 // It is data

    PORTD = PORTD & 0x0F;               // Make Data pins zero
    PORTD = PORTD | (value&0xF0);     // Write Upper nibble of data
    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);

    PORTD = PORTD & 0x0F;               // Make Data pins zero
    PORTD = PORTD | ((value<<4)& 0xF0); // Write Lower nibble of data


    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);
    Nop(); Nop();
    Nop(); Nop();
    return;
}

/**********************************************************************
 * Enable the data present on the bus.
 *
 * @param  void
 * @return void 
 **********************************************************************/
/*void ToggleEpinOfLCD(void)
{
    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);
    return;
}*/

/**********************************************************************
 * Generic routine to create a delay of many milliseconds.
 *
 * @param  delay     IN  time to wait in ms
 * @return void 
 **********************************************************************/
void Delay_LCD_ms(unsigned int delay)
{
  SetRelAlarm(LCD_ALARM_ID, delay, 0);
  while(1)
  {
    WaitEvent(ALARM_EVENT);
    GetEvent(LCDTASK_ID, &LCDevent);
    if (LCDevent & ALARM_EVENT)
    {
      ClearEvent(ALARM_EVENT);
      break;
    }
  }
  return;
}

/**********************************************************************
 * Init phase of the LCD.
 * Do not modify this sequence.
 *
 * @param  void
 * @return void 
 **********************************************************************/
void InitLCD(void)
{
    LATBbits.LATB5  = 0;   // E  = 0
    LATBbits.LATB2  = 0;   // RS = 0
    LATDbits.LATD4  = 0;   // Data bus = 0
    LATDbits.LATD5  = 0;   // Data bus = 0
    LATDbits.LATD6  = 0;   // Data bus = 0
    LATDbits.LATD7  = 0;   // Data bus = 0
    TRISBbits.TRISB5 = 0;   // Make Output
    TRISBbits.TRISB2 = 0;   // Make Output
    TRISDbits.TRISD4 = 0;   // Make Output
    TRISDbits.TRISD5 = 0;   // Make Output
    TRISDbits.TRISD6 = 0;   // Make Output
    TRISDbits.TRISD7 = 0;   // Make Output
    LATBbits.LATB5  = 0;   // E  = 0
    LATBbits.LATB2  = 0;   // RS = 0

        ///////////////// Reset process from datasheet //////////////
    Delay_LCD_ms(15);
    Delay_LCD_ms(15);

	PORTD &= 0x0F;			  // Make Data pins zero
	PORTD |= 0x30;			  // Write 0x3 value on data bus
    
    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);

    Delay_LCD_ms(6);

	PORTD &= 0x0F;			  // Make Data pins zero
	PORTD |= 0x30;			  // Write 0x3 value on data bus
    
    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);

    Delay_LCD_ms(1);
 
	PORTD &= 0x0F;			  // Make Data pins zero
	PORTD |= 0x30;			  // Write 0x3 value on data bus
    
    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);

    Delay_LCD_ms(2);

	PORTD &= 0x0F;			  // Make Data pins zero
	PORTD |= 0x20;			  // Write 0x2 value on data bus
    
    LATBbits.LATB5 = 1;                // Give a pulse on E pin
    Delay_LCD_ms(E_Delay);             // so that LCD can latch the
    LATBbits.LATB5 = 0;                // data from data bus
    Delay_LCD_ms(E_Delay);

    Delay_LCD_ms(2);
  /////////////// Reset Process End ////////////////
	WriteCommandToLCD(0x2C);    //function set  //2C ya da 2D
	WriteCommandToLCD(0x0C);    //display on,cursor off,blink off //OxOC cursor offf
	WriteCommandToLCD(0x01);    //clear display
    return;
}

/**********************************************************************
 * Copy a string of characters into the LCDchar buffer.
 *
 * @param  s         IN  string to copy into the buffer
 * @param  positionX IN  column selection
 * @param  positionY IN  line selection
 * @return void 
 **********************************************************************/
void LcdPrintString(const rom char *s,  unsigned char positionX, unsigned char positionY)
{
    while (*s)
    {
        if(LCDchar[positionY][positionX]^*s)
            LCDchar[positionY][positionX]=0x80|*s;//set dirty bit
        positionX++;
        s++;
    }
    return;
}

/**********************************************************************
 * Convert a data (8 bits only) into a string of 2 characters.
 *
 * @param  value     IN  Value converted in string of characters
 * @param  positionX IN  column selection
 * @param  positionY IN  line selection
 * @return void
 **********************************************************************/
void LcdPrintChar(unsigned char value, unsigned char positionX, unsigned char positionY)
{
    if(LCDchar[positionY][positionX]^value)
        LCDchar[positionY][positionX] = value|0x80; // set dirty bit
    return;
}

void LcdPrintData(unsigned char value, unsigned char positionX, unsigned char positionY)
{
    if(LCDchar[positionY][positionX]^(value%10+'0'))
        LCDchar[positionY][positionX] = 0x80|(value%10+'0');
    positionX--;
    if(LCDchar[positionY][positionX]^(value/10+'0'))
        LCDchar[positionY][positionX] = 0x80|(value/10+'0');
    return;
}

/**********************************************************************
 * Wait until LCD is not busy
 *
 * @param  void
 * @return void 
 **********************************************************************/
/*void WaitState(void)
{
    Delay_LCD_ms(1);
    return;
}*/

/* End of File : LCD.c */


