/**
 * 
 */
package ceng.ceng351.stackoverflowdb;



/**
 * @date Nov 29, 2016
 * @project ceng 
 *
 */
public interface ISTACKOVERFLOWDB {
	
	
	/**
	 * Place your initialization code inside if required.
	 * 
	 * <p>
	 * This function will be called before all other operations. If your implementation
	 * need initialization , necessary operations should be done inside this function. For
	 * example, you can set your connection to the database server inside this function.
	 */
	public void initialize();
	
	
	/**
	 * Should create the necessary tables when called.
	 * 
	 * @return the number of tables that are created successfully.
	 */
	public int createTables();
	
	
	/**
	 * Should drop the tables if exists when called. 
	 * 
	 * @return the number of tables are dropped successfully.
	 */
	public int dropTables();
	
	
	/**
	 * Should insert an array of User into the database.
	 * 
	 * @return Number of rows inserted successfully.
	 */
	public int insertUser(User[] users);
	
	/**
	 * Should insert an array of Article into the database.
	 * 
	 * @return Number of rows inserted successfully.
	 */
	public int insertArticle(Article[] articles);
	
	/**
	 * Should insert an array of Comment into the database.
	 * 
	 * @return Number of rows inserted successfully.
	 */
	public int insertComment(Comment[] comments);
	
	/**
	 * Should insert an array of insertReputation into the database.
	 * 
	 * @return Number of rows inserted successfully.
	 */
	public int insertReputation(Reputation[] reputations);
	
	
	/**
	 * Should get the article which has the highest rating
	 * 
	 * @return QueryResult.UsernameDateRatingResult[]
	 */
	 public QueryResult.UsernameDateRatingResult[] getArticleHighestRating();
	
	/**
	 * Should get the user whose comment message does not contain 'mysql' and the comment has the lowest rating
	 * 
	 * @return QueryResult.UsernameMessageRatingAlltimereputationResult[]
	 */
	 public QueryResult.UsernameMessageRatingAlltimereputationResult[] getCommentLowestRating();
	
	/**
	 * Should get the userID, username, registration date and weekly reputation of users whose registration date is after a given date
	 * 
	 * 
	 * @return QueryResult.UseridUsernameRegistrationdateWeeklyreputationResult[]
	 */
	public QueryResult.UseridUsernameRegistrationdateWeeklyreputationResult[]  getUseridUsernameAfterGivenDate(String Date);
	
	
	/**
	 * Should get the username, message, and rating of comments which does not contain given string in their message ant it's rating
	 * is more than given rating
	 * 
	 * 
	 * @return QueryResult.UsernameMessageRatingResult[]
	 */
	public QueryResult.UsernameMessageRatingResult[]  getUsernameMessageRatingMoreThanGivenRating(String message, int rating);
	

	/**
	 * Update the rating of the comment by multiplying 2 whose date is after for a given date.
	 * 
	 * 
	 * @return the row count for SQL Data Manipulation Language (DML) statements
	 */
	public int  MultiplyComment(String date);

	
	/**
	 * Should return the userID, username and last login date of user/s who comment all the articles commented by a given user
	 * 
	 * 
	 * @return QueryResult.UsernameMessageRatingResult[]
	 */
	public QueryResult.UseridUsernameLastlogindateResult[]  getUsernameMessageRatingCommentedByGivenUser(String userID);

	/**
	 * Should return the username, comment message and rating of comments whose comment rating is more than given number of ratings
	 * for a given article (given articleID)
	 * 
	 * 
	 * @return QueryResult.NameUsernameDateRatingResult[]
	 */
	public QueryResult.UsernameMessageRatingResult[]  getNameUsernameDateRatingMoreThanGivenArticle(int rating, String articleID);
	
	/**
	 * Should return the article name, username, article date and article rating of articles commented by this user such that none of these
	 * articles are commented by any other user.
	 * 
	 * 
	 * @return QueryResult.UsernameDateRatingResult[]
	 */
	public QueryResult.NameUsernameDateRatingResult[]  getUsernameDateRatingNotCommentedByAnyUser();
	
	/**
	 * Should return the username, date and rating of articles whose date is before given date and whose user has the highest 
	 * weeklyReputation.
	 * 
	 * 
	 * @return QueryResult.UsernameDateRatingResult[]
	 */
	public QueryResult.UsernameDateRatingResult[]  UsernameDateRatingHasHighestReputation(String date);
	
	/**
	 * Should first delete rating of the articles for a given date then, output the username, date and rating of these articles.
	 * 
	 * 
	 * @return QueryResult.UsernameDateRatingResult[]
	 */
	public QueryResult.UsernameDateRatingResult[]  UsernameDateRatingDeleteAndSelect(String date);
		
	
}
