#include "Kingdom.h"

Kingdom::Kingdom(string kingdomName, Religion kingdomReligion)
{
	name=kingdomName;
	religion=kingdomReligion;
	greatHouse=NULL;
	nobleHouses.clear();
	nobleHouses.resize(3, NULL);
	duelPoints=0;
}

Kingdom::~Kingdom()
{
/*	for(int i=0;i<3;i++)
		removeNobleHouse(nobleHouses[i]);
	dismissGreatHouse();*/
}

void Kingdom::assignGreatHouse(House* const gh)
{
	greatHouse=gh;
	if(gh!=NULL && gh->getDuellist()!=NULL)
		gh->getDuellist()->setReligion(religion);
}

void Kingdom::dismissGreatHouse()
{
	if(greatHouse!=NULL && greatHouse->getDuellist()!=NULL)
		greatHouse->getDuellist()->setReligion(RELIGION_NONE);
	greatHouse=NULL;
}

void Kingdom::addNobleHouse(House* const nh)
{
	for(int i=0;i<3;i++)
		if(nobleHouses[i]==NULL)
		{
			nobleHouses[i]=nh;
			if(nh!=NULL && nh->getDuellist()!=NULL)
				nh->getDuellist()->setReligion(religion);
			break;
		}
}

void Kingdom::removeNobleHouse(House* const nh)
{
	for(int i=0;i<3;i++)
		if(nobleHouses[i]==nh)
		{
			nobleHouses[i]=NULL;
			for(int j=i;j<2;j++)
				nobleHouses[j]=nobleHouses[j+1];
			if(nh!=NULL && nh->getDuellist()!=NULL)
				nh->getDuellist()->setReligion(RELIGION_NONE);
			break;
		}
}

string Kingdom::getName()
{
	return name;
}

Religion Kingdom::getReligion()
{
	return religion;
}

void Kingdom::setReligion(Religion r)
{
	religion=r;
}

House* Kingdom::getGreatHouse()
{
	return greatHouse;
}

vector<House*> Kingdom::getNobleHouses()
{
	return nobleHouses;
}

void Kingdom::increaseDuelPoints(int x)
{
	duelPoints+=x;
}

int Kingdom::getDuelPoints()
{
	return duelPoints;
}

