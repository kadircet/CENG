#include "Wildling.h"

Wildling::Wildling(string cname) : Character(cname)
{
	healthDefault=425;
	attackPowerDefault=70;
	abbreviation="-WIL-";
}

void Wildling::attackTo(Character* const enemy)
{
	Character::attackTo(enemy);
	enemy->setBleeding(enemy->getBleeding()+1);
}
