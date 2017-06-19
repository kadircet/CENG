#include "NobleHouse.h"

NobleHouse::NobleHouse(string hname)
{
	name=hname;
	supporterCount=30;
	duellist=NULL;
}

void NobleHouse::setSupporterCount(int x)
{
	supporterCount=max(0, min(60, x));
}

void NobleHouse::increaseSupportCount(int x)
{
	setSupporterCount(supporterCount+x);
}

void NobleHouse::fight()
{
	increaseSupportCount(-supporterCount/5);
}

void NobleHouse::matchFinished(bool won)
{
	increaseSupportCount(3*(won?1:-1));
}

