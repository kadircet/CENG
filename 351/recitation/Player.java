/**
 * 
 */
package ceng.ceng351.recitation;

/**
 * @author Alperen Dalkiran
 * @date Dec 2, 2016
 * @project ceng 
 *
 */
public class Player {
	
	public int number;
	public String teamname;
	public String name;
	public int age;
	public String position;
	
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getTeamname() {
		return teamname;
	}
	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}


	public String toString() {
		return name + "(" + age + "):" + teamname + " (#" + number + ") - " + position;
	}
	
	
	public Player(int number, String teamname, String name, int age, String position) {
		this.number = number;
		this.teamname = teamname;
		this.name = name;
		this.age = age;
		this.position = position;
	}

	
	
}
