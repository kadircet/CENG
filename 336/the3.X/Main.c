#include <p18cxxx.h>
#include <p18f8722.h>
#pragma config OSC = HSPLL, FCMEN = OFF, IESO = OFF, PWRT = OFF, BOREN = OFF, WDT = OFF, MCLRE = ON, LPT1OSC = OFF, LVP = OFF, XINST = OFF, DEBUG = OFF

#define _XTAL_FREQ   40000000

#include "Includes.h"
#include "LCD.h"

const unsigned char *cats[] = 
{
    "Beverages ", 
    "Snack Food", 
    "Fast Food ", 
    "Deserts   "
};
const unsigned char *foods[4][4] = 
{
    {"Water ", "Milk ", "Coke ", "Tea "},
    {"Seeds ", "Nuts ", "Chips ", "PCorn "},
    {"Pizza ", "Hambu ", "Sandw ", "Crack "},
    {"Cake ", "Choc ", "ICrea ", "Biscu "}
};
unsigned char counts[4][4] = 
{
    {9,2,3,1},
    {3,1,2,3},
    {2,3,1,0},
    {3,1,9,2}
};
unsigned char prices[4][4] = 
{
    {2,3,5,4},
    {5,4,3,5},
    {3,5,4,4},
    {5,4,2,3}
};

unsigned char tmr0cnt = 0, tmr0cnt2 = 0, tmr1cnt = 0, tmr1cnt2 = 0;
unsigned char _7seg[4], it, error=0, oldcurc=5, oldcurf=5, state2f;
unsigned char toggled[2], pressed[2], blinks=0;
unsigned int pot;
unsigned long i,j;
unsigned char pid, curl=0, curc=0, curf=0, curs=0, credit=0;

void interrupt high_isr(void)
{
    if(TMR1IE && TMR1IF)
    {
        tmr1cnt++;
        if(tmr1cnt==20)
        {
            tmr1cnt=0;
            tmr1cnt2++;
        }
        if(tmr1cnt%5==0)
            blinks^=1;
        TMR1L = 220;
        TMR1H = 11;
        TMR1IF = 0;
    }
    if(T0IE && T0IF)
    {
        tmr0cnt++;
        if(tmr0cnt==18)
        {
            tmr0cnt=0;
            tmr0cnt2++;
            GO = 1;
        }
        TMR0 = 39;
        T0IF = 0;
    }
    if(RBIE && RBIF)
    {
        if(pressed[0] && RB6 && !toggled[0])
        {
            toggled[0]=1;
            pressed[0]=0;
        }
        if(pressed[1] && RB7 && !toggled[1])
        {
            toggled[1]=1;
            pressed[1]=0;
        }
        if(!RB6 && !toggled[0])
            pressed[0]=1;
        if(!RB7 && !toggled[1])
            pressed[1]=1;
        RBIF = 0;
    }
    if(ADIE && ADIF)
    {
        ADIF = 0;
        pot = ADRESH<<8 | ADRESL;
    }
}

void setup(void)
{
    GIE = 0;    //disable interrupts
    
    TRISE1 = 1;  //only re1 is input
    
    INTCON = 0;     //clear intcon
    TRISB = 0;
    TRISB6 = 1;     //rb6 and 7 is input, rest is output
    TRISB7 = 1;
    RBIE = 1;   //enable rb interrupts
    RBPU = 0;   //enable pullups
    RBIF = 0;   //clear rb interrupt flag
    
    TMR0IE = 1;     //enable timer0
    T0CON = 0;      // clear t0con
    T08BIT = 1;     // make t0 8bit
    PSA = 0;        // enable prescaler
    T0PS0 = T0PS1 = T0PS2 = 1;  //prescaler : 256
    TMR0 = 39;     //offset tmr0 by 39, we will count 18 times
    T0IF = 0;       //clear tmr0 flag
    TMR0ON = 1;     //tmr0 is on
    
    TMR1IE = 1;     //enable tmr1
    TMR1L = 220;//preload with 3036 so that we have 8*62500=500000clockcycles(50ms)
    TMR1H = 11;//62500=256*256-3036
    T1CKPS1 = T1CKPS0 = 1; //prescaler is 8
    TMR1ON = 1;     //tmr1 is on

    CHS3 = CHS2 = 1;    //set channel 12
    ADIE = 1;       //enable ad interrupt
    ACQT2 = 1;      //set acq time to
    ACQT1 = 0;      //12 TADs since we convert analog input to 10 bits
    ACQT0 = 1;      //we need at least 11TADs
    ADIF = 0;       //clear interrupt flag
    ADFM = 1;       //make it right justify
    ADON = 1;       //make ad on
    
    TRISH = 0xf0;   //make rh7-4 input rest output, input for analog, output for led segment and lcd disp
    TRISJ = 0;      //trisj is all output, for led segments
    
    GIE = 1;    //enable interrupts
}

