/*******************************************************************
 * 
 * driver.c - Driver program for CS:APP Performance Lab
 * 
 * In kernels.c, students generate an arbitrary number of transpose and
 * col_convert test functions, which they then register with the driver
 * program using the add_transpose_function() and add_col_convert_function()
 * functions.
 * 
 * The driver program runs and measures the registered test functions
 * and reports their performance.
 * 
 * Copyright (c) 2002, R. Bryant and D. O'Hallaron, All rights
 * reserved.  May not be used, modified, or copied without permission.
 *
 ********************************************************************/

#include <sys/time.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <assert.h>
#include <math.h>
#include "fcyc.h"
#include "defs.h"
#include "config.h"

/* Team structure that identifies the students */
extern team_t team; 

/* Keep track of a number of different test functions */
#define MAX_BENCHMARKS 100
#define DIM_CNT 5

/* Misc constants */
#define BSIZE 32     /* cache block size in bytes */     
#define MAX_DIM 1280 /* 1024 + 256 */
#define ODD_DIM 96   /* not a power of 2 */

/* fast versions of min and max */
#define min(a,b) (a < b ? a : b)
#define max(a,b) (a > b ? a : b)

/* This struct characterizes the results for one benchmark test */
typedef struct {
	int func_type;
	union func{
		lab_test_func1 tfunct1;
		lab_test_func2 tfunct2;
	} tfunct;
    ; /* The test function */
    double cpes[DIM_CNT]; /* One CPE result for each dimension */
    char *description;    /* ASCII description of the test function */
    unsigned short valid; /* The function is tested if this is non zero */
} bench_t;

/* The range of matrix dimensions that we will be testing */
static int test_dim_transpose[] = {64, 128, 256, 512, 1024};
static int test_dim_col_convert[] = {64, 128, 256, 512, 1024};

/* Baseline CPEs (see config.h) */
static double transpose_baseline_cpes[] = {T64, T128, T256, T512, T1024};
static double col_convert_baseline_cpes[] = {C64, C128, C256, C512, C1024};

/* These hold the results for all benchmarks */
static bench_t benchmarks_transpose[MAX_BENCHMARKS];
static bench_t benchmarks_col_convert[MAX_BENCHMARKS];

/* These give the sizes of the above lists */
static int transpose_benchmark_count = 0;
static int col_convert_benchmark_count = 0;

/* 
 * An matrix is a dimxdim matrix of ints stored in a 1D array.  The
 * data array holds three matrixs (the input original, a copy of the original, 
 * and the output result array. There is also an additional BSIZE bytes
 * of padding for alignment to cache block boundaries.
 */
static int data[(3*MAX_DIM*MAX_DIM) + (BSIZE/sizeof(int))];

/* Various matrix pointers */
static int *orig = NULL;         /* original matrix */
static int *copy_of_orig = NULL; /* copy of original for checking result */
static int *result = NULL;       /* result matrix */

/* Keep track of the best transpose and col_convert score for grading */
double transpose_maxmean = 0.0;
char *transpose_maxmean_desc = NULL;

double col_convert_maxmean = 0.0;
char *col_convert_maxmean_desc = NULL;


/******************** Functions begin *************************/

void add_col_convert_function(lab_test_func2 f, char *description) 
{
	benchmarks_col_convert[col_convert_benchmark_count].func_type = 2;
    benchmarks_col_convert[col_convert_benchmark_count].tfunct.tfunct2 = f;
    benchmarks_col_convert[col_convert_benchmark_count].description = description;
    benchmarks_col_convert[col_convert_benchmark_count].valid = 0;  
    col_convert_benchmark_count++;
}


void add_transpose_function(lab_test_func1 f, char *description) 
{
	benchmarks_transpose[col_convert_benchmark_count].func_type = 1;
    benchmarks_transpose[transpose_benchmark_count].tfunct.tfunct1 = f;
    benchmarks_transpose[transpose_benchmark_count].description = description;
    benchmarks_transpose[transpose_benchmark_count].valid = 0;
    transpose_benchmark_count++;
}

/* 
 * random_in_interval - Returns random integer in interval [low, high) 
 */
static int random_in_interval(int low, int high) 
{
    int size = high - low;
    return (rand()% size) + low;
}

/*
 * create - creates a dimxdim matrix aligned to a BSIZE byte boundary
 */
