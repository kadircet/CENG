/**********************************************************************/
/*                                                                    */
/* File name: hd4478_drv.h                                            */
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
/* Purpose:   Hitachi LCD driver header                               */
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

//#ifndef _HD4478_DRV_h_
//#define _HD4478_DRV_h_
//
//
//void LcdPrintString(const rom char *s,
//                      unsigned char positionX,
//                      unsigned char positionY);
//void LcdPrintData(unsigned char value,
//                     unsigned char positionX,
//                     unsigned char positionY);
//void LcdClear(void);
//
//
//#endif

#ifndef __LCD
#define __LCD

// LCD module connections
#define LCD_RS              LATBbits.LATB2;      // RS pin for LCD
#define LCD_E               LATBbits.LATB5;      // Enable pin for LCD
#define LCD_Data_Bus_D4     LATDbits.LATD4;
#define LCD_Data_Bus_D5     LATDbits.LATD5;
#define LCD_Data_Bus_D6     LATDbits.LATD6;
#define LCD_Data_Bus_D7     LATDbits.LATD7;

#define LCD_RS_Dir          TRISB2_bit;
#define LCD_E_Dir           TRISB5_bit;
#define LCD_Data_Bus_Dir_D4 TRISDbits.TRISD4;     // Data bus bit 4
#define LCD_Data_Bus_Dir_D5 TRISDbits.TRISD5;     // Data bus bit 5
#define LCD_Data_Bus_Dir_D6 TRISDbits.TRISD6;     // Data bus bit 6
#define LCD_Data_Bus_Dir_D7 TRISDbits.TRISD7;     // Data bus bit 7
// End LCD module connections

// Constants
#define E_Delay       1


// Function Declarations
void WriteCommandToLCD(unsigned char);
void WriteDataToLCD(char);
void InitLCD(void);
void WriteStringToLCD(const char*);
void ClearLCDScreen(void);


#endif

/* End of file : lcd_drv.h */
