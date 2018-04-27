#include <stdio.h>

int inplen,res[50];
char *digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

typedef struct _bigint
{
	int nbits;
	unsigned char val[50];
} bigint;

int isZero(bigint A)
{
	int i;
	for(i=0;i<A.nbits;i++)
		if(A.val[i]!=0)
			return 0;
	return 1;
}

void print(bigint A)
{
	int i;
	for(i=0;i<A.nbits;i++)
		printf("%d", A.val[i]);
	puts("");
}

bigint div(bigint A, int d, int *rem)
{
	bigint Q;
	int D=0,i;
	Q.nbits=A.nbits;
	for(i=0;i<Q.nbits;i++)Q.val[i]=0;
	for(i=0;i<A.nbits;i++)
	{
		D*=10;
		D+=A.val[i];
		Q.val[i]=D/d;
		D%=d;
	}
	*rem=D;
	return Q;
}

int main()
{
	int i,j;
	bigint A;
	inplen=0;
	while(scanf("%c",&A.val[inplen])!=-1 && A.val[inplen]!='\n')
		A.val[inplen++]-='0';
	A.nbits=inplen;
	i=0;
	while(!isZero(A))
	{
		A=div(A,i+++2,&j);
		res[i-1]=j;
	}
	while(i>0)
		printf("%c", digits[res[--i]]);
	return 0;
}

