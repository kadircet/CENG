#ifndef CHARACTER_H
#define CHARACTER_H

#include "Enums.h"

#include <string>
#include <iostream>

using std::string;
using std::cout;
using std::endl;

class Character 
{
	public:	
		Character(string characterName);	
    	~Character(){}
    	
    	virtual void attackTo(Character* const character);

    	string getName() const;
        string getAbbreviation() const;

    	int getHealth() const;
    	int getAttackPower() const;

    	int getBleeding() const;
    	void setBleeding(int bleedingLevel);

    	int getFrostBitten() const;
    	void setFrostBitten(int frostBittenLevel);

    	int getTakingMagicalDamage() const;
    	void setTakingMagicalDamage(int takingMagicalDamageLevel);

		///  It would ease your job to implement these as a function.
        void takeDamage(int damage);
		void applyCheerBonus(int damageBonus);
    	void restoreOriginalValues();
		void removeDebuffs();
		void saveOriginalValues();

		void applyDOTs();

        bool isDead();
        ///  It would ease your job to implement these as a function.

    	// Note: Do not implement getters for Weapon/Armor/Religion.
    	// Note: Weapon/Armor/Religion will buff health or/and attack power.

    	void setWeapon(Weapon weapon);
    	void setArmor(Armor armor);
    	void setReligion(Religion religion);

    protected:
    	string name;
        string abbreviation;

		Weapon weapon;
		Armor armor;
		Religion religion;
        
    	int attackPower;
    	int health;

    	int attackPowerDefault;
    	int healthDefault;

    	int bleedingLevel;
    	int frostBittenLevel;
    	int takingMagicalDamageLevel;
};


#endif // CHARACTER_H
