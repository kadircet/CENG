#ifndef MAIN_CPP
#define MAIN_CPP

#include <iostream>
#include <fstream>

using std::ifstream;

#include "GreatHouse.h"
#include "NobleHouse.h"

#include "Wizard.h"
#include "Whitewalker.h"
#include "Wildling.h"

#include "Kingdom.h"

#include "War.h"

void printCharacter(Character *character)
{
	cout << character->getName() << " : ";
	cout << "Health = " << character->getHealth() << " " << "Attack = " << character->getAttackPower() << endl;
}
int main(int argv, char** argc)
{
	cout << "Part 1 - Creation(s)" << endl;

	Kingdom *theNorth = new Kingdom("The North", GOD_OF_WITS_AND_TINE);
	GreatHouse *houseStark = new GreatHouse("House Stark");

	Wizard *firstWizard = new Wizard("Azor Ahai");
	Wildling *firstWildling = new Wildling("Osha");
	Whitewalker *firstWhitewalker = new Whitewalker("Night King");

	cout << "Seems cool for now." << endl;
	cout << "Part 2 - Weapon Overloading" << endl;

	firstWizard->setWeapon(WEAPON_NONE);

	printCharacter(firstWizard);

	firstWizard->setWeapon(ICESTAFF);
	firstWizard->setWeapon(FIRESTAFF);
	firstWizard->setWeapon(LIGHTINGSTAFF);
	firstWizard->setWeapon(VOODOSTAFF);
	firstWizard->setWeapon(LIGHTINGSTAFF);
	firstWizard->setWeapon(FIRESTAFF);
	firstWizard->setWeapon(ICESTAFF);

	printCharacter(firstWizard);

	cout << "Part 3 - Armor Overloading" << endl;

	firstWizard->setArmor(ARMOR_NONE);

	printCharacter(firstWizard);

	firstWizard->setArmor(LEATHER);
	firstWizard->setArmor(CHAINMAIL);
	firstWizard->setArmor(PLATEMAIL);
	firstWizard->setArmor(CHAINMAIL);
	firstWizard->setArmor(LEATHER);

	printCharacter(firstWizard);

	cout << "Part 4 - Religion Test" << endl;

	theNorth->assignGreatHouse(houseStark);

	printCharacter(firstWildling);
	houseStark->assignCharacter(firstWildling, GOD_OF_WITS_AND_TINE);
	printCharacter(firstWildling);
	houseStark->dismissCharacter();
	printCharacter(firstWildling);

	houseStark->assignCharacter(firstWildling, GOD_OF_WITS_AND_TINE);
	printCharacter(firstWildling);
	theNorth->dismissGreatHouse();
	printCharacter(firstWildling);
}

#endif