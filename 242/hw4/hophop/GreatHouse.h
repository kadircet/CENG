#ifndef GREAT_HOUSE_H
#define GREAT_HOUSE_H

#include "House.h"

// TODO: Define Great House Class
class GreatHouse : public House
{
public:
	GreatHouse(string);

	void setSupporterCount(int);
	
	void matchFinished(bool);
	void increaseSupportCount(int);

	void disappointedMatch(bool);
	void boredMatch();
};

#endif // GREAT_HOUSE_H
