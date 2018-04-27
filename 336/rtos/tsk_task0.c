/**********************************************************************/
/*                                                                    */
/* File name: tsk_task0.c                                             */
/*                                                                    */
/* Since:     2004-Sept-20                                            */
/*                                                                    */
/* Version:   PICos18 v2.10                                           */
/*            Copyright (C) 2003, 2004, 2005 Pragmatec.               */
/*                                                                    */
/* Author:    Designed by Pragmatec S.A.R.L.        www.pragmatec.net */
/*            MONTAGNE Xavier [XM]      xavier.montagne@pragmatec.net */
/*                                                                    */
/* Purpose:   First task of the tutorial.                             */
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
/* 2004/09/20 [XM]  Create this file.                                 */
/*                                                                    */
/**********************************************************************/

#include "define.h"


/**********************************************************************
 * Definition dedicated to the local functions.
 **********************************************************************/
#define ALARM_TSK0      0
unsigned char robotx, roboty, dir;//dir 0->north, 3->west
char dx[] = {0,1,0,-1}, dy[] = {-1,0,1,0};

/**********************************************************************
 * ------------------------------ TASK0 -------------------------------
 *
 * First task of the tutorial.
 *
 **********************************************************************/
TASK(TASK0) 
{
    robotx = roboty = dir = 0;
  SetRelAlarm(ALARM_TSK0, 1000, 1000);

  while(1)
  {
    WaitEvent(ALARM_EVENT);
    ClearEvent(ALARM_EVENT);

    LATBbits.LATB4 = ~LATBbits.LATB4;
    SetEvent(TASK1_ID, TASK1_EVENT);
  }
}
 
/* End of File : tsk_task0.c */
