#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char inp[210];

char **split(char *seq)
{
	char **res=(char**)malloc(sizeof(char*)*2);
	int ll,lr,i;
	for(ll=0;seq[ll]!='#';ll++);
	res[0] = (char*)malloc(sizeof(char)*(ll+1));
	for(lr=0;seq[ll+lr+1];lr++);
	res[1] = (char*)malloc(sizeof(char)*(lr+1));
	for(i=0;i<ll;i++)
		res[0][i]=seq[i];
	res[0][i]=0;
	for(i=0;i<lr;i++)
		res[1][i]=seq[ll+1+i];
	res[1][i]=0;
	return res;
}

char **getFormulas(char *side, int *n)
{
	char **res;
	int fl,i,j;
	*n=1;
	for(i=0;side[i];i++)
		if(side[i]==',')
			(*n)++;
	res=(char**)malloc(sizeof(char*)**n);
	for(i=0;*side;i++)
	{
		if(*side==',')side++;
		for(fl=0;side[fl]&&side[fl]!=',';fl++);
		res[i]=(char*)malloc(sizeof(char)*(fl+1));
		for(j=0;j<fl;j++)
			res[i][j]=*side++;
	}
	return res;
}

#define isOp(c) ((c)=='|' || (c)=='&' || (c)=='>')
char** parseFormula(char *formula, char *op)
{
	char **res=(char**)malloc(sizeof(char*)*2);
	int i,state,ll;
	if(formula[0]=='-')
	{
		for(i=0,state=0;formula[i];i++)
		{
			if(formula[i]=='(')
				state++;
			else if(formula[i]==')')
				state--;
			else if(state>0 || !isOp(formula[i]))
				continue;
			else
				break;
		}
		if(!formula[i])
		{
			ll=i;
			*op='-';
			res[0]=(char*)malloc(sizeof(char)*(ll+1));
			for(i=1;i<ll;i++)
				res[0][i-1]=formula[i];
			res[0][i-1]=0;
			res[1]=NULL;
			return res;
		}
	}
	for(i=0,state=0;formula[i];i++)
		if(formula[i]=='(')
			state++;
		else if(formula[i]==')')
			state--;
		else if(state==(formula[0]=='(') && isOp(formula[i]))
		{
			*op=formula[i];
			break;
		}
	ll=i;
	res[0]=(char*)malloc(sizeof(char)*(ll+1));
	for(i=0;i<ll;i++)
		res[0][i]=formula[i+(formula[0]=='(')];
	res[0][ll-(formula[0]=='(')]=0;
	if(!formula[i])
	{
		*op=0;
		res[1]=NULL;
		return res;
	}
	for(i=0;formula[ll+i+1];i++);
	res[1]=(char*)malloc(sizeof(char)*(i+1));
	for(i=0;formula[ll+i+1];i++)
		res[1][i]=formula[ll+i+1];
	res[1][i-1+(formula[0]!='(')]=0;
	return res;
}

int hasSolven(char **lhs, int llen, char** rhs, int rlen)
{
	int i,j;
	for(i=0;i<llen;i++)
		for(j=0;j<rlen;j++)
			if(!strcmp(lhs[i], rhs[j]))
				return 1;
	for(i=0;i<llen;i++)
		if(lhs[i][1])
			return -1;
	for(i=0;i<rlen;i++)
		if(rhs[i][1])
			return -1;
	return 0;
}

void move(int pos, char ***from, int *flen, char ***to, int *tlen)
{
	char *str=(*from)[pos];
	int i;
	for(i=0;str[i+1];i++)
		str[i]=str[i+1];
	str[i]=0;
	for(i=pos;i<*flen-1;i++)
		(*from)[i]=(*from)[i+1];
	(*flen)--;
	*to=(char**)realloc(*to, sizeof(char*)*((*tlen)+1));
	(*to)[*tlen]=str;
	(*tlen)++;
}

void freeF(char **f, int len)
{
	int i;
	for(i=0;i<len;i++)
		free(f[i]);
	free(f);
}

