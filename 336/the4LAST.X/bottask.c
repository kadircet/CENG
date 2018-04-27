#include "define.h"

unsigned char dataReady;
unsigned char datasent;
unsigned char rcvbuffer[BUFSIZE];
unsigned char ridx;
unsigned char sndbuffer[BUFSIZE/2];
unsigned char sidx;
unsigned char STATE;
unsigned char duration[4];
unsigned char _posTx[4];
unsigned char newPoke;

char posX;
char posY;
char T[4];
char dir;
unsigned char board[25];
unsigned char remTime;
unsigned int disTime[25];
unsigned int tmrcnt0;
char dx[4] = {0,1,0,-1};
char dy[4] = {-1,0,1,0};
unsigned char nextAct;

void updateLcd();

void initBot()
{
    unsigned char i;
    duration[0]=15;
    duration[1]=10;
    duration[2]=15;
    duration[3]=30;
    
    sndbuffer[0] = '$';
    sndbuffer[1] = 'A';
    sndbuffer[2] = ':';
    nextAct='A';
    
    TRISA = 0;
    for(i=0;i<25;i++)
    {
        disTime[i]=0;
        board[i]=0xff;
    }
    
    ridx = 0;
    sidx = 0;
    STATE=_WAITING;
    
    _posTx[0]=14;
    _posTx[1]=4;
    _posTx[2]=10;
    _posTx[3]=15;
    dataReady = 0;
  
    /* Change baud rate here  [cf datasheet]*/
  /* SPBRG = 255; for   9600 [40MHZ only]*/
  /* SPBRG =  21; for 115200 [40MHZ only]*/
  SPBRG1            = 21;
  TXSTA1 = 0x04; //8bit, transmitter disabled, async, highspeed
  RCSTA1 = 0x90; //8bit, receiver enabled, CREN, SPEN
  TRISC  = 0x80; //C7 input rest output
  
  PIR1 = 0;
  PIE1bits.RC1IE    = 1;
  //PIE1bits.TX1IE    = 1;
  
  INTCONbits.PEIE = 1;
}

/*void RS_RX_INT(void)
{
    unsigned char dt;
  PIR1bits.RC1IF = 0;
  dt = RCREG1;
  if(dt=='$')
      ridx=0;
  rcvbuffer[ridx++]=dt;
  if(dt==':')
      dataReady=1;
}*/

void parse()
{
    unsigned char t,I;
    switch(rcvbuffer[1])
    {
        case 'G':
            STATE = _RUNNING;
            tmrcnt0=0;
            remTime = 20;
            dir = 2;
            posX = 0;
            posY = 1;
            T[0]=T[1]=T[2]=T[3]=0;
            for(t=0;t<25;t++)
            {
                disTime[t]=0;
                board[t]=0xff;
            }
            LcdPrintString(" Time:20  T0:00 ",0,0);
            LcdPrintString("T1:00 T2:00T3:00",0,1);
            SetEvent(RSSEND_ID, RSSEND_EVENT);
            break;
        case 'D':
            t = rcvbuffer[3]-'0';
            T[t]++;
            I = (rcvbuffer[4]-'0')*16;
            if( rcvbuffer[5] <= '9')
                I += rcvbuffer[5]-'0';
            else
                I += rcvbuffer[5]-'a'+10;
            board[I] = t;
            disTime[I] = duration[t]+tmrcnt0;
            newPoke=1;
            break;
        case 'E':
            STATE = _WAITING;
            remTime = 0;
            updateLcd();
            break;
    }
}

void updateLcd()
{
    unsigned char i;
    if(STATE==_RUNNING)
    {
        LcdPrintData(remTime, 7, 0);
        for(i=0;i<4;i++)
            LcdPrintData(T[i], _posTx[i], i>0);
    }
    else
    {
        LcdPrintString("X   CENG 336   X", 0, 0);
        LcdPrintString("X  CENG HUNTER X", 0, 1);
    }
    //SetEvent(LCDTASK_ID, LCD_EVENT);
}

