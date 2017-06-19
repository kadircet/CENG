#ifndef WRITE_OUTPUT_H
#define WRITE_OUTPUT_H

#include <stdio.h>
#include <pthread.h>
#include <sys/time.h>

typedef enum Action {
    CREATE_SHIP,
    REQUEST_ENTRY,
    ENTER_DOCK,
    REQUEST_UNLOAD,
    UNLOAD_CARGO,
    REQUEST_LOAD,
    LOAD_CARGO,
    LEAVE_DOCK,
    DESTROY_SHIP
    } Action;


void InitWriteOutput();
unsigned long long GetTimestamp();
void PrintThreadId();
void WriteOutput(int shipId, int dockId, int cargoId, Action action);

#endif 

