#ifndef WHITEWALKER_H
#define WHITEWALKER_H

#include "Character.h"

// TODO: Define Whitewalker Class
class Whitewalker : public Character
{
public:
	Whitewalker(string);
	~Whitewalker(){}

	void attackTo(Character* const);
};

#endif // WHITEWALKER_H
