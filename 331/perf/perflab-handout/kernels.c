/********************************************************
 * Kernels to be optimized for the CS:APP Performance Lab
 ********************************************************/

#include <stdio.h>
#include <stdlib.h>
#include "defs.h"

/* 
 * Please fill in the following student struct 
 */
team_t team = {
    "2036457",              /* Student ID */

    "Kadir Cetinkaya",     /* full name */
    "e2036457@ceng.metu.edu.tr",  /* email address */

    "",                   /* leave blank */
    ""                    /* leave blank */
};

/***************
 * TRANSPOSE KERNEL
 ***************/

/******************************************************
 * Your different versions of the transpose kernel go here
 ******************************************************/

/* 
 * naive_transpose - The naive baseline version of transpose 
 */
char naive_transpose_descr[] = "Naive_transpose: Naive baseline implementation";
void naive_transpose(int dim, int *src, int *dst) 
{
    int i, j;

    for (i = 0; i < dim; i++)
        for (j = 0; j < dim; j++)
            dst[j*dim+i] = src[i*dim+j];
}


/* 
 * transpose - Your current working version of transpose
 * IMPORTANT: This is the version you will be graded on
 */
char transpose_descr[] = "Transpose: Current working version";
#define BLOCK_SIZE 32
void transpose2(int dim, int *src, int *dst)
{
	int i,j,di=1-3*dim,dj=dim-3;
	for(i=0,di=0;i<dim;i+=4)
		for(j=0,dj=0;j<dim;j+=4)
		{
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst = *src;
			src+=di;
			dst+=dj;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst = *src;
			src+=di;
			dst+=dj;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst = *src;
			src+=di;
			dst+=dj;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst = *src;
			src+=di;
			dst+=dj;
		}
}
void transpose(int dim, int *src, int *dst) 
{
    int i, j,di=dim*dim-1;
    for (i = 0; i < dim; i++,src-=di)
        for (j = 0; j < dim; j+=BLOCK_SIZE)
		{
            //transpose_block(dim, src+di+j, dst+dj+i);
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
			*dst++ = *src;
			src+=dim;
		}
}


/*********************************************************************
 * register_transpose_functions - Register all of your different versions
 *     of the transpose kernel with the driver by calling the
 *     add_transpose_function() for each test function. When you run the
 *     driver program, it will test and report the performance of each
 *     registered test function.  
 *********************************************************************/

void register_transpose_functions() 
{
    add_transpose_function(&naive_transpose, naive_transpose_descr);   
    add_transpose_function(&transpose, transpose_descr);   
    add_transpose_function(&transpose2, transpose_descr);   

    /* ... Register additional test functions here */
}


/***************
 * CONVERT KERNEL
 **************/


/******************************************************
 * Your different versions of the col_convert kernel go here
 ******************************************************/

/*
 * naive_col_convert - The naive baseline version of col_convert 
 */
char naive_col_convert_descr[] = "Naive_col_convert: Naive baseline implementation";
void naive_col_convert(int dim, int *G) 
{
    int i, j;
    for (i = 0; i < dim; i++)
        for (j = 0; j < dim; j++)
            G[j*dim+i] = G[j*dim+i] || G[i*dim+j];
}



/*
 * col_convert - Your current working version of col_convert. 
 * IMPORTANT: This is the version you will be graded on
 */
char col_convert_descr[] = "Col_convert: Current working version";
void col_convert_block(int dim, int *G, int *G2) 
{
    int i, j,di,dj;
    for (i = 0,di=0; i < BLOCK_SIZE;i++,di+=dim)
        for (j = 0,dj=0; j < BLOCK_SIZE;j++,dj+=dim)
            G[dj+i] |= G2[di+j];
}
void col_convert(int dim, int *G) 
{
    int i, j, di, dj;
    for (i = 0,di=0; i < dim; i+=BLOCK_SIZE,di+=BLOCK_SIZE*dim)
        for (j = 0,dj=0; j < dim; j+=BLOCK_SIZE,dj+=BLOCK_SIZE*dim)
			col_convert_block(dim, G+dj+i, G+di+j);
}


/********************************************************************* 
 * register_col_convert_functions - Register all of your different versions
 *     of the col_convert kernel with the driver by calling the
 *     add_col_convert_function() for each test function.  When you run the
 *     driver program, it will test and report the performance of each
 *     registered test function.  
 *********************************************************************/


void register_col_convert_functions() {
    add_col_convert_function(&naive_col_convert, naive_col_convert_descr);
    add_col_convert_function(&col_convert, col_convert_descr);

    /* ... Register additional test functions here */
}

