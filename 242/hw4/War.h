#ifndef WAR_H
#define WAR_H

#include "Kingdom.h"

#include <vector>

class War {

	public:
		std::vector<Kingdom *> v;

		// You may not change methods below. 

		void startSimulation();

		// The return value should go accordingly:
		// 1 -> First Kingdom/House/Character won.
		// 0 -> Draw.
		// -1 -> Second Kingdom/House/Character won.

		int duelOfKingdoms(Kingdom* kingdom1, Kingdom* kingdom2);
		int duelOfHouses(House* house1, House* house2);

		// First part -> Duel Value
		// Second part -> Round Count
		pair<int, int> duelOfCharacters(Character* character1, Character* character2, int cheerBonus1, int cheerBonus2);
	
		//--//

		// You are free to add/change methods below. These example methods are for your own convenience.
		void addKingdom(Kingdom* kingdom);
		void removeKingdom(Kingdom* kingdom);

		int kingdomCount();
};


#endif