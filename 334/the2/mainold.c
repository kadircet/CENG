#include "ship.h"
#include <stdio.h>

cargo **docks, **shipStr;
pthread_mutex_t *countLock, *entryLock, nthLock;
sem_t *loadLock, *unloadLock, *dockLock, shipDestroy;
int *waitLoad, *waitUnload, *loading, *unloading;
int *dockCap, **route, *tTime, *shipCap, *aTime, *rLen;
void* shipMain(void*);
int nth=0;
struct timeval simStart;
int **dockQ, *front, *rear;
int Nd, Ns, Nc;

int main()
{
	int i,j;
	pthread_t tid;
	pthread_attr_t attr;

	sem_init(&shipDestroy, 0, 0);
	pthread_mutex_init(&nthLock, NULL);

	scanf("%d%d%d", &Nd, &Ns, &Nc);
	dockLock = (sem_t*)malloc(sizeof(sem_t)*Nd);
	loadLock = (sem_t*)malloc(sizeof(sem_t)*Nd);
	unloadLock = (sem_t*)malloc(sizeof(pthread_mutex_t)*Nd);
	loadLock = (sem_t*)malloc(sizeof(pthread_mutex_t)*Nd);
	docks = (cargo**)calloc(sizeof(cargo*), Nd);
	shipStr = (cargo**)calloc(sizeof(cargo*), Ns);
	countLock = (pthread_mutex_t*)malloc(sizeof(pthread_mutex_t)*Nd);
	entryLock = (pthread_mutex_t*)malloc(sizeof(pthread_mutex_t)*Nd);
	dockCap = (int*)malloc(sizeof(int)*Nd);
	loading = (int*)malloc(sizeof(int)*Nd);
	waitLoad = (int*)malloc(sizeof(int)*Nd);
	dockQ = (int**)malloc(sizeof(int*)*Nd);
	front = (int*)malloc(sizeof(int)*Nd);
	rear = (int*)malloc(sizeof(int)*Nd);
	waitUnload = (int*)malloc(sizeof(int)*Nd);
	unloading = (int*)malloc(sizeof(int)*Nd);
	route = (int**)malloc(sizeof(int*)*Ns);
	tTime = (int*)malloc(sizeof(int)*Ns);
	shipCap = (int*)malloc(sizeof(int)*Ns);
	aTime = (int*)malloc(sizeof(int)*Ns);
	rLen = (int*)malloc(sizeof(int)*Ns);
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);

	for(i=0;i<Nd;i++)
	{
		scanf("%d", dockCap+i);
		if(sem_init(dockLock+i, 0, dockCap[i])==-1)
		{
			puts("error on sem_init");
			return 1;
		}
		dockQ[i] = (int*)malloc(sizeof(int)*Ns);
		front[i]=0;
		rear[i]=0;
		sem_init(loadLock+i, 0, 0);
		pthread_mutex_init(entryLock+i, NULL);
		pthread_mutex_init(countLock+i, NULL);
	}

	for(i=0;i<Ns;i++)
	{
		scanf("%d%d%d%d", tTime+i, shipCap+i, aTime+i, rLen+i);
		route[i] = (int*)malloc(sizeof(int)*rLen[i]);
		for(j=0;j<rLen[i];j++)
			scanf("%d", route[i]+j);
	}

	for(i=0;i<Nc;i++)
	{
		scanf("%d", &j);
		cargo *new = malloc(sizeof(cargo));
		scanf("%d", &new->dest);
		new->id=i;
		new->next=docks[j];
		new->state=0;
		pthread_mutex_init(&new->mtx, NULL);
		docks[j] = new;
	}

	InitWriteOutput();
	gettimeofday(&simStart, NULL);
	for(i=0;i<Ns;i++)
		pthread_create(&tid, &attr, shipMain, (void*)(long)i);

	while(1)
	{
		sem_wait(&shipDestroy);
		pthread_mutex_lock(&nthLock);
		if(nth==0)
		{
			pthread_mutex_unlock(&nthLock);
			break;
		}
		pthread_mutex_unlock(&nthLock);
	}

	for(i=0;i<Nd;i++)
		sem_destroy(dockLock+i);
	sem_destroy(&shipDestroy);
	pthread_mutex_destroy(&nthLock);

	return 0;
}
