#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define isUpper(x) ((x)<='Z' && (x)>='A')
#define isDigit(x) ((x)<='9' && (x)>='0')
#define NAME(x) ((x)->name)
#define TYPE(x) ((x)->type)
#define DATA(x) ((x)->data)
#define HEAD(x) (DATA((x)).head)
#define PRICE(x) (DATA((x)).price)
#define QUANT(x) ((x)->quantity)
#define PART(x) ((x)->p)
#define NEXT(x) ((x)->next)

typedef struct _part
{
	char *name;
	int type;
	union
	{
		double price;
		struct _item *head;
	} data;
} part;

typedef struct _item
{
	int quantity;
	struct _part *p;
	struct _item *next;
} item;

part *readPart();
void printPart(part*, int, int);
void freePart(part *p);

char *readName()
{
	char *res=(char*)malloc(sizeof(char)*21);
	int i=-1;
	do
	{
		i++;
		scanf(" %c", res+i);
	}while(isUpper(res[i]));
	ungetc(res[i], stdin);
	res[i]=0;
	return res;
}

item *readList()
{
	item *res=(item*)malloc(sizeof(item));
	char tmp;
	int quantity;
	scanf(" %c", &tmp);
	ungetc(tmp, stdin);
	quantity=1;
	if(isDigit(tmp))
		scanf("%d %c", &quantity, &tmp);
	PART(res)=readPart();
	QUANT(res)=quantity;
	scanf(" %c", &tmp);
	if(tmp==',')
		NEXT(res)=readList();
	else
		NEXT(res)=NULL;
	return res;
}

part *readPart()
{
	part *res=(part*)malloc(sizeof(part));
	char *name=readName(), tmp;
	NAME(res)=name;
	scanf(" %c", &tmp);
	TYPE(res)=2;
	if(tmp=='(') /* composite part */
	{
		TYPE(res)=1;
		HEAD(res)=readList();
	}
	else if(tmp=='[') /* basic part */
	{
		TYPE(res)=0;
		scanf("%lf %c", &PRICE(res), &tmp);
	}
	else if(tmp==',') /* list of items */
		ungetc(tmp, stdin);
	else if(tmp==')')
		ungetc(tmp, stdin);
	return res;
}

void printList(item *it, int ind)
{
	while(it!=NULL)
	{
		printPart(PART(it), ind, QUANT(it));
		it=NEXT(it);
	}
}

void printPart(part *p, int ind, int q)
{
	int i;
	for(i=0;i<ind;i++)
		printf("\t");
	if(q!=0)
		printf("%d*", q);
	printf("%s ", NAME(p));
	if(TYPE(p)==0)
		printf("[ %f ]\n", PRICE(p));
	else if(TYPE(p)==1)
	{
		printf("(\n");
		printList(HEAD(p), ind+1);
		for(i=0;i<ind;i++) printf("\t");
		printf(")\n");
	}
	else puts("");
}

part *findDef(part *root, char *name)
{
	item *it=HEAD(root);
	part *p;
	if(!strcmp(NAME(root),name) && TYPE(root)!=2)
		return root;
	if(TYPE(root)==1)
		while(it!=NULL)
		{
			if((p=findDef(PART(it), name)))
				return p;
			it=NEXT(it);
		}
	return NULL;
}

double getPrice(part *p, part *root)
{
	item *it=HEAD(p);
	part *tmp;
	double res=0;
	if(p==NULL)
		return 0;
	if(TYPE(p)==0)
		return PRICE(p);
	if(TYPE(p)==1)
	{
		while(it!=NULL)
		{
			res+=QUANT(it)*getPrice(PART(it), root);
			it=NEXT(it);
		}
		return res;
	}
	tmp=findDef(root, NAME(p));
	TYPE(p)=0;
	PRICE(p)=getPrice(tmp, root);
	return PRICE(p);
}

void freeList(item *head)
{
	if(head==NULL)
		return;
	freePart(PART(head));
	freeList(NEXT(head));
	free(head);
}

void freePart(part *p)
{
	if(p==NULL)
		return;
	free(NAME(p));
	if(TYPE(p)==1)
		freeList(HEAD(p));
	free(p);
}

int main()
{
	part *p=readPart();
	printPart(p, 0, 0);
	printf("%f\n", getPrice(p, p));
	freePart(p);
	return 0;
}

