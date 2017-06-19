#include "GreatHouse.h"

GreatHouse::GreatHouse(string hname)
{
	name=hname;
	supporterCount=50;
	duellist=NULL;
}

void GreatHouse::setSupporterCount(int x)
{
	supporterCount=max(0, min(100, x));
}

void GreatHouse::increaseSupportCount(int x)
{
	setSupporterCount(supporterCount+x);
}

void GreatHouse::disappointedMatch(bool x)
{
	increaseSupportCount(4*x-8*(1-x));
}

void GreatHouse::boredMatch()
{
	increaseSupportCount(-6);
}

void GreatHouse::matchFinished(bool won)
{
	increaseSupportCount(5*(won?1:-1));
}

