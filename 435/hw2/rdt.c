#include "rdt.h"
#define SYN 1
#define ACK 2
#define INF 4
#define BUFSIZE 65536
#define PORT 1337
#define ALARMTIME 3

typedef struct _package
{
	short syn;
	short ack;
	unsigned short flags;
	unsigned short size;
	unsigned short chksum;
	unsigned short empty;
} package;

typedef struct _packet
{
	unsigned short size;
	void* data;
} packet;

#define MAXPAY (1000-sizeof(struct package))

volatile char running=0, expired=0;
pthread_mutex_t locksbuffer, lockrbuffer;
short base=0,end=0;
short wndsize = 128;
short eack=0;
packet sbuffer[BUFSIZE], rbuffer[BUFSIZE];
unsigned short rbf=0, rbr=0;
int socketd, sendsock;
char **dests;
int NIPS;

void fatal(char *s)
{
	puts(s);
	exit(1);
}

void timerexpire(int sig)
{
	expired=1;
}

void init(char *SRV_IP)
{
	pthread_mutex_init(&locksbuffer, NULL);
	pthread_mutex_init(&lockrbuffer, NULL);

	running = 1;
	struct sockaddr_in saddr;
	socketd=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	sendsock=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if(socketd==-1)
		fatal("socket");
	memset((char*)&saddr, 0, sizeof(saddr));
	saddr.sin_family = AF_INET;
	saddr.sin_port = htons(PORT);
	inet_aton(SRV_IP, &saddr.sin_addr);
	if(bind(socketd, &saddr, sizeof(saddr))==-1)
		fatal("bind");
	startsender();
	startreceiver();
}

unsigned short chksum(unsigned char* pkg, int size)
{
	unsigned short res=size%2?pkg[size]:0;
	unsigned char* pkend = pkg+size;
	for(;pkg<=pkend;pkg+=2)
		res ^= (((unsigned short)*pkg)<<8) | *(pkg+1);
	return res;
}

packet* mkpkg(char* data, int size, int *count, int syn)
{
	packet *res;
	package pkg;
	packet pkt;
	*count = (size+MAXPAY-1)/MAXPAY;
	res = malloc(sizeof(packet)**count);
	*count=0;
	while(size>MAXPAY)
	{
		pkt.size = MAXPAY+sizeof(package);
		pkt.data = malloc(pkt.size);

		pkg.syn = syn++;
		pkg.size = MAXPAY;
		pkg.flags = SYN;
		pkg.chksum = chksum(data, pkg.size);
		pkg.empty = (BUFSIZE-rbr+rbf)%BUFSIZE;

		memcpy(pkt.data, &pkg, sizeof(package));
		memcpy(pkt.data+sizeof(package), (void*)data, pkg.size);

		res[(*count)++] = pkt;
		size-=MAXPAY;
		data+=MAXPAY;
	}
	if(size>0)
	{
		pkt.size = size+sizeof(package);
		pkt.data = malloc(pkt.size);

		pkg.syn = syn++;
		pkg.size = size;
		pkg.flags = SYN;
		pkg.chksum = chksum(data, pkg.size);
		pkg.empty = (BUFSIZE-rbr+rbf)%BUFSIZE;

		memcpy(pkt.data, &pkg, sizeof(package));
		memcpy(pkt.data+sizeof(package), (void*)data, pkg.size);

		res[(*count)++] = pkt;
	}
	return res;
}

packet mkack(int syn)
{
	packet res;
	package pkg;
	
	res.size = sizeof(package);
	res.data = malloc(res.size);

	pkg.syn = syn;
	pkg.size = 0;
	pkg.flags = ACK;
	pkg.chksum = 0;
	pkg.empty = (BUFSIZE-rbr+rbf)%BUFSIZE;

	memcpy(res.data, &pkg, sizeof(pkg));
	return res;
}

void* sender(void *arg)
{
	while(running)
	{
		pthread_mutex_lock(&locksbuffer);
		while(lastsent-base<wndsize&&lastsent<end)
			udt_send(sbuffer[lastsent++], dest[i%NIPS]);
		pthread_mutex_unlock(&locksbuffer);
	}
}

void* reader(void *arg)
{
	package pkg;
	packet pkt;
	struct sockaddr_storage from;
	int flen = sizeof(from);
	while(running)
	{
		pthread_mutex_lock(&lockrbuffer);
		//if(rbf!=(rbr+1)%BUFSIZE)
		{
			if(recvfrom(socketd, &pkg, sizeof(pkg), 0, &from, &flen)!=sizeof(from))
				running = 0;
			pkt.size = pkg.size;
			pkt.data = malloc(pkt.size);
			if(recvfrom(socketd, pkt.data, pkt.size, 0, &from, &flen)!=pkt.size)
				running = 0;
			rbuffer[pkg.syn]=pkt;
			if(pkg.syn==rbase)
		}
		pthread_mutex_unlock(&lockrbuffer);
	}
}

void rdt_send(char *data, int size)
{
	int frags;
	packet *pkts = mkpkg(data, size, &frags, end);
	pthread_mutex_lock(&locksbuffer);
	if(end==base)
		alarm(ALARM_TIME);
	for(int i=0;i<frags;i++)
		sbuffer[end++] = pkts[i];
	pthread_mutex_unlock(&locksbuffer);
}

void udt_send(packet pkt, char *dest)
{
	struct sockaddr_in saddr;
	memset((char*)&saddr, 0, sizeof(saddr));
	saddr.sin_family = AF_INET;
	saddr.sin_port = htons(PORT);
	inet_aton(dest, &saddr.sin_addr);

	sendto(sendsock, pkt.data, pkt.size, 0, (struct sockaddr*)&saddr, sizeof(saddr));
}

void rdt_read(char* buf, int size)
{

}
