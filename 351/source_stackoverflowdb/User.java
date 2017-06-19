/**
 * 
 */
package ceng.ceng351.stackoverflowdb;

/**
 * @date November 28, 2016
 * @project ceng 
 *
 */
public class User {
	
	//user(userID:char(10), username:char(30), registrationDate:date, lastLoginDate:date)
	String userID;
	String username;
	String registrationDate;
	String lastLoginDate;
	
	
	
	public String getuserID() {
		return userID;
	}
	public void setuserID(String userID) {
		this.userID = userID;
	}
	public String getusername() {
		return username;
	}
	public void setusername(String username) {
		this.username = username;
	}
	public String getregistrationDate() {
		return registrationDate;
	}
	public void setregistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getlastLoginDate() {
		return lastLoginDate;
	}
	public void setlastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	
	
	public User(String userID, String username, String registrationDate, String lastLoginDate) {
		super();
		this.userID = userID;
		this.username = username;
		this.registrationDate = registrationDate;
		this.lastLoginDate = lastLoginDate;
	}
	

	

}
