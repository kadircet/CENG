/**********************************************************************/
/*                                                                    */
/* File name: taskdesc.c                                              */
/*                                                                    */
/* Since:     2004-Sept-20                                            */
/*                                                                    */
/* Version:   PICos18 v2.10                                           */
/*            Copyright (C) 2003, 2004, 2005 Pragmatec.               */
/*                                                                    */
/* Author:    Designed by Pragmatec S.A.R.L.        www.pragmatec.net */
/*            MONTAGNE Xavier [XM]      xavier.montagne@pragmatec.net */
/*                                                                    */
/* Purpose:   Kind of OIL file where all the features of the tasks    */
/*            are described.                                          */
/*                                                                    */
/*           rom_desc_tsk foo = {                                     */
/*             0x..,    Priority level [0:15],15 the most significant */
/*             0x....,  Stack address                                 */
/*             0x....,  Start adress of the task                      */
/*             0x..,    Task state at start [RUN/WAIT/READY/SUSPEND]  */
/*             0x..,    Identification number of the task             */
/*             0x....   Stack size                                    */
/*             };                                                     */
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
/* 2004/09/20 [XM]  Create this file                                  */
/* 2005/10/29 [XM]  Added cast to avoid some C18 v3.00 WARNINGS.      */
/* 2006/05/17 [XM]  Removed the stack section.                        */
/*                                                                    */
/**********************************************************************/

#include "define.h"

/**********************************************************************
 * --------------------- COUNTER & ALARM DEFINITION -------------------
 **********************************************************************/
Counter Counter_list[] = 
  {
   /*******************************************************************
    * -------------------------- First counter ------------------------
    *******************************************************************/
   {
     {
       200,                                /* maxAllowedValue        */
        10,                                /* ticksPerBase           */
       100                                 /* minCycle               */
     },
     0,                                    /* CounterValue           */
     0                                     /* Nbr of Tick for 1 CPT  */
   }
  };

Counter Counter_kernel = 
  {
    {
      65535,                              /* maxAllowedValue        */
          1,                              /* ticksPerBase           */
          0                               /* minCycle               */
    },
    0,                                    /* CounterValue           */
    0                                     /* Nbr of Tick for 1 CPT  */
  };

AlarmObject Alarm_list[] = 
  {
   /*******************************************************************
    * -------------------------- First task ---------------------------
    *******************************************************************/
   {
     OFF,                                  /* State                   */
     0,                                    /* AlarmValue              */
     0,                                    /* Cycle                   */
     &Counter_kernel,                      /* ptrCounter              */
     TASK0_ID,                             /* TaskID2Activate         */
     ALARM_EVENT,                          /* EventToPost             */
     0                                     /* CallBack                */
   },
   /*******************************************************************
    * -------------------------- Fifth task ---------------------------
    *******************************************************************/
   {
     OFF,                                  /* State                   */
     0,                                    /* AlarmValue              */
     0,                                    /* Cycle                   */
     &Counter_kernel,                      /* ptrCounter              */
     TASK4_ID,                             /* TaskID2Activate         */
     ALARM_EVENT,                          /* EventToPost             */
     0                                     /* CallBack                */
   },
 };

#define _ALARMNUMBER_          sizeof(Alarm_list)/sizeof(AlarmObject)
#define _COUNTERNUMBER_        sizeof(Counter_list)/sizeof(Counter)
unsigned char ALARMNUMBER    = _ALARMNUMBER_;
unsigned char COUNTERNUMBER  = _COUNTERNUMBER_;
unsigned long global_counter = 0;

/**********************************************************************
 * --------------------- COUNTER & ALARM DEFINITION -------------------
 **********************************************************************/
Resource Resource_list[] = 
  {
   {
      10,                                /* priority           */
       0,                                /* Task prio          */
       0,                                /* lock               */
   }
  };
  
#define _RESOURCENUMBER_       sizeof(Resource_list)/sizeof(Resource)
unsigned char RESOURCENUMBER = _RESOURCENUMBER_;

/**********************************************************************
 * ----------------------- TASK & STACK DEFINITION --------------------
 **********************************************************************/
#define DEFAULT_STACK_SIZE      128
DeclareTask(TASK0);
DeclareTask(TASK1);
DeclareTask(TASK2);
DeclareTask(TASK3);
DeclareTask(TASK4);
DeclareTask(RS_Drv);

