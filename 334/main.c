#include "parser.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <signal.h>

typedef struct _cinfo
{
	int pid;
	struct _cinfo *next;
} cinfo;

cinfo *nc, *chead=NULL, *np;
int interrupted=0;

void handleSignal(int sig, siginfo_t *si, void *ctx)
{
	int cstatus=0;
	switch(sig)
	{
		case SIGCHLD:
			for(nc=chead,np=chead;nc;np=nc,nc=nc->next)
				if(waitpid(nc->pid, &cstatus, WNOHANG)==nc->pid)
				{
					printf("[%d] retval: %d\n", nc->pid, WEXITSTATUS(cstatus));
					np->next=nc->next;
					if(nc==chead)
						chead=nc->next;
					free(nc);
				}
			waitpid(si->si_pid, NULL, 0);
			break;
		case SIGINT:
			interrupted=1;
			break;
	}
}

void lbp()
{
	int pids[41],i;
	for(i=0,nc=chead;nc;nc=nc->next,i++)
		pids[i] = nc->pid;
	while(i>0)
		printf("[%d]\n", pids[--i]);
}

int main(int argc, char **argv)
{
	int pipefd[41][2], i, j;
	char line[513];
	input *inp;
	char *args[21];
	int cpid, cstatus, cpids[41], quit=0;
	command cmd;
	struct sigaction sVal;

	sVal.sa_flags = SA_SIGINFO;
	sVal.sa_sigaction = handleSignal;
	sigaction(SIGCHLD, &sVal, NULL);
//	if(argc==1)
		sigaction(SIGINT, &sVal, NULL);

	while(quit==0)
	{
		if(argc==1)
			printf("> ");
		fflush(stdout);

		while(argc==1 && fgets(line, 512, stdin)==NULL)
			if(feof(stdin))
				quit=1;
		if(quit)
			break;
		if(argc>1)
			strcpy(line, argv[1]);

		inp = parse(line);
		//print_input(inp);
		line[0]=0;
		if(inp->num_of_commands>0 && inp->del=='|')
			for(int i=0;i<inp->num_of_commands;i++)
				pipe(pipefd[i]);
		interrupted=0;
		for(int i=0;i<inp->num_of_commands && interrupted==0;i++)
		{
			cmd = inp->commands[i];
			if(cmd.type==NORMAL && !strcmp(cmd.info.com->name, "quit"))
			{
				quit=1;
				break;
			}
			else if(cmd.type==NORMAL && !strcmp(cmd.info.com->name, "lbp"))
			{
				lbp();
				continue;
			}
			else if(cmd.input && inp->del=='|' && i==0)
			{
				cpid=open(cmd.input, O_RDONLY); 
				if(cpid==-1)
				{
					if(errno==ENOENT)
						printf("%s not found\n", cmd.input);
					break;
				}
			}

			cpid=fork();
			if(cpid==0)
			{
				if(inp->background)
					setpgid(getpid(), getpid());
				if((inp->del==';' || i==0) && cmd.input)
				{
					cpid=open(cmd.input, O_RDONLY); 
					if(cpid==-1)
					{
						if(errno==ENOENT)
							printf("%s not found\n", cmd.input);
						_exit(errno);
					}
					dup2(cpid, STDIN_FILENO);
					close(cpid);
				}
				if(i>0 && inp->del=='|')
				{
					dup2(pipefd[i-1][0], STDIN_FILENO);
					close(pipefd[i][0]);
				}
				if(i<inp->num_of_commands-1 && inp->del=='|')
				{
					dup2(pipefd[i][1], STDOUT_FILENO);
					close(pipefd[i][0]);
					close(pipefd[i][1]);
				}
				if((inp->del==';' || i==inp->num_of_commands-1) && cmd.output)
				{
					cpid=open(cmd.output, O_DSYNC|O_CREAT|O_WRONLY|O_TRUNC, 0666);
					if(cpid==-1)
					{
						printf("%d %s\n", errno, strerror(errno));
						_exit(errno);
					}
					dup2(cpid, STDOUT_FILENO);
					close(cpid);
				}
				cpid=0;
				if(cmd.type==NORMAL)
				{
					args[cpid] = cmd.info.com->name;
					while(cpid<cmd.info.com->num_of_args)
						args[cpid+1]=cmd.info.com->arguments[cpid++];
					args[cpid+1]=NULL;
					execvp(cmd.info.com->name, args);
				}
				else
				{
					args[0] = argv[0];
					args[1] = cmd.info.subshell;
					args[2] = NULL;
					execv(args[0], args);
				}
				printf("%d %s\n", errno, strerror(errno));
				_exit(errno);
			}
			if(inp->num_of_commands>0 && inp->del=='|')
				close(pipefd[i][1]);
			cpids[i] = cpid;
			if(inp->background==0 && (i==inp->num_of_commands-1 || inp->del==';'))
				for(j=0;j<=i;j++)
					waitpid(cpids[j], NULL, 0);
			else if(inp->background)
			{
				nc = (cinfo*)malloc(sizeof(cinfo));
				nc->next=chead;
				nc->pid=cpid;
				chead=nc;
			}
		}
		clear_input(inp);
		if(argc>1)
			break;
	}
	while(chead!=NULL);
	return 0;
}