unsigned char getDisp(unsigned char c)
{
    switch(c)
    {
        case 0: return 0b00111111;  // 7-Segment = 0
        case 1: return 0b00000110;  // 7-Segment = 1
        case 2: return 0b01011011;  // 7-Segment = 2
        case 3: return 0b01001111;  // 7-Segment = 3
        case 4: return 0b01100110;  // 7-Segment = 4
        case 5: return 0b01101101;  // 7-Segment = 5
        case 6: return 0b01111101;  // 7-Segment = 6
        case 7: return 0b00000111;  // 7-Segment = 7
        case 8: return 0b01111111;  // 7-Segment = 8
        case 9: return 0b01100111;  // 7-Segment = 9
        case '-': return 1<<6;      // only the dash segment
    }
    return 0;   // all segments off
}

void clear7Segment()
{
    for(i=0;i<4;i++)
        _7seg[i]='-';//set all segments to dash
}

void update7Segment()
{
    it=1;//update every character of 7segment by iterating over the bits of TRISH
    for(i=0;i<4;i++)//we use last 4 bits
    {
        PORTJ = 0;  //clear portj first
        PORTH |= (it&0x0f); //then select appropriate bit in porth
        PORTJ = getDisp(_7seg[3-i]);    //set portj to appropriate representation
        __delay_us(500);            //wait for 500microsec 
        PORTH &= 0xf0;  //clear porth
        it <<= 1;   // go to next bit
    }
}

void state0(void)
{
    if(curl==0)
    {
        curc = pot/250;
        if(curc>3)curc=3;
        curf = 0;
        toggled[0]=0;
    }
    else
    {
        curf = pot/250;
        if(curf>3)curf=3;
        if(oldcurf!=curf)
            error=0;
    }
    pid = (curc<<2)+curf+1; //calculate product id by multiplying category by 4 and add curfood id and 1 offset
    WriteCommandToLCD((curl)?0xC0:0x80);
    if(error)   //blink the cursor, if errornous select was detected
        WriteDataToLCD(blinks?'X':' '); //display X 
    else    //otherwise display normal cursor
        WriteDataToLCD(blinks?'>':' ');
    if(oldcurc!=curc)
    {
        WriteCommandToLCD(0x81);
        WriteStringToLCD(cats[curc]);
    }
    if(oldcurf!=curf || oldcurc!=curc)
    {
        WriteCommandToLCD(0xC1);
        WriteDataToLCD('0'+pid/10);
        WriteDataToLCD('0'+pid%10);
        WriteDataToLCD('-');
        WriteStringToLCD(foods[curc][curf]);
        WriteStringToLCD("C:");
        WriteDataToLCD('0'+counts[curc][curf]);
        WriteStringToLCD("$:");
        WriteDataToLCD('0'+prices[curc][curf]);
        WriteStringToLCD("  ");
    }
    oldcurc=curc;
    oldcurf=curf;
    if(toggled[1])
    {
        WriteCommandToLCD((curl==0)?0x80:0xC0);
        WriteDataToLCD(' ');
        curl ^=1;
        toggled[1]=0;
        error=0;
    }
    if(toggled[0] && curl)
    {
        toggled[0]=0;
        if(counts[curc][curf]==0)
        {
            error=1;
            return;
        }
        RBIE=0;
        WriteCommandToLCD(0x80);
        WriteDataToLCD(' ');
        _7seg[3]=_7seg[2]=0;
        _7seg[1]=pid/10;
        _7seg[0]=pid%10;
        curs=curl=1;
        toggled[1]=0;
        TMR1L = 220;
        TMR1H = 11;
        tmr1cnt=tmr1cnt2=0;
    }
}

void state1(void)
{
    WriteCommandToLCD(0xC0);
    WriteDataToLCD(blinks?'>':' '); //blinks is toggled every 250ms in timer1isr
    
    if(tmr1cnt<10)  //  toggle the d0 and d1 displays once every 500ms
    {
        _7seg[1]=pid/10;
        _7seg[0]=pid%10;
    }
    else
    {
        _7seg[1]='x';
        _7seg[0]='x';
    }
    
    if(tmr1cnt2>=3) //  after 3 seconds go to next state
    {
        WriteCommandToLCD(0xC0);    
        WriteDataToLCD(' ');    // clear the cursor
        curs=2; // set next state
        _7seg[1]=((curc<<2)+curf+1)/10; //make sure 7segment is in the correct
        _7seg[0]=((curc<<2)+curf+1)%10; //state, not the clear state
        tmr1cnt=tmr1cnt2=0;     //reset tmr1 state
        TMR1L = 220;    //reset tmr1 state
        TMR1H = 11; //reset tmr1 state
        state2f=0;  //clear state flag
    }
}

