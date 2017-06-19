#include "War.h"

void War::addKingdom(Kingdom *k)
{
	/*for(int i=0;i<(int)v.size();i++)
		if(v[i]==NULL)
		{
			v[i]=k;
			return;
		}*/
	v.push_back(k);
}

void War::removeKingdom(Kingdom *k)
{
	for(int i=0;i<(int)v.size();i++)
		if(v[i]==k)
		{
			for(int j=i;j<(int)v.size();j++)
				v[j]=v[j+1];
			v.resize(v.size()-1);
			break;
		}
}

int War::kingdomCount()
{
	/*int r=0;
	for(int i=0;i<v.size();i++)
		r+=v[i]!=NULL;
	return r;*/
	return v.size();
}

pair<int,int> War::duelOfCharacters(Character* c1, Character* c2, int bon1, int bon2)
{
	pair<int,int> res;
	res.first=res.second=0;
	
	c1->restoreOriginalValues();
	c1->applyCheerBonus(bon1);
	c1->removeDebuffs();
	c2->restoreOriginalValues();
	c2->applyCheerBonus(bon2);
	c2->removeDebuffs();
	while(!c1->isDead() && !c2->isDead())
	{
		res.second++;
		c1->applyDOTs();
		c2->applyDOTs();
		c1->attackTo(c2);
		c2->attackTo(c1);
		
		cout << "    ****" << endl;
	}
	
	if(c1->isDead() && c2->isDead())
		res.first=0;
	else if(c1->isDead())
		res.first=-1;
	else
		res.first=1;
		
	return res;
}

int War::duelOfHouses(House *h1, House *h2)
{
	cout << "  Duel between " << h1->getName() << "(" << h1->getSupporterCount()
		<< ") " << h1->getDuellist()->getAbbreviation() << " and " << 
		h2->getName() << "(" << h2->getSupporterCount()<< ") " << 
		h2->getDuellist()->getAbbreviation() << " has begun." << endl;
	
	pair<int, int> res = duelOfCharacters(h1->getDuellist(), h2->getDuellist(),
		h1->getSupporterCount(), h2->getSupporterCount());
	
	if(res.second<=2 && res.first!=0)
	{
		h1->disappointedMatch(res.first==1);
		h2->disappointedMatch(res.first==-1);
	}
	else if(res.second>6)
	{
		h1->boredMatch();
		h2->boredMatch();
	}
	
	if(res.first==1 && h2->getSupporterCount()>h1->getSupporterCount())
		h1->fight();
	else if(res.first==-1 && h1->getSupporterCount()>h2->getSupporterCount())
		h2->fight();
		
	if(res.first!=0)
	{
		h1->matchFinished(res.first==1);
		h2->matchFinished(res.first==-1);
	}
	
	if(res.first==0)
		cout << "    Both of them are dead. A draw." << endl;
	else if(res.first==1)
		cout << "    " << h1->getDuellist()->getName() << "(" << 
			h1->getDuellist()->getHealth() << ") has won." << endl;
	else if(res.first==-1)
		cout << "    " << h2->getDuellist()->getName() << "(" << 
			h2->getDuellist()->getHealth() << ") has won." << endl;
	
	return res.first;
}

int War::duelOfKingdoms(Kingdom *k1, Kingdom *k2)
{
	int p1=0,p2=0,x;
	vector<House*> h1=k1->getNobleHouses(), h2=k2->getNobleHouses();
	for(int i=0;i<3;i++)
		for(int j=0;j<3;j++)
		{
			x=duelOfHouses(h1[j], h2[(j+i)%3]);
			if(x==1)
				p1++;
			else if(x==-1)
				p2++;
		}
	x=duelOfHouses(k1->getGreatHouse(), k2->getGreatHouse());
	if(x==1)
		p1+=3;
	else if(x==-1)
		p2+=3;
		
	cout << "  " << k1->getName() << " got " << p1 << " house points. " << 
		k2->getName() << " got " << p2 << " house points." << endl;
	if(p1>p2)
		return 1;
	else if(p1<p2)
		return -1;
	return 0;	
}

void War::startSimulation()
{
	int dc=1,x,y=0;
	cout << "War has started with " << v.size() << " kingdoms." << endl;
	for(int i=1;i<(int)v.size();i++)
		for(int j=0;i+j<(int)v.size();j++)
		{
			cout << "---------------" << endl;
			cout << "Duel " << dc << ": " << v[j]->getName() << " vs " << 
				v[i+j]->getName() << endl;
			
			x=duelOfKingdoms(v[j], v[i+j]);
			
			cout << endl;
			if(x==1)
			{
				v[j]->increaseDuelPoints();
				cout << "The winner is: " << v[j]->getName() << " with currently "
					<< v[j]->getDuelPoints() << " kingdom points." << endl;
			}
			else if(x==-1)
			{
				v[i+j]->increaseDuelPoints();
				cout << "The winner is: " << v[i+j]->getName() << " with currently "
					<< v[i+j]->getDuelPoints() << " kingdom points." << endl;
			}
			else
			{
				v[j]->increaseDuelPoints();
				v[i+j]->increaseDuelPoints();
				cout << "A draw." << endl;
			}
			cout << "---------------" << endl;
			dc++;
		}
	x=0;
	for(int i=0;i<(int)v.size();i++)
		if(v[i]->getDuelPoints()>v[x]->getDuelPoints())
		{
			x=i;
			y=i;
		}
		else if(v[i]->getDuelPoints()==v[x]->getDuelPoints())
			y=i;
			
	cout << "The War is over. ";
	if(y==x)
		cout << "The new owner of King's Landing is " << v[x]->getName() <<
			" with a total of " << v[x]->getDuelPoints() << " points." << endl;
	else
	{
		cout << "The new owners of King's Landing are " << v[x]->getName();
		for(int i=x+1;i<y;i++)
			if(v[i]->getDuelPoints()==v[x]->getDuelPoints())
				cout << ", " << v[i]->getName();
		cout << ", and " << v[y]->getName() << "." << endl;
	}
	cout << endl;

	for(int i=0;i<(int)v.size();i++)
	{
		delete v[i]->getGreatHouse()->getDuellist();
		delete v[i]->getGreatHouse();
		for(int j=0;j<3;j++)
		{
			delete v[i]->getNobleHouses()[j]->getDuellist();
			delete v[i]->getNobleHouses()[j];
		}
		delete v[i];
	}
}

