#ifndef WIZARD_H
#define WIZARD_H

#include "Character.h"

// TODO: Define Wizard Class
class Wizard : public Character
{
public:
	Wizard(string);
	~Wizard(){};

	void attackTo(Character* const);
};

#endif // WIZARD_H
