#include "GreatHouse.h"
#include "NobleHouse.h"

#include "Wizard.h"
#include "Whitewalker.h"
#include "Wildling.h"

#include "Kingdom.h"

#include "War.h"
#include <string>
#include <iostream>
#include <vector>
using namespace std;
int main()
{
	int kingdomCount;
	vector <Kingdom *> kingdoms;
	vector <GreatHouse *> gHouses;
	vector <NobleHouse *> nHouses;
	vector <Character *> characters;
	War* war=new War();
	string tmp;
	cin>>tmp>>tmp>>tmp>>kingdomCount;
	getline(cin,tmp);
	for(int i=0; i<kingdomCount;i++)
	{
		string input,name,characterType;
		Character * tmpCharacter;
		int religion,weapon,armor;
		getline(cin,input);

		getline(cin,input);
		name=input.substr(0,input.find("/"));
		religion=input.substr(input.find("-")+1)[0]-'0';
		Kingdom* tmpKingdom=new Kingdom(name,(Religion) religion);
		kingdoms.push_back(tmpKingdom);
		war->addKingdom(tmpKingdom);

		getline(cin,input);
		name=input.substr(0,input.find("/"));
		GreatHouse* tmpGreatHouse=new GreatHouse(name);
		gHouses.push_back(tmpGreatHouse);
		tmpKingdom->assignGreatHouse(tmpGreatHouse);
		input=input.substr(input.find("/")+1);
		name=input.substr(0,input.find("/"));
		input=input.substr(input.find("/")+1);
		characterType=input.substr(0,input.find("/"));
		input=input.substr(input.find("/")+1);
		weapon=input.substr(input.find("-")+1)[0]-'0';
		input=input.substr(input.find("/")+1);
		armor=input.substr(input.find("-")+1)[0]-'0';
		if(characterType=="Whitewalker")
		{
			tmpCharacter=new Whitewalker(name);
			tmpCharacter->setArmor((Armor) armor);
			tmpCharacter->setWeapon((Weapon) weapon);
			tmpGreatHouse->assignCharacter(tmpCharacter,tmpKingdom->getReligion());
		}
		else if(characterType=="Wizard")
		{
			tmpCharacter=new Wizard(name);
			tmpCharacter->setArmor((Armor) armor);
			tmpCharacter->setWeapon((Weapon) weapon);
			tmpGreatHouse->assignCharacter(tmpCharacter,tmpKingdom->getReligion());
		}
		else if(characterType=="Wildling")
		{
			tmpCharacter=new Wildling(name);
			tmpCharacter->setArmor((Armor) armor);
			tmpCharacter->setWeapon((Weapon) weapon);
			tmpGreatHouse->assignCharacter(tmpCharacter,tmpKingdom->getReligion());
		}
		characters.push_back(tmpCharacter);
		for(int j=0;j<3;j++)
		{
			getline(cin,input);
			name=input.substr(0,input.find("/"));
			NobleHouse* tmpNobleHouse=new NobleHouse(name);
			nHouses.push_back(tmpNobleHouse);
			tmpKingdom->addNobleHouse(tmpNobleHouse);
			input=input.substr(input.find("/")+1);
			name=input.substr(0,input.find("/"));
			input=input.substr(input.find("/")+1);
			characterType=input.substr(0,input.find("/"));
			input=input.substr(input.find("/")+1);
			weapon=input.substr(input.find("-")+1)[0]-'0';
			input=input.substr(input.find("/")+1);
			armor=input.substr(input.find("-")+1)[0]-'0';
			if(characterType=="Whitewalker")
			{
				tmpCharacter=new Whitewalker(name);
				tmpCharacter->setArmor((Armor) armor);
				tmpCharacter->setWeapon((Weapon) weapon);
				tmpNobleHouse->assignCharacter(tmpCharacter,tmpKingdom->getReligion());
			}
			else if(characterType=="Wizard")
			{
				tmpCharacter=new Wizard(name);
				tmpCharacter->setArmor((Armor) armor);
				tmpCharacter->setWeapon((Weapon) weapon);
				tmpNobleHouse->assignCharacter(tmpCharacter,tmpKingdom->getReligion());
			}
			else if(characterType=="Wildling")
			{
				tmpCharacter=new Wildling(name);
				tmpCharacter->setArmor((Armor) armor);
				tmpCharacter->setWeapon((Weapon) weapon);
				tmpNobleHouse->assignCharacter(tmpCharacter,tmpKingdom->getReligion());
			}
			characters.push_back(tmpCharacter);
		}

	}
	war->startSimulation();
	/*for(int i = 0; i<kingdoms.size();i++)
		delete kingdoms[i];
	for(int i = 0; i<gHouses.size();i++)
		delete gHouses[i];
	for(int i = 0; i<nHouses.size();i++)
		delete nHouses[i];
	for(int i = 0; i<characters.size();i++)
		delete characters[i];*/
	delete war;
	return 0;
}