void state2(void)
{
    WriteCommandToLCD(0x80);
    WriteDataToLCD(blinks?'>':' ');//blinks is toggled every 250ms in timer1isr
    if(state2f==0)  // if its our first entry to that state update the lcd
    {
        WriteStringToLCD("Enter Credits  ");
        WriteCommandToLCD(0xC1);
        WriteDataToLCD('0'+pid/10);
        WriteDataToLCD('0'+pid%10);
        WriteDataToLCD('-');
        WriteStringToLCD(foods[curc][curf]);
        WriteStringToLCD("C:");
        WriteDataToLCD('0'+counts[curc][curf]);
        WriteStringToLCD("$:");
        WriteDataToLCD('0'+prices[curc][curf]);
        WriteStringToLCD("  ");
        state2f=1;  // set the flag
    }
    if(tmr1cnt<10) //blink every 500ms, since tmr1cnt increments every 50ms
        _7seg[3] = 0;   // we either display zero
    else
        _7seg[3]='x';   // or clear the screen
    
    if(tmr1cnt2>=4) //after 4 seconds go to next state
    {
        WriteCommandToLCD(0xC0);    // clear the cursor
        WriteDataToLCD(' ');        // clear the cursor
        curs=3;     // set next state
        tmr1cnt=tmr1cnt2=0;     //clear tmr1 state
        TMR1L = 220;    //clear tmr1 state
        TMR1H = 11; //clear tmr1 state
        RBIE = 1;       // enable rb interrupts
    }
}

void state3(void)
{
    WriteCommandToLCD(0x80);
    WriteDataToLCD(blinks?'>':' ');//blinks is toggled every 250ms in timer1isr
    credit = pot/102;   //read pot value, divide it by 102 intervals, pot is updated by ad interrupt
    if(credit>9)credit=9;   // limit the maximal value
    
    _7seg[3] = credit;  //update 7segment to show current credit value
    
    if(toggled[0])  // if rb6 is pressed and released, checked by rb interrupt
    {
        curs=4;             // will go to next state
        counts[curc][curf]--;   // decrement count of the selected item
        credit-=prices[curc][curf]; // calculate remaining credit
        TMR0 = 39;      // set tmr0 state
        tmr0cnt = tmr0cnt2 = 0; // set tmr0 state
        TMR1L = 220;    // set tmr1 state
        TMR1H = 11;     // set tmr1 state
        tmr1cnt=tmr1cnt2=0; // set tmr1 state
        state2f=0;  // clear flag
    }
}

void state4(void)
{
    WriteCommandToLCD(0x80);
    WriteDataToLCD(blinks?'>':' ');     //blinks is toggled every 250ms in timer1isr
    if(state2f==0)      // if its our first entry to that state update the lcd
    {
        WriteStringToLCD(cats[curc]);
        WriteStringToLCD("   ");
        WriteCommandToLCD(0xC1);
        WriteDataToLCD('0'+pid/10);
        WriteDataToLCD('0'+pid%10);
        WriteDataToLCD('-');
        WriteStringToLCD(foods[curc][curf]);
        WriteStringToLCD("C:");
        WriteDataToLCD('0'+counts[curc][curf]);
        WriteStringToLCD("$:");
        WriteDataToLCD('0'+prices[curc][curf]);
        WriteStringToLCD("  ");
        state2f=1;      // set the flag
    }
    if((tmr0cnt2/5)&1)      //blink every 500ms, since timer0cnt increments every
        _7seg[2] = 'x';  //100ms, at every 5 increment our last bit will change
    else                    //and according to that we will toggle, 'x' is special
        _7seg[2]=credit;    // terminator for clearing out that display
    
    if(tmr0cnt2>=30) // we increment it every 100ms, 30 times means 3seconds
    {               // go to next state
        tmr0cnt2=0; // clear timercnt
        RBIE = 0;   // disable rb interrupts
        curl=0;     // clear state variable
        curs=5;     // go to begining of the machine
    }
}

void state5(void)
{
    ClearLCDScreen();           // Clear LCD screen
    clear7Segment();            // clear 7segment to all dashes
    
    while(RE1)                  //wait for re1 press & release
        update7Segment();
    while(!RE1)
        update7Segment();
    
    WriteCommandToLCD(0x82);            // dipslay the intro text
    WriteStringToLCD("Ceng Vending");
    WriteCommandToLCD(0xC1);
    WriteStringToLCD("$$$Machine$$$");
    for(j=0;j<245UL*6;j++)// wait for 3 seconds, and while waiting
        update7Segment(); // keep updating the 7segment so it does not flicker
    ClearLCDScreen();       // clear lcd for next state
    curs=0;                 // next state is 0
    tmr0cnt=tmr0cnt2=tmr1cnt=tmr1cnt2=0;    // clear timers
    toggled[0]=toggled[1]=0;       //clear button states
    RBIE = 1;               // enable rb interrupts
    oldcurf=oldcurc=5;      // initialization of variables to detect pot changes
}

// Main Function
void main(void)
{
    InitLCD();			// Initialize LCD in 4bit mode
    setup();
    curs=5;             // we start with state=5
    while(1)            // we update our external environment
    {                   // and process sensors in a round robin fashion
        update7Segment();   // we update the 7segment so it does not flicker
        switch(curs)        // and give a smooth display
        {
            case 0:
                state0();
                break;
            case 1:
                state1();
                break;
            case 2:
                state2();
                break;
            case 3:
                state3();
                break;
            case 4:
                state4();
                break;
            case 5:
                state5();
                break;
        }
    }
}