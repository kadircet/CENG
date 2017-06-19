#include "Character.h"

Character::Character(string name):name(name)
{
}

string Character::getName() const
{
	return name;
}

string Character::getAbbreviation() const
{
	return abbreviation;
}

int Character::getHealth() const
{
	return health;
}

int Character::getAttackPower() const
{
	return attackPower;
}

int Character::getBleeding() const
{
	return bleedingLevel;
}

void Character::setBleeding(int x)
{
	if(x>3)
		x=3;
	bleedingLevel=x;
}

int Character::getFrostBitten() const
{
	return frostBittenLevel;
}

void Character::setFrostBitten(int x)
{
	if(x>3)
		x=3;
	frostBittenLevel=x;
}

int Character::getTakingMagicalDamage() const
{
	return takingMagicalDamageLevel;
}

void Character::setTakingMagicalDamage(int x)
{
	if(x>3)
		x=3;
	takingMagicalDamageLevel=x;
}

void Character::takeDamage(int x)
{
	health-=x;
	//if(health<0)
		//health=0;
}

void Character::applyCheerBonus(int x)
{
	attackPower+=x;
}

void Character::restoreOriginalValues()
{
	attackPower=attackPowerDefault+weaponValue(weapon)+religionValue(religion).first;
	health=healthDefault+armorValue(armor)+religionValue(religion).second;
}

void Character::removeDebuffs()
{
	bleedingLevel=0;
	frostBittenLevel=0;
	takingMagicalDamageLevel=0;
}

void Character::saveOriginalValues()
{
}

bool Character::isDead()
{
	return health<=0;
}

void Character::setWeapon(Weapon x)
{
	weapon=x;
}

void Character::setArmor(Armor x)
{
	armor=x;
}

void Character::setReligion(Religion x)
{
	religion=x;
}

void Character::applyDOTs()
{
	int hBef=health;
	health-=bleedingLevel*BLEEDING_DAMAGE;
	health-=takingMagicalDamageLevel*MAGICAL_DAMAGE;
	health-=frostBittenLevel*FROSTBITE_DAMAGE;
	if(hBef>health)
		cout << "    " << name << "(" << hBef << ") took " << hBef-health << 
			" damage as DOT." << endl;
}

void Character::attackTo(Character* const enemy)
{
	cout << "    " << name << "(" << health << ") hit " << enemy->name << "(" <<
		enemy->health << ") " << attackPower << "." << endl;
	enemy->takeDamage(attackPower);
}