unsigned char lastidx, best;
#define ABS(X) ((X)>0?(X):-(X))
void findMove()
{
    char i,k,x,y,cur,j,_dy,_dx;
    if(lastidx%25==0)
    {
        best=50;
        lastidx=0;
    }
    for(k=0;k<5;k++)
    {
        i=(lastidx++)%25;
        if(board[i]!=0xff && tmrcnt0+1<disTime[i] && board[i]!=2)
        {
            y=(i/5)<<1;
            x=(i%5)<<1;
            
            _dy=y-posY;
            if(y<posY)
                _dy=posY-y;
            _dx=x-posX;
            if(x<posX)
                _dx=posX-x;
            cur = _dx+_dy;
            //cur=ABS(y-posY)+ABS(x-posX);
            if(cur==1)
            {
                for(j=0;j<4;j++)
                    if(posY+dy[j]==y && posX+dx[j]==x)
                        break;
                cur=j-dir;
                if(j<dir)
                    cur=dir-j;
                //cur=ABS(j-dir);
                //if(cur==3)
                //    cur=1;
                x=posX;
                y=posY;
            }
            else
            {
                if(_dy==1)
                    y=posY;
                else if(_dx==1)
                    x=posX;
                else
                {
                    if(y==posY)
                    {
                        y--;
                        if(y<0)
                            y=1;
                    }
                    else
                    {
                        x--;
                        if(x<0)
                            x=1;
                    }
                }
                _dy=y-posY;
                if(y<posY)
                    _dy=posY-y;
                _dx=x-posX;
                if(x<posX)
                    _dx=posX-x;
                cur=_dy+_dx;//+1;
                //if(y!=posY && x!=posX)
                //    cur++;
            }
            //if(tmrcnt0+cur>=disTime[i]+2)
            //    continue;
            if(cur<best || board[i]==3)// || (board[i]==1 && bestt==0))
            {
                best=cur;
                if(board[i]==3)
                    best=0;
                nextAct='A';
                if(x==posX && y==posY)
                {
                    if(j<dir)
                    {
                        nextAct='L';
                        if(j==0&&dir==3)
                            nextAct='R';
                    }
                    else if(j>dir)
                    {
                        nextAct='R';
                        if(j==3&&dir==0)
                            nextAct='L';
                    }
                }
                else if(x==posX)
                {
                    if(y<posY)
                    {
                        nextAct='L';
                        if(dir==0)
                            nextAct='M';
                        else if(dir==3)
                            nextAct='R';
                    }
                    else if(y>posY)
                    {
                        nextAct='L';
                        if(dir==2)
                            nextAct='M';
                        else if(dir==1)
                            nextAct='R';
                    }
                }
                else if(y==posY)
                {
                    if(x<posX)
                    {
                        nextAct='L';
                        if(dir==3)
                            nextAct='M';
                        else if(dir==2)
                            nextAct='R';
                    }
                    else if(x>posX)
                    {
                        nextAct='L';
                        if(dir==1)
                            nextAct='M';
                        else if(dir==0)
                            nextAct='R';
                    }
                }
                else
                {
                    if(posY%2)
                    {
                        if(x<posX)
                        {
                            nextAct='L';
                            if(dir==3)
                                nextAct='M';
                            else if(dir==2)
                                nextAct='R';
                        }
                        else if(x>posX)
                        {
                            nextAct='L';
                            if(dir==1)
                                nextAct='M';
                            else if(dir==0)
                                nextAct='R';
                        }
                    }
                    else
                    {
                        if(y<posY)
                        {
                            nextAct='L';
                            if(dir==0)
                                nextAct='M';
                            else if(dir==3)
                                nextAct='R';
                        }
                        else if(y>posY)
                        {
                            nextAct='L';
                            if(dir==2)
                                nextAct='M';
                            else if(dir==1)
                                nextAct='R';
                        }
                    }
                }
            }
            if(board[i]==3)
                break;
        }
    }
}

TASK(BOTTASK)
{  
    unsigned char i;
    initBot();
    updateLcd();
    while(1)
    {
        if(dataReady)
        {
            dataReady=0;
            parse();
        }
        if(TXSTA1bits.TXEN==0)// && tmrcnt0%20==0)
        {
            //newPoke=0;
            findMove();
            if(STATE==_RUNNING)
            {
                LcdPrintData(remTime, 7, 0);
                for(i=0;i<4;i++)
                    LcdPrintData(T[i], _posTx[i], i>0);
            }
            else
            {
                LcdPrintString("X   CENG 336   X", 0, 0);
                LcdPrintString("X  CENG HUNTER X", 0, 1);
            }
            SetEvent(LCDTASK_ID, LCD_EVENT);
        }
        
    }
}