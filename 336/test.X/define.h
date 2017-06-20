/**********************************************************************/
/*                                                                    */
/* File name: define.h                                                */
/*                                                                    */
/* Since:     2004-Sept-20                                            */
/*                                                                    */
/* Version:   PICos18 v2.10                                           */
/*            Copyright (C) 2003, 2004, 2005 Pragmatec.               */
/*                                                                    */
/* Author:    Designed by Pragmatec S.A.R.L.        www.pragmatec.net */
/*            MONTAGNE Xavier [XM]      xavier.montagne@pragmatec.net */
/*            NIELSEN  Peter  [PN]                   pnielsen@tuug.fi */
/*                                                                    */
/* Purpose:   Specify all the specific definitions of the project.    */
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
/* 2004/11/06 [RZR] Original idea of RZR.                             */
/* 2007/01/01 [PN]  Added magic formula dedicated to clock frequency. */
/*                                                                    */
/**********************************************************************/

#ifndef _DEFINE_H
#define _DEFINE_H

#include "device.h"

/***********************************************************************
 * ------------------------ Timer settings -----------------------------
 **********************************************************************/
/* MAGIC : Tmr0.lt = 65536 - (CPU_FREQUENCY_HZ/4/1000 - 232)          */
#define _10MHZ	63320
#define _16MHZ	61768
#define _20MHZ	60768
#define _32MHZ	57768
#define _40MHZ 	55768-209

/***********************************************************************
 * ----------------------------- Events --------------------------------
 **********************************************************************/
#define ALARM_EVENT       0x80
#define LCD_EVENT         0x10
#define RS_NEW_MSG        0x10
#define RS_RCV_MSG        0x20
#define RS_QUEUE_EMPTY    0x10
#define RS_QUEUE_FULL     0x20


/***********************************************************************
 * ----------------------------- Task ID -------------------------------
 **********************************************************************/
/* Info about the tasks:
 * TASK0: 7-segment display task
 * TASK1: A/D Conversion and USART transmission
 */
#define LCDTASK_ID             1
#define BOTTASK_ID             2
#define RSTASK_ID              3

/* Priorities of the tasks */
#define LCDTASK_PRIO           8
#define BOTTASK_PRIO           1
#define RSTASK_PRIO            9


#endif /* _DEFINE_H */


/* End of File : define.h */
