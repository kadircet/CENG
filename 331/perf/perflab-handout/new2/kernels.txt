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

/* 
 * transpose - Your current working version of transpose
 * IMPORTANT: This is the version you will be graded on
 */
inline void transpose_block4x4(int dim, int *src, int *dst)
{
    int di=dim-3,dj=1-dim*3;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
}

inline void transpose_block8x8(int dim, int *src, int *dst)
{
    int di=dim-7,dj=1-dim*7;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
}

inline void transpose_block16x16(int dim, int *src, int *dst)
{
    int di=dim-15,dj=1-dim*15;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst++=*src;
	src+=dim;
	*dst=*src;
	dst+=di;
	src+=dj;
}

void transpose2(int dim, int *src, int *dst)
{
        int i,j,di=dim*8,dj=dim*dim-8;
        for(i=0;i<dim;i+=8,dst+=di-dim,src-=dj)
                for(j=0;j<dim;j+=8,src+=di,dst+=8)
			transpose_block8x8(dim, src, dst);
}

void transpose5(int dim, int *src, int *dst)
{
        int i,j,di=dim*16,dj=dim*dim-16;
        for(i=0;i<dim;i+=16,dst+=di-dim,src-=dj)
                for(j=0;j<dim;j+=16,src+=di,dst+=16)
			transpose_block16x16(dim, src, dst);
}
void transpose3(int dim, int *src, int *dst)
{
        int i,j,di=dim*4,dj=dim*dim-4;
        for(i=0;i<dim;i+=4,dst+=di-dim,src-=dj)
                for(j=0;j<dim;j+=4,src+=di,dst+=4)
			transpose_block4x4(dim, src, dst);
}

#define BLOCK_SIZE 16
void transpose(int dim, int *src, int *dst) 
{
    int i, j;
	register int di,dj;
    for (i = 0; i < dim; i+=BLOCK_SIZE)
        for (j = 0; j < dim; j++)
        {
			di=j*dim+i;
			dj=i*dim+j;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di++]=src[dj];
			dj+=dim;
			dst[di]=src[dj];
		}
}
void transpose4(int dim, int *src, int *dst) 
{
    int i, j,di=dim*dim-1;
    for (i = 0; i < dim; i++,src-=di)
        for (j = 0; j < dim; j+=BLOCK_SIZE)
        {
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
char transpose_descr[] = "AAAA";

/*********************************************************************
 * register_transpose_functions - Register all of your different versions
 *     of the transpose kernel with the driver by calling the
 *     add_transpose_function() for each test function. When you run the
 *     driver program, it will test and report the performance of each
 *     registered test function.  
 *********************************************************************/

void register_transpose_functions() 
{
    //add_transpose_function(&naive_transpose, naive_transpose_descr);   
    add_transpose_function(&transpose, transpose_descr);   
    add_transpose_function(&transpose2, transpose_descr);   
    add_transpose_function(&transpose3, transpose_descr);   
    add_transpose_function(&transpose4, transpose_descr);   
    add_transpose_function(&transpose5, transpose_descr);   

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
            G[j*dim+i] |= G[i*dim+j];
}



/*
 * col_convert - Your current working version of col_convert. 
 * IMPORTANT: This is the version you will be graded on
 */
#define BLOCK_SIZE2 8
char col_convert_descr[] = "0123456789012345678901234567890123456789012345678901234567890";
inline void col_convert_block(int dim, int *G, int *G2) 
{
    int i, j,di,dj;
    for (i = 0,di=0; i < BLOCK_SIZE2;i++,di+=dim)
        for (j = 0,dj=0; j < BLOCK_SIZE2;j++,dj+=dim)
		{
            G[dj+i] |= G2[di+j];
            G2[di+j] = G[dj+i];
		}
}

void col_convert(int dim, int *G) 
{
    int i, j;
	register int di, dj;
    for (i = 0,di=0; i < dim;i+=BLOCK_SIZE2)
        for (j = i+1; j < dim; j++)
		{
			di=i*dim+j;
			dj=j*dim+i;
			G[di] |= G[dj];
			G[dj++] = G[di];
			di+=dim;
			G[di] |= G[dj];
			G[dj++] = G[di];
			di+=dim;
			G[di] |= G[dj];
			G[dj++] = G[di];
			di+=dim;
			G[di] |= G[dj];
			G[dj++] = G[di];
			di+=dim;
			G[di] |= G[dj];
			G[dj++] = G[di];
			di+=dim;
			G[di] |= G[dj];
			G[dj++] = G[di];
			di+=dim;
			G[di] |= G[dj];
			G[dj++] = G[di];
			di+=dim;
			G[di] |= G[dj];
			G[dj] = G[di];
		}
}

void col_convert2(int dim, int *G) 
{
    int i, j, di, dj;
    for (i = 0,di=0; i < dim; i+=BLOCK_SIZE2,di+=BLOCK_SIZE2*dim)
        for (j = 0,dj=0; j <= i; j+=BLOCK_SIZE2,dj+=BLOCK_SIZE2*dim)
			col_convert_block(dim, G+dj+i, G+di+j);
}

void col_convert3(int dim, int *G) 
{
    int i, j, di, dj;
    for (i = 0,di=0; i < dim; i+=BLOCK_SIZE2,di+=BLOCK_SIZE2*dim)
        for (j = i,dj=i*BLOCK_SIZE2*dim; j < dim; j+=BLOCK_SIZE2,dj+=BLOCK_SIZE2*dim)
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
//    add_col_convert_function(&naive_col_convert, naive_col_convert_descr);
    add_col_convert_function(&col_convert, col_convert_descr);
    add_col_convert_function(&col_convert2, col_convert_descr);
    add_col_convert_function(&col_convert3, col_convert_descr);

    /* ... Register additional test functions here */
}

