
#ifndef __LCD
#define __LCD

// LCD module connections
#define LCD_RS              LATB2;      // RS pin for LCD
#define LCD_E               LATB5;      // Enable pin for LCD
#define LCD_Data_Bus_D4     LATD4;
#define LCD_Data_Bus_D5     LATD5;
#define LCD_Data_Bus_D6     LATD6;
#define LCD_Data_Bus_D7     LATD7;

#define LCD_RS_Dir          TRISB2_bit;
#define LCD_E_Dir           TRISB5_bit;
#define LCD_Data_Bus_Dir_D4 TRISD4;     // Data bus bit 4
#define LCD_Data_Bus_Dir_D5 TRISD5;     // Data bus bit 5
#define LCD_Data_Bus_Dir_D6 TRISD6;     // Data bus bit 6
#define LCD_Data_Bus_Dir_D7 TRISD7;     // Data bus bit 7
// End LCD module connections

// Constants
#define E_Delay       1000


// Function Declarations
void WriteCommandToLCD(unsigned char);
void WriteDataToLCD(char);
void InitLCD(void);
void WriteStringToLCD(const char*);
void ClearLCDScreen(void);
void update7Segment(void);


#endif