static void create(int dim, int flag)
{
    int i, j;

    /* Align the matrixs to BSIZE byte boundaries */
    orig = data;
    while ((unsigned)orig % BSIZE)
		orig = (int *)(((char *)orig) + 1);
    result = orig + dim*dim;
    copy_of_orig = result + dim*dim;

    for (i = 0; i < dim; i++) {
	for (j = 0; j < dim; j++) {
	    /* Original matrix initialized to random colors */
		if(flag==1)
	    	orig[RIDX(i,j,dim)] = random_in_interval(0, 65536);
	    else
	   		orig[RIDX(i,j,dim)] = random_in_interval(0, 2);

	    /* Copy of original matrix for checking result */
	    copy_of_orig[RIDX(i,j,dim)] = orig[RIDX(i,j,dim)];
	   

	    /* Result matrix initialized to all black */
	    result[RIDX(i,j,dim)] = 0;
	}
    }

    return;
}


/* 
 * compare_ints - Returns 1 if the two arguments don't have same RGB
 *    values, 0 o.w.  
 */
static int compare_ints(int p1, int p2) 
{
    return 
	(p1 != p2);
}


/* Make sure the orig array is unchanged */
static int check_orig(int dim) 
{
    int i, j;

    for (i = 0; i < dim; i++) 
	for (j = 0; j < dim; j++) 
	    if (compare_ints(orig[RIDX(i,j,dim)], copy_of_orig[RIDX(i,j,dim)])) {
		printf("\n");
		printf("Error: Original matrix has been changed!\n");
		return 1;
	    }

    return 0;
}

/* 
 * check_transpose - Make sure the transpose actually works. 
 * The orig array should not  have been tampered with! 
 */
static int check_transpose(int dim) 
{
    int err = 0;
    int i, j;
    int badi = 0;
    int badj = 0;
    int orig_bad, res_bad;

    /* return 1 if the original matrix has been  changed */
    if (check_orig(dim)) 
	return 1; 

    for (i = 0; i < dim; i++) 
	for (j = 0; j < dim; j++) 
	    if (compare_ints(orig[RIDX(i,j,dim)], 
			       result[RIDX(j,i,dim)])) {
		err++;
		badi = i;
		badj = j;
		orig_bad = orig[RIDX(i,j,dim)];
		res_bad = result[RIDX(j,i,dim)];
	    }

    if (err) {
	printf("\n");
	printf("ERROR: Dimension=%d, %d errors\n", dim, err);    
	printf("E.g., The following two ints should have equal value:\n");
	printf("src[%d][%d] = %d\n",
	       badi, badj, orig_bad);
	printf("dst[%d][%d] = %d\n",
	       (dim-1-badj), badi, res_bad);
    }

    return err;
}


/* 
 * check_col_convert - Make sure the col_convert function actually works.  The
 * orig array should not have been tampered with!  
 */
static int check_col_convert(int dim) {
    int err = 0;
    int i, j;
    int badi = 0;
    int badj = 0;
    int right, wrong;

    /* return 1 if original matrix has been changed */
    //if (check_orig(dim)) 
	//return 1; 

    for (i = 0; i < dim; i++) {
	for (j = 0; j < dim; j++) {
	    if (orig[RIDX(j,i,dim)] != (copy_of_orig[RIDX(j,i,dim)] || copy_of_orig[RIDX(i,j,dim)])) {
		err++;
		badi = i;
		badj = j;
		wrong = orig[RIDX(j,i,dim)];
		right = (copy_of_orig[RIDX(j,i,dim)] || copy_of_orig[RIDX(i,j,dim)]);
	    }
	}
    }

    if (err) {
	printf("\n");
	printf("ERROR: Dimension=%d, %d errors\n", dim, err);    
	printf("E.g., \n");
	printf("src[%d][%d] = %d\n",
	       badi, badj, right);
	printf("dst[%d][%d] = %d\n",
	       (dim-1-badj), badi, wrong);
	}

    return err;
}


void func_wrapper(void *arglist[]) 
{
    int *src, *dst;
    int mydim;
    lab_test_func1 f1;
	lab_test_func2 f2;
	int type;
	type = *((int*) arglist[0]);
	if ( type == 1 ) {
		f1 = (lab_test_func1) arglist[1];
		mydim = *((int *) arglist[2]);
		src = (int *) arglist[3];
		dst = (int *) arglist[4];
		(*f1)(mydim, src, dst);
	}
	else {
		f2 = (lab_test_func2) arglist[1];
		mydim = *((int *) arglist[2]);
		src = (int *) arglist[3];
		(*f2)(mydim, src);
	}
	
    

    

    return;
}



void run_transpose_benchmark(int idx, int dim) 
{
    benchmarks_transpose[idx].tfunct.tfunct1(dim, orig, result);
}

