#include "the1.c"
extern void exit(int);

int _main()
{
	while(!feof(stdin))
	{
		main();
		puts("");
	}	
	exit(0);
}

