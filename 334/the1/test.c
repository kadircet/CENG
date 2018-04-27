#include <unistd.h>

int main()
{
	char *args[] = {"ls", NULL};
	execvp("ls", args);
}
