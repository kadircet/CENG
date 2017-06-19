package ceng.ceng351.stackoverflowdb;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class STACKOVERFLOWDB implements ISTACKOVERFLOWDB
{
	private static String uname = "e2036457", pass="08f893", dbname="db2036457";
	private static String dbhost= "144.122.71.165";
	private static int port = 3306;
	private Connection con;

	/**
	 * Place your initialization code inside if required.
	 * 
	 * <p>
	 * This function will be called before all other operations. If your implementation
	 * need initialization , necessary operations should be done inside this function. For
	 * example, you can set your connection to the database server inside this function.
	 */
	public void initialize()
	{
		String url = "jdbc:mysql://"+this.dbhost+":"+this.port+"/"+this.dbname;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			this.con = DriverManager.getConnection(url, this.uname, this.pass);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Should create the necessary tables when called.
	 * 
	 * @return the number of tables that are created successfully.
	 */
	public int createTables()
	{
		int res=0;
		String sql = "CREATE TABLE IF NOT EXISTS user (userID char(10), username char(30), registrationDate date, lastLoginDate date, primary key(userID));";
		try
		{
			Statement stmt = this.con.createStatement();
			System.out.println(stmt.executeUpdate(sql));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return res;
	}
	
	
	/**
	 * Should drop the tables if exists when called. 
	 * 
	 * @return the number of tables are dropped successfully.
	 */
	public int dropTables()
	{
		return 0;
	}
	
	
	/**
	 * Should insert an array of User into the database.
	 * 
	 * @return Number of rows inserted successfully.
	 */
	public int insertUser(User[] users)
	{
		return 0;
	}
	
	/**
	 * Should insert an array of Article into the database.
	 * 
	 * @return Number of rows inserted successfully.
	 */
	public int insertArticle(Article[] articles)
	{
		return 0;
	};
	
	/**
	 * Should insert an array of Comment into the database.
	 * 
	 * @return Number of rows inserted successfully.
	 */
	public int insertComment(Comment[] comments)
	{
		return 0;
	};
	
	/**
	 * Should insert an array of insertReputation into the database.
	 * 
	 * @return Number of rows inserted successfully.
	 */
	public int insertReputation(Reputation[] reputations)
	{
		return 0;
	};
	
	
	/**
	 * Should get the article which has the highest rating
	 * 
	 * @return QueryResult.UsernameDateRatingResult[]
	 */
	 public QueryResult.UsernameDateRatingResult[] getArticleHighestRating()
	 {
		 return null;
	 };
	
	/**
	 * Should get the user whose comment message does not contain 'mysql' and the comment has the lowest rating
	 * 
	 * @return QueryResult.UsernameMessageRatingAlltimereputationResult[]
	 */
	 public QueryResult.UsernameMessageRatingAlltimereputationResult[] getCommentLowestRating()
	 {
		 return null;
	 };
	
	/**
	 * Should get the userID, username, registration date and weekly reputation of users whose registration date is after a given date
	 * 
	 * 
	 * @return QueryResult.UseridUsernameRegistrationdateWeeklyreputationResult[]
	 */
	public QueryResult.UseridUsernameRegistrationdateWeeklyreputationResult[]  getUseridUsernameAfterGivenDate(String Date)
	{
		return null;
	};
	
	
	/**
	 * Should get the username, message, and rating of comments which does not contain given string in their message ant it's rating
	 * is more than given rating
	 * 
	 * 
	 * @return QueryResult.UsernameMessageRatingResult[]
	 */
	public QueryResult.UsernameMessageRatingResult[]  getUsernameMessageRatingMoreThanGivenRating(String message, int rating)
	{
		return null;
	};
	

	/**
	 * Update the rating of the comment by multiplying 2 whose date is after for a given date.
	 * 
	 * 
	 * @return the row count for SQL Data Manipulation Language (DML) statements
	 */
	public int  MultiplyComment(String date)
	{
		return 0;
	};

	
	/**
	 * Should return the userID, username and last login date of user/s who comment all the articles commented by a given user
	 * 
	 * 
	 * @return QueryResult.UsernameMessageRatingResult[]
	 */
	public QueryResult.UseridUsernameLastlogindateResult[]  getUsernameMessageRatingCommentedByGivenUser(String userID)
	{
		return null;
	};

	/**
	 * Should return the username, comment message and rating of comments whose comment rating is more than given number of ratings
	 * for a given article (given articleID)
	 * 
	 * 
	 * @return QueryResult.NameUsernameDateRatingResult[]
	 */
	public QueryResult.UsernameMessageRatingResult[]  getNameUsernameDateRatingMoreThanGivenArticle(int rating, String articleID)
	{
		return null;
	};
	
	/**
	 * Should return the article name, username, article date and article rating of articles commented by this user such that none of these
	 * articles are commented by any other user.
	 * 
	 * 
	 * @return QueryResult.UsernameDateRatingResult[]
	 */
	public QueryResult.NameUsernameDateRatingResult[]  getUsernameDateRatingNotCommentedByAnyUser()
	{
		return null;
	};
	
	/**
	 * Should return the username, date and rating of articles whose date is before given date and whose user has the highest 
	 * weeklyReputation.
	 * 
	 * 
	 * @return QueryResult.UsernameDateRatingResult[]
	 */
	public QueryResult.UsernameDateRatingResult[]  UsernameDateRatingHasHighestReputation(String date)
	{
		return null;
	};
	
	/**
	 * Should first delete rating of the articles for a given date then, output the username, date and rating of these articles.
	 * 
	 * 
	 * @return QueryResult.UsernameDateRatingResult[]
	 */
	public QueryResult.UsernameDateRatingResult[]  UsernameDateRatingDeleteAndSelect(String date)
	{
		return null;
	};
		
	
}
