#include "Wizard.h"

Wizard::Wizard(string cname) : Character(cname)
{
	healthDefault=450;
	attackPowerDefault=60;
	abbreviation="-WIZ-";
}

void Wizard::attackTo(Character* const enemy)
{
	Character::attackTo(enemy);
	enemy->setTakingMagicalDamage(enemy->getTakingMagicalDamage()+1);
}

