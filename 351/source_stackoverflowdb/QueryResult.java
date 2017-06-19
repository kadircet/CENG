/**
 * 
 */
package ceng.ceng351.stackoverflowdb;

/**
 * @date Nov 29, 2016
 * @project ceng 
 *
 *
 * This class will be used for returning query results.
 * You will design queries which return different results.
 * You can find an appropriate sub-class for each type of
 * results.
 */
public class QueryResult {

	public static class UsernameDateRatingResult{
	
		String username;
		String date;
		int rating;
		
		public UsernameDateRatingResult(
				String username, 
				String date, 
				int rating) {
			
			super();
			this.username = username;
			this.date = date;
			this.rating = rating;
		}
		
		public String toString() {
			return username + "\t" + date + "\t" + rating;
		}
		
	}
	
	public static class UsernameMessageRatingAlltimereputationResult{
		
		String username;
		String message;
		int rating;
		int alltimeReputation;
		
		public UsernameMessageRatingAlltimereputationResult(String username, String message, int rating, int alltimeReputation) {
			super();
			this.username = username;
			this.message = message;
			this.rating = rating;
			this.alltimeReputation = alltimeReputation;
		}
		
		public String toString() {
			return username + "\t" + message + "\t" + rating + "\t" + alltimeReputation;
		}
	}
	
	
	
	public static class UseridUsernameRegistrationdateWeeklyreputationResult{
		
		String userID;
		String username;
		String registrationDate;
		int weeklyReputation;
		
		
		public UseridUsernameRegistrationdateWeeklyreputationResult(String userID, String username, String registrationDate, int weeklyReputation) {
			super();
			this.userID = userID;
			this.username = username;
			this.registrationDate = registrationDate;
			this.weeklyReputation = weeklyReputation;
		}
		
		
		public String toString() {
			return userID + "\t" + username + "\t" + registrationDate + "\t" + weeklyReputation ;
		}
		
		
	}
	
	
	public static class UseridUsernameLastlogindateResult{
		
		String userID;
		String username;
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
		public String getlastLoginDate() {
			return lastLoginDate;
		}
		public void setlastLoginDate(String lastLoginDate) {
			this.lastLoginDate = lastLoginDate;
		}
		
		
		
		
		public UseridUsernameLastlogindateResult(String userID, String username, String lastLoginDate) {
			super();
			this.userID = userID;
			this.username = username;
			this.lastLoginDate = lastLoginDate;
		}
		
		public String toString() {
			return userID + "\t" + username + "\t" + lastLoginDate;
		}		
		
	}
	


	
	public static class UsernameMessageRatingResult{
		String username;
		String message;
		int rating;
		
		
		public String getusername() {
			return username;
		}
		public void setusername(String username) {
			this.username = username;
		}
		public String getmessage() {
			return message;
		}
		public void setmessage(String message) {
			this.message = message;
		}
		public int getrating() {
			return rating;
		}
		public void setrating(int rating) {
			this.rating = rating;
		}
		
		public UsernameMessageRatingResult(String username, String message, int rating) {
			super();
			this.username = username;
			this.message = message;
			this.rating = rating;
		}
		
		public String toString() {
			return username + "\t" + message + "\t" + rating ;
		}
			
	}
		
		public static class NameUsernameDateRatingResult{
		
		String name;
		String username;
		String date;
		int rating;
		
		
		public NameUsernameDateRatingResult(String name, String username, String date, int rating) {
			super();
			this.name = name;
			this.username = username;
			this.date = date;
			this.rating = rating;
		}
		
		
		public String toString() {
			return name + "\t" + username + "\t" + date + "\t" + rating ;
		}
		
		
	}
	
}
