#ifndef HOUSE_H
#define HOUSE_H

#include "Character.h"

#include <vector>

#define max(a,b) ((a)>(b)?(a):(b))
#define min(a,b) ((a)>(b)?(b):(a))

class House 
{	
	public:
		virtual ~House(){}

		virtual void assignCharacter(Character* const d, Religion r)
		{
			duellist=d;
			d->setReligion(r);
		}
		
		virtual void dismissCharacter()
		{
			duellist=NULL;
		}

		virtual std::string getName() const
		{
			return name;
		}
		
		virtual Character* getDuellist() const
		{
			return duellist;
		}

		virtual int getSupporterCount() const
		{
			return supporterCount;
		}
		
		virtual void setSupporterCount(int x) = 0;

		// Optional methods.
		virtual void matchFinished(bool) = 0;
		virtual void increaseSupportCount(int) = 0;

		virtual void disappointedMatch(bool hasWon) {}		// Applies only for Great Houses.
		virtual void boredMatch() {}						// Applies only for Great Houses.

		virtual void fight() {}								// Applies only for Noble Houses.
		// Optional methods.
		
	protected:
    	std::string name;
    	Character* duellist;

    	int supporterCount;
};

#endif // HOUSE_H
