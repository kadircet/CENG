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

War *sharedWar;

int convertStringToCharacterNumber(string characterType)
{
	if(!characterType.compare("Wizard"))
		return 0;
	else if(!characterType.compare("Whitewalker"))
		return 1;
	else if(!characterType.compare("Wildling"))
		return 2;

	return 666;
}
Character* createCharacter(string characterName, string characterType)
{
	int characterNumber = convertStringToCharacterNumber(characterType);

	switch(characterNumber)
	{
		case 0:
			return new Wizard(characterName);
		case 1:
			return new Whitewalker(characterName);
		case 2:
			return new Wildling(characterName);
		default:
			return NULL;
	}

	return NULL;
}
void createKingdom(
string kingdomName,
 Religion religion,
  string greatHouseName,
   string greatHouseHeroName,
    string greatHouseCharacterType,
     Weapon greatHouseWeapon,
      Armor greatHouseArmor,
       vector<string> nobleHouseNames,
        vector<string> nobleHouseHeroes,
         vector<string> nobleHouseCharacterTypes,
          vector<Weapon> nobleHouseWeapons,
           vector<Armor> nobleHouseArmors
)
{
	Kingdom *kingdom = new Kingdom(kingdomName, religion);

	GreatHouse *greatHouse = new GreatHouse(greatHouseName);

	Character *greatHouseHero = createCharacter(greatHouseHeroName, greatHouseCharacterType);

	greatHouseHero->setWeapon(greatHouseWeapon);
	greatHouseHero->setArmor(greatHouseArmor);

	kingdom->assignGreatHouse(greatHouse);

	greatHouse->assignCharacter(greatHouseHero, religion);
	
	//greatHouseHero->saveOriginalValues();

	for(unsigned int i = 0; i < nobleHouseNames.size(); i++)
	{
		NobleHouse *nobleHouse = new NobleHouse(nobleHouseNames[i]);
		Character *nobleHouseHero = createCharacter(nobleHouseHeroes[i], nobleHouseCharacterTypes[i]);

		nobleHouseHero->setWeapon(nobleHouseWeapons[i]);
		nobleHouseHero->setArmor(nobleHouseArmors[i]);
		
		kingdom->addNobleHouse(nobleHouse);

		nobleHouse->assignCharacter(nobleHouseHero, religion);

		//nobleHouseHero->saveOriginalValues();
	}

	sharedWar->addKingdom(kingdom);
}
vector<string> splitString(string line, string delimiter)
{
	vector<string> vectorToReturn;

	size_t position = 0;

	string part;
	
	while((position = line.find(delimiter)) != std::string::npos)
	{
		part = line.substr(0, position);

		vectorToReturn.push_back(part);

		line.erase(0, position + delimiter.length());
	}

	vectorToReturn.push_back(line);

	return vectorToReturn;
}
int main(int argv, char** argc)
{
	if(argv < 2)
	{
		return 0;
	}

	sharedWar = new War();

	ifstream inputFile(argc[1]);
	
	if(inputFile.is_open())
	{
		string line;
		string nameDelimiter = "/";
		string numberDelimiter = "-";

		int lineCounter = 0;
		int currentLineCounter = 0;

		string kingdomName;

		string greatHouseName;
		string greatHouseHero;
		string greatHouseCharacterType;
		Weapon greatHouseWeapon;
		Armor greatHouseArmor;

		string nobleHouseName;
		string nobleHouseHero;
		string nobleHouseCharacterType;
		Weapon nobleHouseWeapon;
		Armor nobleHouseArmor;

		vector<string> nobleHouseNames;
		vector<string> nobleHouseHeroes;
		vector<string> nobleHouseCharacterTypes;
		vector<Weapon> nobleHouseWeapons;
		vector<Armor> nobleHouseArmors;

		vector<string> namesHolder;

		string integerHolder;

		int religion;

		while(getline(inputFile, line))
		{
			if(lineCounter < 2)
			{
				lineCounter++;

				continue;
			}
			else
			{
				currentLineCounter++;

				switch(currentLineCounter)
				{
					case 1:
						namesHolder = splitString(line, nameDelimiter);
						kingdomName = namesHolder[0];
						religion = Religion(splitString(namesHolder[1], numberDelimiter)[1].at(0) - '0');
						break;
					case 2:
						namesHolder = splitString(line, nameDelimiter);

						greatHouseName = namesHolder[0];
						greatHouseHero = namesHolder[1];
						greatHouseCharacterType = namesHolder[2];
						greatHouseWeapon = Weapon(splitString(namesHolder[3], numberDelimiter)[1].at(0) - '0');
						greatHouseArmor = Armor(splitString(namesHolder[4], numberDelimiter)[1].at(0) - '0');

						break;
					case 3:
					case 4:
					case 5:
						namesHolder = splitString(line, nameDelimiter);

						nobleHouseName = namesHolder[0];
						nobleHouseHero = namesHolder[1];
						nobleHouseCharacterType = namesHolder[2];
						nobleHouseWeapon = Weapon(splitString(namesHolder[3], numberDelimiter)[1].at(0) - '0');
						nobleHouseArmor = Armor(splitString(namesHolder[4], numberDelimiter)[1].at(0) - '0');

						nobleHouseNames.push_back(nobleHouseName);
						nobleHouseHeroes.push_back(nobleHouseHero);
						nobleHouseCharacterTypes.push_back(nobleHouseCharacterType);
						nobleHouseWeapons.push_back(nobleHouseWeapon);
						nobleHouseArmors.push_back(nobleHouseArmor);

						break;
					case 6:
						currentLineCounter = 0;
						
						createKingdom(
						kingdomName,
						 (Religion)religion,
						  greatHouseName,
						   greatHouseHero,
						    greatHouseCharacterType,
						     (Weapon)greatHouseWeapon,
						      (Armor)greatHouseArmor,
						       nobleHouseNames, 
						        nobleHouseHeroes,
						         nobleHouseCharacterTypes, 
						          nobleHouseWeapons, 
						           nobleHouseArmors);

						nobleHouseNames.clear();
						nobleHouseHeroes.clear();
						nobleHouseCharacterTypes.clear();
						nobleHouseWeapons.clear();
						nobleHouseArmors.clear();

						break;
				}
			}
		}
	}

	inputFile.close();

	sharedWar->startSimulation();

	// sharedWar->deallocEverything();
}

#endif