void test_transpose(int bench_index) 
{
    int i;
    int test_num;
    char *description = benchmarks_transpose[bench_index].description;
  
    for (test_num = 0; test_num < DIM_CNT; test_num++) {
	int dim;

	/* Check for odd dimension */
	create(ODD_DIM,1);
	run_transpose_benchmark(bench_index, ODD_DIM);
	if (check_transpose(ODD_DIM)) {
	    printf("Benchmark \"%s\" failed correctness check for dimension %d.\n",
		   benchmarks_transpose[bench_index].description, ODD_DIM);
	    return;
	}

	/* Create a test matrix of the required dimension */
	dim = test_dim_transpose[test_num];
	create(dim,1);
#ifdef DEBUG
	printf("DEBUG: Running benchmark \"%s\"\n", benchmarks_transpose[bench_index].description);
#endif

	/* Check that the code works */
	run_transpose_benchmark(bench_index, dim);
	if (check_transpose(dim)) {
	    printf("Benchmark \"%s\" failed correctness check for dimension %d.\n",
		   benchmarks_transpose[bench_index].description, dim);
	    return;
	}

	/* Measure CPE */
	{
	    double num_cycles, cpe;
	    int tmpdim = dim;
	    void *arglist[5];
	    double dimension = (double) dim;
	    double work = dimension*dimension;
#ifdef DEBUG
	    printf("DEBUG: dimension=%.1f\n",dimension);
	    printf("DEBUG: work=%.1f\n",work);
#endif
		int tmp_type = 1;
		arglist[0] = &tmp_type;
	    arglist[1] = (void *) benchmarks_transpose[bench_index].tfunct.tfunct1;
	    arglist[2] = (void *) &tmpdim;
	    arglist[3] = (void *) orig;
	    arglist[4] = (void *) result;

	    create(dim,1);
	    num_cycles = fcyc_v((test_funct_v)&func_wrapper, arglist); 
	    cpe = num_cycles/work;
	    benchmarks_transpose[bench_index].cpes[test_num] = cpe;
	}
    }

    /* 
     * Print results as a table 
     */
    printf("Transpose: Version = %s:\n", description);
    printf("Dim\t");
    for (i = 0; i < DIM_CNT; i++)
	printf("\t%d", test_dim_transpose[i]);
    printf("\tMean\n");
  
    printf("Your CPEs");
    for (i = 0; i < DIM_CNT; i++) {
	printf("\t%.1f", benchmarks_transpose[bench_index].cpes[i]);
    }
    printf("\n");

    printf("Baseline CPEs");
    for (i = 0; i < DIM_CNT; i++) {
	printf("\t%.1f", transpose_baseline_cpes[i]);
    }
    printf("\n");

    /* Compute Speedup */
    {
	double prod, ratio, mean;
	prod = 1.0; /* Geometric mean */
	printf("Speedup\t");
	for (i = 0; i < DIM_CNT; i++) {
	    if (benchmarks_transpose[bench_index].cpes[i] > 0.0) {
		ratio = transpose_baseline_cpes[i]/
		    benchmarks_transpose[bench_index].cpes[i];
	    }
	    else {
		printf("Fatal Error: Non-positive CPE value...\n");
		exit(EXIT_FAILURE);
	    }
	    prod *= ratio;
	    printf("\t%.1f", ratio);
	}

	/* Geometric mean */
	mean = pow(prod, 1.0/(double) DIM_CNT);
	printf("\t%.1f", mean);
	printf("\n\n");
	if (mean > transpose_maxmean) {
	    transpose_maxmean = mean;
	    transpose_maxmean_desc = benchmarks_transpose[bench_index].description;
	}
    }


#ifdef DEBUG
    fflush(stdout);
#endif
    return;  
}

void run_col_convert_benchmark(int idx, int dim) 
{
    benchmarks_col_convert[idx].tfunct.tfunct2(dim, orig);
}

