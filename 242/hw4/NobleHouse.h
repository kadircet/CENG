#ifndef NOBLE_HOUSE_H
#define NOBLE_HOUSE_H

#include "House.h"

// TODO: Define Noble House Class
class NobleHouse : public House
{
public:
	NobleHouse(string);
	
	void setSupporterCount(int);
	
	void matchFinished(bool);
	void increaseSupportCount(int);
	
	void fight();
};

#endif // NOBLE_HOUSE_H