char **copy(char **f, int len)
{
	int i;
	char** res=(char**)malloc(sizeof(char*)*len);
	for(i=0;i<len;i++)
	{
		res[i] = (char*)malloc(sizeof(char)*(strlen(f[i])+1));
		strcpy(res[i], f[i]);
	}
	return res;
}

int prove(char **lhs, int llen, char **rhs, int rlen)
{
	int i;
	char **formula, op, *old, **nlhs, **nrhs;
	while(hasSolven(lhs, llen, rhs, rlen)==-1)
	{
		for(i=0;i<llen;i++)
		{
			formula=parseFormula(lhs[i], &op);
			if(op=='-')
				move(i, &lhs, &llen, &rhs, &rlen),i--;
			else if(op=='&')
			{
				free(lhs[i]);
				lhs[i]=formula[0];
				lhs=(char**)realloc(lhs, sizeof(char*)*(llen+1));
				lhs[llen++]=formula[1];
				i--;
			}
			else if(op=='>')
			{
				old=lhs[i];
				lhs[i]=(char*)malloc(sizeof(char)*(strlen(lhs[i])+3));
				free(old);
				strcpy(lhs[i], "-");
				strcat(lhs[i], formula[0]);
				strcat(lhs[i], "|");
				strcat(lhs[i], formula[1]);
				free(formula[0]);
				free(formula[1]);
				i--;
			}
			free(formula);
		}
		for(i=0;i<rlen;i++)
		{
			formula=parseFormula(rhs[i], &op);
			if(op=='-')
				move(i, &rhs, &rlen, &lhs, &llen),i--;
			else if(op=='|')
			{
				free(rhs[i]);
				rhs[i]=formula[0];
				rhs=(char**)realloc(rhs, sizeof(char*)*(rlen+1));
				rhs[rlen++]=formula[1];
				i--;
			}
			else if(op=='>')
			{
				old=rhs[i];
				rhs[i]=(char*)malloc(sizeof(char)*(strlen(rhs[i])+3));
				free(old);
				strcpy(rhs[i], "-");
				strcat(rhs[i], formula[0]);
				strcat(rhs[i], "|");
				strcat(rhs[i], formula[1]);
				free(formula[0]);
				free(formula[1]);
				i--;
			}
			free(formula);
		}
		for(i=0;i<llen;i++)
		{
			formula=parseFormula(lhs[i], &op);
			if(op=='|')
			{
				nlhs=copy(lhs, llen);
				free(nlhs[i]);
				nlhs[i]=formula[0];
				nrhs=copy(rhs, rlen);
				op=prove(nlhs, llen, nrhs, rlen);
				if(op==1)
				{
					nlhs=copy(lhs, llen);
					free(nlhs[i]);
					nlhs[i]=formula[1];
					nrhs=copy(rhs, rlen);
					op=prove(nlhs, llen, nrhs, rlen);
				}
				freeF(lhs, llen);
				freeF(rhs, rlen);
				return op;
			}
		}
		for(i=0;i<rlen;i++)
		{
			formula=parseFormula(rhs[i], &op);
			if(op=='&')
			{
				nlhs=copy(lhs, llen);
				nrhs=copy(rhs, rlen);
				free(nrhs[i]);
				nrhs[i]=formula[0];
				op=prove(nlhs, llen, nrhs, rlen);
				if(op==1)
				{
					nlhs=copy(lhs, llen);
					nrhs=copy(rhs, rlen);
					free(nrhs[i]);
					nrhs[i]=formula[1];
					op=prove(nlhs, llen, nrhs, rlen);
				}
				freeF(lhs, llen);
				freeF(rhs, rlen);
				return op;
			}
		}
	}
	i=hasSolven(lhs, llen, rhs, rlen);
	freeF(lhs, llen);
	freeF(rhs, rlen);
	return i;
}

int main()
{
	int llen,rlen;
	char **sides,**lhs,**rhs;
	scanf("%s", inp);
	sides=split(inp);
	lhs=getFormulas(sides[0],&llen);
	rhs=getFormulas(sides[1],&rlen);
	llen=prove(lhs, llen, rhs, rlen);
	if(llen)
		puts("T");
	else
		puts("F");
	return 0;
}