void test_col_convert(int bench_index) 
{
    int i;
    int test_num;
    char *description = benchmarks_col_convert[bench_index].description;
  
    for(test_num=0; test_num < DIM_CNT; test_num++) {
	int dim;

	/* Check correctness for odd (non power of two dimensions */
	create(ODD_DIM,2);
	run_col_convert_benchmark(bench_index, ODD_DIM);
	if (check_col_convert(ODD_DIM)) {
	    printf("Benchmark \"%s\" failed correctness check for dimension %d.\n",
		   benchmarks_col_convert[bench_index].description, ODD_DIM);
	    return;
	}

	/* Create a test matrix of the required dimension */
	dim = test_dim_col_convert[test_num];
	create(dim,2);

#ifdef DEBUG
	printf("DEBUG: Running benchmark \"%s\"\n", benchmarks_col_convert[bench_index].description);
#endif
	/* Check that the code works */
	run_col_convert_benchmark(bench_index, dim);
	if (check_col_convert(dim)) {
	    printf("Benchmark \"%s\" failed correctness check for dimension %d.\n",
		   benchmarks_col_convert[bench_index].description, dim);
	    return;
	}

	/* Measure CPE */
	{
	    double num_cycles, cpe;
	    int tmpdim = dim;
	    void *arglist[4];
	    double dimension = (double) dim;
	    double work = dimension*dimension;
#ifdef DEBUG
	    printf("DEBUG: dimension=%.1f\n",dimension);
	    printf("DEBUG: work=%.1f\n",work);
#endif
		int tmp_type = 2;
		arglist[0] = &tmp_type;
	    arglist[1] = (void *) benchmarks_col_convert[bench_index].tfunct.tfunct2;
	    arglist[2] = (void *) &tmpdim;
	    arglist[3] = (void *) orig;
        
	    create(dim,2);
	    num_cycles = fcyc_v((test_funct_v)&func_wrapper, arglist); 
	    cpe = num_cycles/work;
	    benchmarks_col_convert[bench_index].cpes[test_num] = cpe;
	}
    }

    /* Print results as a table */
    printf("Col_convert: Version = %s:\n", description);
    printf("Dim\t");
    for (i = 0; i < DIM_CNT; i++)
	printf("\t%d", test_dim_col_convert[i]);
    printf("\tMean\n");
  
    printf("Your CPEs");
    for (i = 0; i < DIM_CNT; i++) {
	printf("\t%.1f", benchmarks_col_convert[bench_index].cpes[i]);
    }
    printf("\n");

    printf("Baseline CPEs");
    for (i = 0; i < DIM_CNT; i++) {
	printf("\t%.1f", col_convert_baseline_cpes[i]);
    }
    printf("\n");

    /* Compute speedup */
    {
	double prod, ratio, mean;
	prod = 1.0; /* Geometric mean */
	printf("Speedup\t");
	for (i = 0; i < DIM_CNT; i++) {
	    if (benchmarks_col_convert[bench_index].cpes[i] > 0.0) {
		ratio = col_convert_baseline_cpes[i]/
		    benchmarks_col_convert[bench_index].cpes[i];
	    }
	    else {
		printf("Fatal Error: Non-positive CPE value...\n");
		exit(EXIT_FAILURE);
	    }
	    prod *= ratio;
	    printf("\t%.1f", ratio);
	}
	/* Geometric mean */
	mean = pow(prod, 1.0/(double) DIM_CNT);
	printf("\t%.1f", mean);
	printf("\n\n");
	if (mean > col_convert_maxmean) {
	    col_convert_maxmean = mean;
	    col_convert_maxmean_desc = benchmarks_col_convert[bench_index].description;
	}
    }

    return;  
}


void usage(char *progname) 
{
    fprintf(stderr, "Usage: %s [-hqg] [-f <func_file>] [-d <dump_file>]\n", progname);    
    fprintf(stderr, "Options:\n");
    fprintf(stderr, "  -h         Print this message\n");
    fprintf(stderr, "  -q         Quit after dumping (use with -d )\n");
    fprintf(stderr, "  -g         Autograder mode: checks only transpose() and col_convert()\n");
    fprintf(stderr, "  -f <file>  Get test function names from dump file <file>\n");
    fprintf(stderr, "  -d <file>  Emit a dump file <file> for later use with -f\n");
    exit(EXIT_FAILURE);
}



