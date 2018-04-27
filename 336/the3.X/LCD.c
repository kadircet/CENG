#include <p18cxxx.h>
#include <p18f8722.h>
#include "LCD.h"
#include "Includes.h"

void ToggleEpinOfLCD(void)
{
    LATB5 = 1;                // Give a pulse on E pin
    //__delay_us(E_Delay);      // so that LCD can latch the
    update7Segment();
    LATB5 = 0;                // data from data bus
    update7Segment();
    //__delay_us(E_Delay);
}

void WriteCommandToLCD(unsigned char Command)  
{
    LATB2 = 0;                          // It is a command

    PORTD = PORTD & 0x0F;               // Make Data pins zero
    PORTD = PORTD |(Command&0xF0);      // Write Upper nibble of data
    ToggleEpinOfLCD();                  // Give pulse on E pin

    PORTD = PORTD & 0x0F;               // Make Data pins zero
    PORTD = PORTD | ((Command<<4)&0xF0);// Write Lower nibble of data

    ToggleEpinOfLCD();                  // Give pulse on E pin
}

void WriteDataToLCD(char LCDChar)  
{
    LATB2 = 1;                          // It is data

    PORTD = PORTD & 0x0F;               // Make Data pins zero
    PORTD = PORTD | (LCDChar&0xF0);     // Write Upper nibble of data
    ToggleEpinOfLCD();                  // Give pulse on E pin

    PORTD = PORTD & 0x0F;               // Make Data pins zero
    PORTD = PORTD | ((LCDChar<<4)& 0xF0); // Write Lower nibble of data


    ToggleEpinOfLCD();                  // Give pulse on E pin
}

void InitLCD(void)
{
    LATB5  = 0;   // E  = 0
    LATB2  = 0;   // RS = 0
    LATD4  = 0;   // Data bus = 0
    LATD5  = 0;   // Data bus = 0
    LATD6  = 0;   // Data bus = 0
    LATD7  = 0;   // Data bus = 0
    TRISB5 = 0;   // Make Output
    TRISB2 = 0;   // Make Output
    TRISD4 = 0;   // Make Output
    TRISD5 = 0;   // Make Output
    TRISD6 = 0;   // Make Output
    TRISD7 = 0;   // Make Output
    LATB5  = 0;   // E  = 0
    LATB2  = 0;   // RS = 0

        ///////////////// Reset process from datasheet //////////////
    __delay_ms(15);
    __delay_ms(15);

	PORTD &= 0x0F;			  // Make Data pins zero
	PORTD |= 0x30;			  // Write 0x3 value on data bus
	ToggleEpinOfLCD();		  // Give pulse on E pin
    
    __delay_ms(6);

	PORTD &= 0x0F;			  // Make Data pins zero
	PORTD |= 0x30;			  // Write 0x3 value on data bus
	ToggleEpinOfLCD();		  // Give pulse on E pin

   __delay_us(300);

	PORTD &= 0x0F;			  // Make Data pins zero
	PORTD |= 0x30;			  // Write 0x3 value on data bus
	ToggleEpinOfLCD();		  // Give pulse on E pin

   __delay_ms(2);

	PORTD &= 0x0F;			  // Make Data pins zero
	PORTD |= 0x20;			  // Write 0x2 value on data bus
	ToggleEpinOfLCD();		  // Give pulse on E pin

	__delay_ms(2);
  /////////////// Reset Process End ////////////////
	WriteCommandToLCD(0x2C);    //function set  //2C ya da 2D
	WriteCommandToLCD(0x0C);    //display on,cursor off,blink off //OxOC cursor offf
	WriteCommandToLCD(0x01);    //clear display


}

void WriteStringToLCD(const char *s)
{
    while(*s)
        WriteDataToLCD(*s++);   // print first character on LCD
}

void ClearLCDScreen(void)       // Clear the Screen and return cursor to zero position
{
    WriteCommandToLCD(0x01);    // Clear the screen
    __delay_ms(2);              // Delay for cursor to return at zero position
}