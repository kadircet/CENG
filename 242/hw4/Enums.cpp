#include "Enums.h"

#include <string>

int weaponValue(Weapon weapon)
{
	switch(weapon)
	{
		case WEAPON_NONE:
			return 0;

		case ICESTAFF:
			return 25;
		case FIRESTAFF:
			return 35;
		case LIGHTINGSTAFF:
			return 30;
		case VOODOSTAFF:
			return 40;

		case ICEBLADE:
			return 50;

		case LONGBOW:
			return 40;
		case DAGGER:
			return 45;
	}

	return 0;
}
int armorValue(Armor armor)
{	
	switch(armor)
	{
		case ARMOR_NONE:
			return 0;

		case LEATHER:
			return 25;
		case CHAINMAIL:
			return 50;
		case PLATEMAIL:
			return 75;
	}

	return 0;
}
pair<int, int> religionValue(Religion religion)
{
	switch(religion)
	{
		case RELIGION_NONE:
			return make_pair(0, 0);

		case FAITH_OF_THE_SEVEN:
			return make_pair(30, 40);
		case OLD_GODS_OF_THE_FOREST:
			return make_pair(40, 30);
		case DROWNED_GOD:
			return make_pair(50, 20);
		case LORD_OF_LIGHT:
			return make_pair(10, 60);
		case GOD_OF_DEATH:
			return make_pair(70, 0);
		case GREAT_STALLION:
			return make_pair(90, -20);

		case GOD_OF_WITS_AND_TINE:
			return make_pair(33, 33);
	}

	return make_pair(0, 0);
}