int main(int argc, char *argv[])
{
    int i;
    int quit_after_dump = 0;
    int skip_teamname_check = 0;
    int autograder = 0;
    int seed = 1729;
    char c = '0';
    char *bench_func_file = NULL;
    char *func_dump_file = NULL;

    /* register all the defined functions */
    register_transpose_functions();
    register_col_convert_functions();

    /* parse command line args */
    while ((c = getopt(argc, argv, "tgqf:d:s:h")) != -1)
	switch (c) {

	case 't': /* skip team name check (hidden flag) */
	    skip_teamname_check = 1;
	    break;

	case 's': /* seed for random number generator (hidden flag) */
	    seed = atoi(optarg);
	    break;

	case 'g': /* autograder mode (checks only transpose() and col_convert()) */
	    autograder = 1;
	    break;

	case 'q':
	    quit_after_dump = 1;
	    break;

	case 'f': /* get names of benchmark functions from this file */
	    bench_func_file = strdup(optarg);
	    break;

	case 'd': /* dump names of benchmark functions to this file */
	    func_dump_file = strdup(optarg);
	    {
		int i;
		FILE *fp = fopen(func_dump_file, "w");	

		if (fp == NULL) {
		    printf("Can't open file %s\n",func_dump_file);
		    exit(-5);
		}

		for(i = 0; i < transpose_benchmark_count; i++) {
		    fprintf(fp, "R:%s\n", benchmarks_transpose[i].description); 
		}
		for(i = 0; i < col_convert_benchmark_count; i++) {
		    fprintf(fp, "S:%s\n", benchmarks_col_convert[i].description); 
		}
		fclose(fp);
	    }
	    break;

	case 'h': /* print help message */
	    usage(argv[0]);

	default: /* unrecognized argument */
	    usage(argv[0]);
	}

    if (quit_after_dump) 
	exit(EXIT_SUCCESS);


    /* Print team info */
    if (!skip_teamname_check) {
	if (strcmp("ID", team.team) == 0) {
	    printf("%s: Please fill in your ID in kernels.c.\n", argv[0]);
	    exit(1);
	}
	printf("ID: %s\n", team.team);
	printf("Name: %s\n", team.name1);
	printf("Email: %s\n", team.email1);
	if (*team.name2 || *team.email2) {
	    printf("Name 2: %s\n", team.name2);
	    printf("Email 2: %s\n", team.email2);
	}
	printf("\n");
    }

    srand(seed);

    /* 
     * If we are running in autograder mode, we will only test
     * the transpose() and bench() functions.
     */
    if (autograder) {
	transpose_benchmark_count = 1;
	col_convert_benchmark_count = 1;

	benchmarks_transpose[0].tfunct.tfunct1 = transpose;
	benchmarks_transpose[0].description = "transpose() function";
	benchmarks_transpose[0].valid = 1;

	benchmarks_col_convert[0].tfunct.tfunct2 = col_convert;
	benchmarks_col_convert[0].description = "col_convert() function";
	benchmarks_col_convert[0].valid = 1;
    }

    /* 
     * If the user specified a file name using -f, then use
     * the file to determine the versions of transpose and col_convert to test
     */
    else if (bench_func_file != NULL) {
	char flag;
	char func_line[256];
	FILE *fp = fopen(bench_func_file, "r");

	if (fp == NULL) {
	    printf("Can't open file %s\n",bench_func_file);
	    exit(-5);
	}
    
	while(func_line == fgets(func_line, 256, fp)) {
	    char *func_name = func_line;
	    char **strptr = &func_name;
	    char *token = strsep(strptr, ":");
	    flag = token[0];
	    func_name = strsep(strptr, "\n");
#ifdef DEBUG
	    printf("Function Description is %s\n",func_name);
#endif

	    if (flag == 'R') {
		for(i=0; i<transpose_benchmark_count; i++) {
		    if (strcmp(benchmarks_transpose[i].description, func_name) == 0)
			benchmarks_transpose[i].valid = 1;
		}
	    }
	    else if (flag == 'S') {
		for(i=0; i<col_convert_benchmark_count; i++) {
		    if (strcmp(benchmarks_col_convert[i].description, func_name) == 0)
			benchmarks_col_convert[i].valid = 1;
		}
	    }      
	}

	fclose(fp);
    }

    /* 
     * If the user didn't specify a dump file using -f, then 
     * test all of the functions
     */
    else { /* set all valid flags to 1 */
	for (i = 0; i < transpose_benchmark_count; i++)
	    benchmarks_transpose[i].valid = 1;
	for (i = 0; i < col_convert_benchmark_count; i++)
	    benchmarks_col_convert[i].valid = 1;
    }

    /* Set measurement (fcyc) parameters */
    set_fcyc_cache_size(1 << 14); /* 16 KB cache size */
    set_fcyc_clear_cache(1); /* clear the cache before each measurement */
    set_fcyc_compensate(1); /* try to compensate for timer overhead */
 
    for (i = 0; i < transpose_benchmark_count; i++) {
	if (benchmarks_transpose[i].valid)
	    test_transpose(i);
    
}
    for (i = 0; i < col_convert_benchmark_count; i++) {
	if (benchmarks_col_convert[i].valid)
	    test_col_convert(i);
    }


    if (autograder) {
	printf("\nbestscores:%.1f:%.1f:\n", transpose_maxmean, col_convert_maxmean);
    }
    else {
	printf("Summary of Your Best Scores:\n");
	printf("  Transpose:   %3.1f (%s)\n", transpose_maxmean, transpose_maxmean_desc);
	printf("  Col_convert: %3.1f (%s)\n", col_convert_maxmean, col_convert_maxmean_desc);
    }

    return 0;
}













