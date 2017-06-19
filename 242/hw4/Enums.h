#ifndef ENUMS_H
#define ENUMS_H

#include <utility>

#define BLEEDING_DAMAGE 25
#define MAGICAL_DAMAGE 20
#define FROSTBITE_DAMAGE 30

using std::pair;
using std::make_pair;

enum Weapon
{
	WEAPON_NONE,

	// Wizard Weapons
	ICESTAFF,
	FIRESTAFF,
	LIGHTINGSTAFF,
	VOODOSTAFF,
	
	// Whitewalker Weapons
	ICEBLADE,

	// Wildling Weapons
	LONGBOW,
	DAGGER
};
enum Armor
{
	ARMOR_NONE,

	LEATHER,
	CHAINMAIL,
	PLATEMAIL
};

enum Religion
{
	RELIGION_NONE,

	FAITH_OF_THE_SEVEN,
	OLD_GODS_OF_THE_FOREST,
	DROWNED_GOD,
	LORD_OF_LIGHT,
	GOD_OF_DEATH,
	GREAT_STALLION,
	GOD_OF_WITS_AND_TINE	// All hail Tyrion!
};

int weaponValue(Weapon weapon);							// Defines attack power
int armorValue(Armor armor);							// Defines health
pair<int, int> religionValue(Religion religion);		// Increases attack power and health, respectively.

#endif // ENUMS_H