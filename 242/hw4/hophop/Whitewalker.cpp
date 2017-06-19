#include "Whitewalker.h"

Whitewalker::Whitewalker(string cname) : Character(cname)
{
	abbreviation="-WHI-";
	healthDefault=600;
	attackPowerDefault=40;
}

void Whitewalker::attackTo(Character* const enemy)
{
	Character::attackTo(enemy);
	enemy->setFrostBitten(enemy->getFrostBitten()+1);
}
