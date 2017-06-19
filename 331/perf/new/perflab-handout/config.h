/*********************************************************
 * config.h - Configuration data for the driver.c program.
 *********************************************************/
#ifndef _CONFIG_H_
#define _CONFIG_H_

/* 
 * CPEs for the baseline (naive) version of the transpose function that
 * was handed out to the students. Rd is the measured CPE for a dxd
 * matrix. Run the driver.c program on your system to get these
 * numbers.  
 */
#define T64    1.5
#define T128   5.3
#define T256   8.2
#define T512   9.4
#define T1024  10.0

/* 
 * CPEs for the baseline (naive) version of the col_convert function that
 * was handed out to the students. Sd is the measure CPE for a dxd
 * matrix. Run the driver.c program on your system to get these
 * numbers.  
 */
#define C64    6.7
#define C128   7.9
#define C256   9.4
#define C512   12.3
#define C1024  17.7


#endif /* _CONFIG_H_ */
