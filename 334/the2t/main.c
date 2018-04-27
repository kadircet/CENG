#include "ship.h"
#include <stdio.h>

cargo **docks, **shipStr;
pthread_mutex_t *countLock, *entryLock, nthLock;
pthread_cond_t *dockLock, *loadLock, *unloadLock, shipDestroy;
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

	scanf("%d%d%d", &Nd, &Ns, &Nc);
	dockLock = (pthread_cond_t*)malloc(sizeof(pthread_cond_t)*Nd);
	loadLock = (pthread_cond_t*)malloc(sizeof(pthread_cond_t)*Nd);
	unloadLock = (pthread_cond_t*)malloc(sizeof(pthread_cond_t)*Nd);
	docks = (cargo**)calloc(sizeof(cargo*), Nd);
	shipStr = (cargo**)calloc(sizeof(cargo*), Ns);
	countLock = (pthread_mutex_t*)malloc(sizeof(pthread_mutex_t)*Nd);
	entryLock = (pthread_mutex_t*)malloc(sizeof(pthread_mutex_t)*Nd);
	dockCap = (int*)malloc(sizeof(int)*Nd);
	loading = (int*)malloc(sizeof(int)*Nd);
	unloading = (int*)malloc(sizeof(int)*Nd);
	dockQ = (int**)malloc(sizeof(int*)*Nd);
	front = (int*)malloc(sizeof(int)*Nd);
	rear = (int*)malloc(sizeof(int)*Nd);
	route = (int**)malloc(sizeof(int*)*Ns);
	tTime = (int*)malloc(sizeof(int)*Ns);
	shipCap = (int*)malloc(sizeof(int)*Ns);
	aTime = (int*)malloc(sizeof(int)*Ns);
	rLen = (int*)malloc(sizeof(int)*Ns);
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);

	pthread_cond_init(&shipDestroy, NULL);
	pthread_mutex_init(&nthLock, NULL);
	for(i=0;i<Nd;i++)
	{
		scanf("%d", dockCap+i);
		if(pthread_cond_init(dockLock+i, NULL)==-1||pthread_cond_init(loadLock+i, NULL)==-1||pthread_cond_init(unloadLock+i, NULL))
		{
			puts("error on pthread_cond_init");
			return 1;
		}
		if(pthread_mutex_init(entryLock+i, NULL)==-1||pthread_mutex_init(countLock+i,NULL)==-1)
		{
			puts("error on pthread_mutex_init");
			return 1;
		}
		dockQ[i] = (int*)malloc(sizeof(int)*Ns);
		front[i]=rear[i]=0;
		loading[i]=unloading[i]=0;
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
		if(pthread_mutex_init(&new->mtx, NULL)==-1)
		{
			puts("error on cargo mutex_init");
			return 1;
		}
		docks[j] = new;
	}

	InitWriteOutput();
	gettimeofday(&simStart, NULL);
	for(i=0;i<Ns;i++)
		pthread_create(&tid, &attr, shipMain, (void*)(long)i);

	pthread_mutex_lock(&nthLock);
	while(nth>0)
		pthread_cond_wait(&shipDestroy, &nthLock);
	pthread_mutex_unlock(&nthLock);

	pthread_cond_destroy(&shipDestroy);
	pthread_mutex_destroy(&nthLock);
	for(i=0;i<Nd;i++)
	{
		pthread_cond_destroy(dockLock+i);
		pthread_cond_destroy(loadLock+i);
		pthread_cond_destroy(unloadLock+i);
		pthread_mutex_destroy(entryLock+i);
		pthread_mutex_destroy(countLock+i);
		free(dockQ[i]);
		cargo *it=docks[i];
		while(it!=NULL)
		{
			docks[i]=it;
			it=it->next;
			pthread_mutex_destroy(&docks[i]->mtx);
			free(docks[i]);
		}
	}
	for(i=0;i<Ns;i++)
	{
		free(route[i]);
		cargo *it=shipStr[i];
		while(it!=NULL)
		{
			shipStr[i]=it;
			it=it->next;
			free(shipStr[i]);
		}
	}
	free(route);
	free(countLock);
	free(entryLock);
	free(dockLock);
	free(loadLock);
	free(unloadLock);
	free(docks);
	free(shipStr);
	free(tTime);
	free(aTime);
	free(shipCap);
	free(rLen);
	free(dockQ);
	free(dockCap);
	free(front);
	free(rear);
	free(loading);
	free(unloading);
	return 0;
}
