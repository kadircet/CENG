#include "ship.h"
#include <errno.h>

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

void enter(int sid, int did)
{
	pthread_mutex_lock(entryLock+did);
	WriteOutput(sid, did, 0, REQUEST_ENTRY);
	dockQ[did][rear[did]++]=sid;
	if(rear[did]==Ns)
		rear[did]=0;
	while(dockCap[did]==0 || dockQ[did][front[did]]!=sid)
		pthread_cond_wait(dockLock+did, entryLock+did);
	front[did]++;
	if(front[did]==Ns)
		front[did]=0;
	dockCap[did]--;
	WriteOutput(sid, did, 0, ENTER_DOCK);
	pthread_mutex_unlock(entryLock+did);
}

void unload(int sid, int did, cargo **str, int *cap)
{
	cargo *it, *itp;
	int i;

	WriteOutput(sid, did, 0, REQUEST_UNLOAD);
	pthread_mutex_lock(countLock+did);
	while(loading[did]>0)
		pthread_cond_wait(unloadLock+did, countLock+did);
	unloading[did]++;
	pthread_mutex_unlock(countLock+did);

	for(it=*str;it;it=it->next)
		if(it->dest==did && it->state==1)
		{
			it->state=2;
			(*cap)++;
			WriteOutput(sid, did, it->id, UNLOAD_CARGO);
			usleep(2000);
		}

	pthread_mutex_lock(countLock+did);
	if(--unloading[did]==0)
		pthread_cond_broadcast(loadLock+did);
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
	while(unloading[did]>0)
		if(pthread_cond_timedwait(loadLock+did, countLock+did, &lockWait)!=0)
		{
		//	puts("timedout");
			timedout=1;
			break;
		}
	if(timedout==0)
	{
		loading[did]++;
		pthread_mutex_unlock(countLock+did);
	}
	else
	{
		pthread_mutex_unlock(countLock+did);
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
		pthread_cond_broadcast(unloadLock+did);
		//for(i=0;i<waitUnload[did];i++)
		//	sem_post(unloadLock+did);
	pthread_mutex_unlock(countLock+did);
}

void shipRoutine(int *route, int rLen, 
		int cap, int tTime, int aTime, int sid)
{
	cargo *it;
	int i;
	struct timeval aWait, cTime;

	WriteOutput(sid, 0, 0, CREATE_SHIP);
	gettimeofday(&cTime, NULL);
	timersub(&cTime, &simStart, &aWait);
	aTime=aTime*1000-(aWait.tv_usec+aWait.tv_sec*1000000);
	if(aTime<0)
		aTime=0;
	usleep(aTime);
	for(i=0;i<rLen;i++)
	{
		usleep((i>0)*tTime*1000);
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
			WriteOutput(sid, route[i], 0, LEAVE_DOCK);
			pthread_mutex_lock(entryLock+route[i]);
			dockCap[route[i]]++;
			pthread_cond_broadcast(dockLock+route[i]);
			pthread_mutex_unlock(entryLock+route[i]);
		}
	}
	WriteOutput(sid, 0, 0, DESTROY_SHIP);
	pthread_mutex_lock(&nthLock);
	nth--;
	pthread_mutex_unlock(&nthLock);
	sem_post(&shipDestroy);
}

