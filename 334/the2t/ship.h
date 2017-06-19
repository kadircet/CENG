#ifndef _SHIP_H_
#define _SHIP_H_

#include "writeOutput.h"
#include <pthread.h>
#include <stdlib.h>
#include <semaphore.h>
#include <unistd.h>

typedef struct _cargo
{
	struct _cargo *next;
	int dest, id;
	pthread_mutex_t mtx;
	char state;
} cargo;

extern cargo **docks, **shipStr;
extern pthread_mutex_t *countLock, *entryLock, nthLock;
extern pthread_cond_t *dockLock, *loadLock, *unloadLock, shipDestroy;
extern int *loading, *unloading;
void* shipMain(void*);
extern int nth, Ns;
extern int *dockCap, **route, *tTime, *shipCap, *aTime, *rLen;
extern struct timeval simStart;
extern int **dockQ, *front, *rear;

#endif
