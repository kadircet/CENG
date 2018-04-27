#include "ship.h"
#include <errno.h>
#include <string.h>

void shipRoutine(int *route, int rLen, 
		int cap, int tTime, int aTime, int sid);

void* shipMain(void* arg)
{
	int id = (int)(long)arg;
	pthread_mutex_lock(&nthLock);
	nth++;
	pthread_mutex_unlock(&nthLock);
	shipRoutine(route[id], rLen[id], shipCap[id], tTime[id], aTime[id], id);
	return NULL;
}

void dockExit(int sid, int did)
{
	WriteOutput(sid, did, 0, LEAVE_DOCK);
}

void enter(int sid, int did)
{
	pthread_mutex_lock(entryLock+did);
	WriteOutput(sid, did, 0, REQUEST_ENTRY);
	dockQ[did][rear[did]++]=sid;
	if(rear[did]==Ns)
		rear[did]=0;
	pthread_mutex_unlock(entryLock+did);
	while(1)
	{
		sem_wait(dockLock+did);
		pthread_mutex_lock(entryLock+did);
		if(dockQ[did][front[did]]==sid)
		{
			front[did]++;
			if(front[did]==Ns)
				front[did]=0;
			pthread_mutex_unlock(entryLock+did);
			break;
		}
		sem_post(dockLock+did);
		pthread_mutex_unlock(entryLock+did);
	}
	WriteOutput(sid, did, 0, ENTER_DOCK);
}

void unload(int sid, int did, cargo **str, int *cap)
{
	cargo *it, *itp;
	int i;

	WriteOutput(sid, did, 0, REQUEST_UNLOAD);
	pthread_mutex_lock(countLock+did);
	if(loading[did]>0)
	{
		waitUnload[did]++;
		pthread_mutex_unlock(countLock+did);
		while(1)
		{
			pthread_mutex_lock(countLock+did);
			if(loading[did]==0)
				break;
			pthread_mutex_unlock(countLock+did);
			sem_wait(unloadLock+did);
		}
		waitUnload[did]--;
	}
	unloading[did]++;
	pthread_mutex_unlock(countLock+did);

	for(it=*str;it;it=it->next)
		if(it->dest==did && it->state==1)
		{
			it->state=2;
			(*cap)++;
			WriteOutput(sid, did, it->id, UNLOAD_CARGO);
			usleep(2*1000);
		}

	pthread_mutex_lock(countLock+did);
	if(--unloading[did]==0)
		for(i=0;i<waitLoad[did];i++)
			sem_post(loadLock+did);
	pthread_mutex_unlock(countLock+did);
}

int contains(int *route, int dest, int len, int cur)
{
	while(len-1>cur)
		if(route[--len]==dest)
			return 1;
	return 0;
}

void load(int sid, int did, int *route, cargo **str, int *pcap, int rLen, int cur)
{
	struct timespec lockWait;
	char timedout=0;
	cargo *storage = *str, *it, *itp;
	int i;
	int cap = *pcap;

	if(cap==0)
		return;
	clock_gettime(CLOCK_REALTIME, &lockWait);
	lockWait.tv_nsec+=20*1000000;
	lockWait.tv_sec+=lockWait.tv_nsec/1000000000;
	lockWait.tv_nsec%=1000000000;

	WriteOutput(sid, did, 0, REQUEST_LOAD);
	pthread_mutex_lock(countLock+did);
	if(unloading[did]>0)
	{
		waitLoad[did]++;
		pthread_mutex_unlock(countLock+did);
		while(1)
		{
			pthread_mutex_lock(countLock+did);
			if(unloading[did]==0)
				break;
			pthread_mutex_unlock(countLock+did);
			if(sem_timedwait(loadLock+did, &lockWait)==-1 && errno==ETIMEDOUT)
			{
				timedout=1;
				break;
			}
		}
		waitLoad[did]--;
	}
	if(timedout==0)
	{
		loading[did]++;
		pthread_mutex_unlock(countLock+did);
		printf("%d no timeout\n", sid);
	}
	else
	{
		printf("%d timeout\n", sid);
		return;
	}

	for(it=docks[did];it!=NULL;it=it->next)
		if(contains(route, it->dest, rLen, cur))
		{
			pthread_mutex_lock(&it->mtx);
			if(it->state==0)
			{
				it->state=1;
				pthread_mutex_unlock(&it->mtx);
				WriteOutput(sid, did, it->id, LOAD_CARGO);
				usleep(3000);

				itp = (cargo*)malloc(sizeof(cargo));
				itp->next=*str;
				itp->dest=it->dest;
				itp->id=it->id;
				itp->state=it->state;
				*str = itp;
				cap--;
				if(cap==0)
					break;
			}
			else
				pthread_mutex_unlock(&it->mtx);
		}

	*pcap=cap;
	pthread_mutex_lock(countLock+did);
	if(--loading[did]==0)
		for(i=0;i<waitUnload[did];i++)
			sem_post(unloadLock+did);
	pthread_mutex_unlock(countLock+did);
}

void shipRoutine(int *route, int rLen, 
		int cap, int tTime, int aTime, int sid)
{
	cargo *it;
	int i;
	struct timeval aWait, cTime;

	WriteOutput(sid, 0, 0, CREATE_SHIP);
	for(i=0;i<rLen;i++)
	{
		if(i==0)
		{
			gettimeofday(&cTime, NULL);
			timersub(&cTime, &simStart, &aWait);
			aTime=aTime*1000-(aWait.tv_usec+aWait.tv_sec*1000000);
			if(aTime<0)
				aTime=0;
		}
		usleep(i==0?aTime:tTime*1000);
		enter(sid, route[i]);
		for(it=shipStr[sid];it!=NULL;it=it->next)
			if(it->dest==route[i] && it->state==1)
			{
				unload(sid, route[i], shipStr+sid, &cap);
				break;
			}
		if(i!=rLen-1)
		{
			load(sid, route[i], route, shipStr+sid, &cap, rLen, i);
			dockExit(sid, route[i]);
		}
		sem_post(dockLock+route[i]);
	}
	WriteOutput(sid, 0, 0, DESTROY_SHIP);
	pthread_mutex_lock(&nthLock);
	nth--;
	pthread_mutex_unlock(&nthLock);
	sem_post(&shipDestroy);
}

