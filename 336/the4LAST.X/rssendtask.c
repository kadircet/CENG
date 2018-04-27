#include "define.h"

void RS_TX_INT(void)
{
    TXREG1 = sndbuffer[sidx];
    if(sndbuffer[sidx]=='L')
            {
                if(dir==0)dir=3;
                else dir--;
            }
            else if(sndbuffer[sidx]=='R')
            {
                if(dir==3)dir=0;
                else dir++;
            }
            else if(sndbuffer[sidx]=='M')
            {
                posX+=dx[dir];
                posY+=dy[dir];
                if(posX<0 || posX>8 || posY<0 || posY>8 || (posX%2==0 && posY%2==0))
                {
                    posX-=dx[dir];
                    posY-=dy[dir];
                }
            }
            else if(sndbuffer[sidx]=='A')
            {
                char tmp = (posX+dx[dir])/2+(posY+dy[dir])/2*5;
                if(!((posX+dx[dir])%2 || (posY+dy[dir])%2))
                //if(!(posX+dx[dir]<0 || posX+dx[dir]>8 || posY+dy[dir]<0 || posY+dy[dir]>8 || tmp<0 || tmp>24))
                {
                    if(board[tmp]==3)
                        remTime+=5;
                    if(board[tmp]==2)
                        remTime--;
                    board[tmp]=0xff;
                }
            }
    if(sndbuffer[sidx++]==':')
    {
        while(!TXSTA1bits.TRMT1);
        TXSTA1bits.TXEN=0;
        sidx=0;
        datasent=1;
    }
}

TASK(RSSEND)
{
    char tmp;
    while(1)
    {
        WaitEvent(RSSEND_EVENT);
        ClearEvent(RSSEND_EVENT);
        tmrcnt0=0;
        SetRelAlarm(1, 50, 50);
        while(STATE==_RUNNING)
        {
            WaitEvent(ALARM_EVENT);
            ClearEvent(ALARM_EVENT);
            
            sidx=1;
            sndbuffer[1]=nextAct;
            TXREG1 = sndbuffer[0];
            TXSTA1bits.TXEN=1;
            
            tmrcnt0++;
            if(tmrcnt0%20==0)
            {
                if(remTime>0)
                    remTime--;
            }
        }
        CancelAlarm(1);
    }
}