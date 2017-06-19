/**
 * 
 */
package ceng.ceng351.stackoverflowdb;

/**
 * @date November 28, 2016
 * @project ceng 
 *
 */
public class Reputation {
	
	
	//reputation(reputationID:char(10), userID:char(10), weeklyReputation:int, monthlyReputation:int, yearlyReputation:int,alltimeReputation:int)
	
	String reputationID;
	String userID;
	int weeklyReputation;
	int monthlyReputation;
	int yearlyReputation;
	int alltimeReputation;
	
	public String getreputationID() {
		return reputationID;
	}
	public void setreputationID(String reputationID) {
		this.reputationID = reputationID;
	}
	public String getuserID() {
		return userID;
	}
	public void setuserID(String userID) {
		this.userID = userID;
	}
	public int getweeklyReputation() {
		return weeklyReputation;
	}
	public void setweeklyReputation(int weeklyReputation) {
		this.weeklyReputation = weeklyReputation;
	}
	public int getmonthlyReputation() {
		return monthlyReputation;
	}
	public void setmonthlyReputation(int monthlyReputation) {
		this.monthlyReputation = monthlyReputation;
	}	
	public int getyearlyReputation() {
		return yearlyReputation;
	}
	public void setyearlyReputation(int yearlyReputation) {
		this.yearlyReputation = yearlyReputation;
	}
	public int getalltimeReputation() {
		return alltimeReputation;
	}
	public void setalltimeReputation(int alltimeReputation) {
		this.alltimeReputation = alltimeReputation;
	}	
	
	
	
	public Reputation(String reputationID, String userID, int weeklyReputation, int monthlyReputation, int yearlyReputation, int alltimeReputation) {
		super();
		this.reputationID = reputationID;
		this.userID = userID;
		this.weeklyReputation = weeklyReputation;
		this.monthlyReputation = monthlyReputation;		
		this.yearlyReputation = yearlyReputation;		
		this.alltimeReputation = alltimeReputation;
	}
	
	

	
}
