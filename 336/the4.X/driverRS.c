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
#define BUFSIZE 8

/**********************************************************************
 * Definition dedicated to the local functions.
 **********************************************************************/

#define _WAITING 0
#define _RUNNING 1
unsigned char dataReady=0;
unsigned char rcvbuffer[BUFSIZE], rcvf, rcve;
unsigned char sndbuffer[BUFSIZE], sndf, snde;
unsigned char STATE, duration[4];
//unsigned short tmrcnt0;
char posX, posY, T[4], dir;
unsigned char board[9][9], disTime[9][9], tmrcnt0, tmrcnt1;
unsigned char _posTx[4];
unsigned char remTime;
unsigned char t,I,X,Y;
char dx[4] = {0,1,0,-1};
char dy[4] = {-1,0,1,0};

void initBot()
{
    unsigned char i,j;
    duration[0]=15;
    duration[1]=10;
    duration[2]=15;
    duration[3]=30;
    
    sndbuffer[0] = '$';
    sndbuffer[1] = 'A';
    sndbuffer[2] = ':';
    dir = 2;
    TRISA = 0;
    for(i=0;i<9;i++)
        for(j=0;j<9;j++)
        {
            disTime[i][j]=0;
            board[i][j]=0xff;
        }
            
    rcvf = rcve = 0;
    sndf = snde = 0;
    STATE=_WAITING;
    posX = 0;
    posY = 1;
    T[0]=T[1]=T[2]=T[3]=0;
    _posTx[0]=14;
    _posTx[1]=4;
    _posTx[2]=10;
    _posTx[3]=15;
    dataReady = 0;
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

TASK(RSSEND)
{
    unsigned char i,j;
    while(1)
    {
        WaitEvent(RSSEND_EVENT);
        ClearEvent(RSSEND_EVENT);
        tmrcnt0=0;
        tmrcnt1=0;
        SetRelAlarm(1, 50, 50);
        while(STATE==_RUNNING)
        {
            WaitEvent(ALARM_EVENT);
            ClearEvent(ALARM_EVENT);
            
            sndf=1;
            TXREG1 = sndbuffer[0];
            //if(sndf==BUFSIZE)
            //    sndf=0;
            TXSTA1bits.TXEN=1;
            
            tmrcnt0++;
            if(tmrcnt0%5 == 0)
                tmrcnt1++;
            if(tmrcnt0%20==0)
            {
                if(remTime>0)
                    remTime--;
            }
            if(tmrcnt0==240)
                tmrcnt0=0;
            for(i=0;i<9;i++)
                for(j=0;j<9;j++)
                    if(board[i][j]!=0xff && disTime[i][j]>0)
                        disTime[i][j]--;
        }
        CancelAlarm(1);
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
    SetEvent(LCDTASK_ID, LCD_EVENT);
}

void parse()
{
    if(dataReady)
    {
        //rcvf=1;
        dataReady=0;
        /*if(rcvbuffer[rcvf]!='$')
            while(rcvbuffer[++rcvf]!='$')
                if(rcvf==BUFSIZE)rcvf=0;
        rcvf++;
        if(rcvf==BUFSIZE)rcvf=0;*/
        if(rcvbuffer[1]=='G')
        {
            //rcvf+=3;
            remTime=20;
            STATE=_RUNNING;
            LcdPrintString(" Time:20  T0:00 ",0,0);
            LcdPrintString("T1:00 T2:00T3:00",0,1);
            SetEvent(RSSEND_ID, RSSEND_EVENT);
        }
        else if(rcvbuffer[1]=='D')
        {
            LATA^=0x1;
            //rcvf+=2;
            //if(rcvf>=BUFSIZE)rcvf-=BUFSIZE;
            t=(rcvbuffer[3]-'0');
            //while(GetResource(0)!=E_OK);
            T[t]++;
            //ReleaseResource(0);
            //if(rcvf==BUFSIZE)rcvf=0;
            I=(rcvbuffer[4]-'0')*16;
            //if(rcvf==BUFSIZE)rcvf=0;
            if(rcvbuffer[5]<='9')
                I+=(rcvbuffer[5]-'0');
            else
                I+=(rcvbuffer[5]-'a')+10;
            X=(I%5);
            Y=((I-X)/5)<<1;
            X<<=1;
            //while(GetResource(0)!=E_OK);
            board[X][Y]=t;
            disTime[X][Y]=/*tmrcnt1+*/duration[t];
            //ReleaseResource(0);
        }
        else if(rcvbuffer[1]=='E')
        {
            STATE=_WAITING;
            remTime=0;
            //rcvf+=4;
            //if(rcvf>=BUFSIZE)rcvf-=BUFSIZE;
            
            for(X=0;X<4;X++)
                T[X]=0;
            for(X=0;X<9;X++)
                for(Y=0;Y<9;Y++)
                {
                    board[X][Y]=0xff;
                    disTime[X][Y]=0;
                }
            dir=2;
            posX=0;
            posY=1;
            tmrcnt0=0;
        }
    }
}

void findMove()
{
    unsigned char i,j;
    char dist = 0, bdist=50;
    for(i=0;i<9;i++)
        for(j=0;j<9;j++)
        {
            t=board[i][j];
            if(t!=0xff && 0<disTime[i][j] && t != 2)
            {
                if(i>posX)
                    dist=i-posX;
                else
                    dist = posX-i;
                if(j>posY)
                    dist+=j-posY;
                else
                    dist+=posY-j;
                //dist+=2;
                //dist=ABS(posX-i) + ABS(posY-j)+2;
                if((dist<bdist && (posX != i && posY != j)) || dist < 2)
                {
                    bdist=dist;
                    
                    if(dist < 2)
                    {
                        if(posX+dx[dir]==i && posY+dy[dir]==j)
                        {
                            sndbuffer[1]='A';
                        }
                        else
                        {
                            sndbuffer[1]='L';
                        }
                        return;
                    }
                    
                    //LATA^=0xff;
                    //sndbuffer[1] = 'M';
                    if((dir==0&& j<posY)||(dir==2&&posY<j)||
                            (dir==3&&i<posX)||(dir==1&&posX<i))
                        sndbuffer[1]='M';
                    /*else if(i<posX)
                        sndbuffer[1]='L';*/
                    else
                        sndbuffer[1]='L';
                    //LcdPrintChar(sndbuffer[1],0,0);
                    
                    if(posX+dx[dir]==i && posY+dy[dir]==j)
                        sndbuffer[1]='A';                        
                    else if(sndbuffer[1]=='M' && ((posX+dx[dir])%2==0 && (posY+dy[dir])%2==0))
                        sndbuffer[1]='L';
                    else if(sndbuffer[1]=='M' && (posX+dx[dir]<0 || posY+dy[dir]<0 || posX+dx[dir]>8 || posY+dy[dir]>8))
                        sndbuffer[1]='L';
                }
            }
        }
}

TASK(BOTTASK)
{  
    initBot();
    while(1)
    {
        parse();
        findMove();
        updateLcd();
    }
}

void RS_TX_INT(void)
{
    TXREG1 = sndbuffer[sndf];
    if(sndbuffer[sndf]=='L')
    {
        if(dir==0)dir=3;
        else dir--;
    }
    else if(sndbuffer[sndf]=='R')
    {
        if(dir==3)dir=0;
        else dir++;
    }
    else if(sndbuffer[sndf]=='M')
    {
        posX+=dx[dir];
        posY+=dy[dir];
        if(posX>8)
            posX=8;
        else if(posX<0)
            posX=0;
        if(posY>8)
            posY=8;
        else if(posY<0)
            posY=0;
    }
    else if(sndbuffer[sndf]=='A')
    {
        if(!(posX+dx[dir]<0 || posX+dx[dir]>8 || posY+dy[dir]<0 || posY+dy[dir]>8))
        {
            if(board[posX+dx[dir]][posY+dy[dir]]==3)
                remTime+=5;
            if(board[posX+dx[dir]][posY+dy[dir]]==2)
                remTime--;
            board[posX+dx[dir]][posY+dy[dir]]=0xff;
        }
    }
    
    if(sndbuffer[sndf++]==':')
    {
        while(!TXSTA1bits.TRMT1);
        TXSTA1bits.TXEN=0;
        sndf=0;
    }
    return;
}

void RS_RX_INT(void)
{
  PIR1bits.RC1IF = 0;
  rcvbuffer[rcve] = RCREG1;
  
  if(rcvbuffer[rcve++]==':')
  {
      dataReady=1;
      rcve=0;
  }
  return;
}
