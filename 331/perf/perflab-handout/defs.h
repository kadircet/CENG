/*
 * driver.h - Various definitions for the Performance Lab.
 * 
 * DO NOT MODIFY ANYTHING IN THIS FILE
 */
#ifndef _DEFS_H_
#define _DEFS_H_

#include <stdlib.h>

#define RIDX(i,j,n) ((i)*(n)+(j))

typedef struct {
  char *team;
  char *name1, *email1;
  char *name2, *email2;
} team_t;

extern team_t team;


typedef void (*lab_test_func1) (int, int*, int*);
typedef void (*lab_test_func2) (int, int*);

void transpose(int, int *, int *);
void col_convert(int, int *);

void register_transpose_functions(void);
void register_col_convert_functions(void);
void add_transpose_function(lab_test_func1, char*);
void add_col_convert_function(lab_test_func2, char*);

#endif /* _DEFS_H_ */

