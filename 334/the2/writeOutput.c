#include "writeOutput.h"
pthread_mutex_t mutexWrite = PTHREAD_MUTEX_INITIALIZER;
struct timeval startTime;

void InitWriteOutput()
{
    gettimeofday(&startTime, NULL);
}

unsigned long long GetTimestamp()
{
    struct timeval currentTime;
    gettimeofday(&currentTime, NULL);
    return (currentTime.tv_sec - startTime.tv_sec) * 1000000 // second
            + (currentTime.tv_usec - startTime.tv_usec); // micro second
}

void PrintThreadId()
{
    pthread_t tid = pthread_self();
    size_t i;
    printf("TID: ");
    for (i=0; i<sizeof(pthread_t); ++i)
        printf("%02x", *(((unsigned char*)&tid) + i));
    printf(", ");
}

void WriteOutput(int shipId, int dockId, int cargoId, Action action)
{
    unsigned long long time = GetTimestamp();
    pthread_mutex_lock(&mutexWrite);

    PrintThreadId();
    printf("SID: %d, DID: %d, CID: %d, time stamp: %llu, ", shipId, dockId, cargoId, time);
    switch (action)
    {
        case CREATE_SHIP:
            // dockId is irrelevant, it will be ignored
            // cargoId is irrelevant, it will be ignored
            printf("AID: 0, The ship created\n");
            break;
        case REQUEST_ENTRY:
            // cargoId is irrelevant, it will be ignored
            printf("AID: 1, The ship requested permission to dock\n");
            break;
        case ENTER_DOCK:
            // cargoId is irrelevant, it will be ignored
            printf("AID: 2, The ship docked\n");
            break;
        case REQUEST_UNLOAD:
            // cargoId is irrelevant, it will be ignored
            printf("AID: 3, The ship requested permission to unload its cargo\n");
            break;
        case UNLOAD_CARGO:
            printf("AID: 4, The ship unloading its cargo\n");
            break;
        case REQUEST_LOAD:
            // cargoId is irrelevant, it will be ignored
            printf("AID: 5, The ship requested permission to load cargo from the dock\n");
            break;
        case LOAD_CARGO:
            printf("AID: 6, The ship loading cargo from the dock\n");
            break;
        case LEAVE_DOCK:
            // cargoId is irrelevant, it will be ignored
            printf("AID: 7, The ship leaved the dock\n");
            break;
        case DESTROY_SHIP:
            // dockId is irrelevant, it will be ignored
            // cargoId is irrelevant, it will be ignored
            printf("AID: 8, The ship destroyed\n");
            break;

        default:
            printf("Unknown action, you called WriteOutput with shipId: %d, dockId: %d, cargoId: %d, action: %d\nPlease check your code.\n", shipId, dockId, cargoId, action);
            break;
    }

    pthread_mutex_unlock(&mutexWrite);
}