// to avoid any C18 map error : regroup the stacks into blocks
// of 256 bytes (except the last one).
#pragma		udata      STACK_A   
volatile unsigned char stack0[DEFAULT_STACK_SIZE];
volatile unsigned char stack1[DEFAULT_STACK_SIZE];
#pragma		udata      STACK_B
volatile unsigned char stack4[DEFAULT_STACK_SIZE+DEFAULT_STACK_SIZE];
#pragma		udata      STACK_C
volatile unsigned char stack2[DEFAULT_STACK_SIZE];
volatile unsigned char stack3[DEFAULT_STACK_SIZE];
#pragma		udata      STACK_D
volatile unsigned char stack_rs[DEFAULT_STACK_SIZE];
#pragma		udata

/**********************************************************************
 * ---------------------- TASK DESCRIPTOR SECTION ---------------------
 **********************************************************************/
#pragma		romdata		DESC_ROM
const rom unsigned int descromarea;
/**********************************************************************
 * -----------------------------  task 0 ------------------------------
 **********************************************************************/
rom_desc_tsk rom_desc_task0 = {
	TASK0_PRIO,                        /* prioinit from 0 to 15       */
	stack0,                            /* stack address (16 bits)     */
	TASK0,                             /* start address (16 bits)     */
	READY,                             /* state at init phase         */
	TASK0_ID,                          /* id_tsk from 1 to 15         */
	sizeof(stack0)                     /* stack size    (16 bits)     */
};
/**********************************************************************
 * -----------------------------  task 1 ------------------------------
 **********************************************************************/
rom_desc_tsk rom_desc_task1 = {
	TASK1_PRIO,                        /* prioinit from 0 to 15       */
	stack1,                            /* stack address (16 bits)     */
	TASK1,                             /* start address (16 bits)     */
	READY,                             /* state at init phase         */
	TASK1_ID,                          /* id_tsk from 1 to 15         */
	sizeof(stack1)                     /* stack size    (16 bits)     */
};
/**********************************************************************
 * -----------------------------  task 2 ------------------------------
 **********************************************************************/
rom_desc_tsk rom_desc_task2 = {
	TASK2_PRIO,                        /* prioinit from 0 to 15       */
	stack2,                            /* stack address (16 bits)     */
	TASK2,                             /* start address (16 bits)     */
	SUSPENDED,                         /* state at init phase         */
	TASK2_ID,                          /* id_tsk from 1 to 15         */
	sizeof(stack2)                     /* stack size    (16 bits)     */
};
/**********************************************************************
 * -----------------------------  task 3 ------------------------------
 **********************************************************************/
rom_desc_tsk rom_desc_task3 = {
	TASK3_PRIO,                        /* prioinit from 0 to 15       */
	stack3,                            /* stack address (16 bits)     */
	TASK3,                             /* start address (16 bits)     */
	SUSPENDED,                         /* state at init phase         */
	TASK3_ID,                          /* id_tsk from 1 to 15         */
	sizeof(stack3)                     /* stack size    (16 bits)     */
};
/**********************************************************************
 * -----------------------------  task 4 ------------------------------
 **********************************************************************/
rom_desc_tsk rom_desc_task4 = {
	TASK4_PRIO,                        /* prioinit from 0 to 15       */
	stack4,                            /* stack address (16 bits)     */
	TASK4,                             /* start address (16 bits)     */
	READY,                             /* state at init phase         */
	TASK4_ID,                          /* id_tsk from 1 to 15         */
	sizeof(stack4)                     /* stack size    (16 bits)     */
};
/**********************************************************************
 * ------------------------------ RS task -----------------------------
 **********************************************************************/
rom_desc_tsk rom_desc_RS_DRV = {
	RDV_RS_PRIO,                       /* prioinit from 0 to 15       */
	stack_rs,                          /* stack address (16 bits)     */
	RS_Drv,                            /* start address (16 bits)     */
	READY,                             /* state at init phase         */
	RS_DRV_ID,                         /* id_tsk from 0 to 15         */
	sizeof(stack_rs)                   /* stack size    (16 bits)     */
}; 

/**********************************************************************
 * --------------------- END TASK DESCRIPTOR SECTION ------------------
 **********************************************************************/
rom_desc_tsk end = {
	0x00,                              /* prioinit from 0 to 15       */
	0x00,                              /* stack address               */
	0x0000,                            /* adr_tsk in 16 bits          */
	0x00,                              /* state at init phase         */
	0x00,                              /* id_tsk from 1 to 15         */
	0x0000                             /* stack size    (16 bits)     */
};

volatile rom unsigned int * taskdesc_addr = (&(descromarea)+1);
	
/* End of File : taskdesc.c */
