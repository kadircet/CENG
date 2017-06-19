/**
 * 
 */
package ceng.ceng351.stackoverflowdb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import ceng.ceng351.stackoverflowdb.QueryResult.UsernameDateRatingResult;
import ceng.ceng351.stackoverflowdb.QueryResult.UsernameMessageRatingAlltimereputationResult;
import ceng.ceng351.stackoverflowdb.QueryResult.UseridUsernameRegistrationdateWeeklyreputationResult;
import ceng.ceng351.stackoverflowdb.QueryResult.UseridUsernameLastlogindateResult;
import ceng.ceng351.stackoverflowdb.QueryResult.UsernameMessageRatingResult;
import ceng.ceng351.stackoverflowdb.QueryResult.NameUsernameDateRatingResult;



/**
 * @date Nov 29, 2016
 * @project ceng 
 *
 */
public class Evaluation {
	
	
	private static String user = "e2036457";  
    private static String password = "08f893";
    private static String host = "144.122.71.165";
    private static String database = "db2036457";
    private static int port = 3306;
     
    private static Connection con;
    
    public static void connect() {
		
		String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con =  DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
	}
    
    public static void disConnect() {
		
        try {
            con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public static void addInputTitle(String title, BufferedWriter bufferedWriter) throws IOException {
		bufferedWriter.write("*** " + title + " ***" + System.getProperty("line.separator"));
	}
	
	public static void printAllTables(BufferedWriter bufferedWriter) throws IOException {
		
		String sql1 = "show tables";
		String sql2 = "describe ";
		
		Vector<String> tables = new Vector<String>();
		
		try
		{
			// Execute query
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql1);
			
			// Process the result set
			while(rs.next()) 
			{
				tables.add(rs.getString(1));
			}
			
			for(int i=0; i < tables.size(); i++) {
				rs = st.executeQuery(sql2 + tables.get(i));
				
				// Print table name
				bufferedWriter.write("--- " + tables.get(i) + " ---" + System.getProperty("line.separator"));
				
				// Print field names and types
				while(rs.next()) {
					bufferedWriter.write(rs.getString(1) + " " + rs.getString(2) + System.getProperty("line.separator"));
				}
				
				bufferedWriter.write(System.getProperty("line.separator"));
			}
			
		} catch (SQLException e) {
			printException(e);
		}
	}
	
	private static void printException(SQLException ex) {
		System.out.println(ex.getMessage() + "\n");
	}
	
	public static void printLine(String result, BufferedWriter bufferedWriter) throws IOException {
		bufferedWriter.write(result + System.getProperty("line.separator"));
	}
	
	public static void addDivider(BufferedWriter bufferedWriter) throws IOException {
		bufferedWriter.write( System.getProperty("line.separator")+ "--------------------------------------------------------------" + System.getProperty("line.separator"));
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int numberofInsertions = 0;
		int numberofTablesCreated = 0;
		int numberofTablesDropped = 0;
		int numberofChanged = 0;
		
		/***********************************************************/
		// TODO While running on your local machine, change stackoverflowdb Directory accordingly
		String stackoverflowdbDirectory = "/home/kadircet/ceng/351/source_stackoverflowdb/test/";
		/***********************************************************/
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		//Connect to the database
		connect();
		
		// Create STACKOVERFLOWDB object
		STACKOVERFLOWDB stackoverflowdb = null;
		
		
		try {
			
			// Create stackoverflowdb object and initialize
			stackoverflowdb = new STACKOVERFLOWDB();
			stackoverflowdb.initialize();
		
			/***********************************************************/
			/*************Create File Writer starts*********************/
			/***********************************************************/
			fileWriter = FileOperations.createFileWriter( stackoverflowdbDirectory + System.getProperty("file.separator") + "output" + System.getProperty("file.separator") + "Output.txt");
			bufferedWriter =  new BufferedWriter(fileWriter);
			/***********************************************************/
			/*************Create File Writer ends***********************/
			/***********************************************************/
		
			
			/***********************************************************/
			/*******************Create tables starts********************/
			/***********************************************************/
			addDivider(bufferedWriter);
			addInputTitle("Create tables",bufferedWriter);
			numberofTablesCreated = 0;
			
			// Create Tables
			try {
				numberofTablesCreated = stackoverflowdb.createTables();
				
				// Check if tables are created
				printLine("Created " + numberofTablesCreated + " tables.", bufferedWriter);

			} catch(Exception e) {
				e.printStackTrace();
			}
			
			addDivider(bufferedWriter);
			/***********************************************************/
			/*******************Create tables ends**********************/
			/***********************************************************/
			
			return;
			/***********************************************************/
			/*******************Insert INTO User starts*****************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Insert into User",bufferedWriter);
			//insert User
			numberofInsertions = 0;
			User[] users = FileOperations.readUserFile(
			stackoverflowdbDirectory + System.getProperty("file.separator") +
					"data" + System.getProperty("file.separator") +
					"Test_UserFile.txt");
			
			numberofInsertions = stackoverflowdb.insertUser(users);
			printLine( numberofInsertions + " users are inserted.",bufferedWriter);
			addDivider(bufferedWriter);
			/***********************************************************/
			/*******************Insert INTO User ends*******************/
			/***********************************************************/
			
			
			/***********************************************************/
			/*******************Insert INTO Article starts**********/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Insert into Article",bufferedWriter);
			//insert Article
			numberofInsertions = 0;
			Article[] articles = FileOperations.readArticleFile(
			stackoverflowdbDirectory + System.getProperty("file.separator") +
					"data" + System.getProperty("file.separator") +
					"Test_ArticleFile.txt");
			
			numberofInsertions = stackoverflowdb.insertArticle(articles);
			printLine( numberofInsertions + " article are inserted.", bufferedWriter);
			addDivider(bufferedWriter);
			/***********************************************************/
			/*******************Insert INTO Article ends************/
			/***********************************************************/
	
			
			/***********************************************************/
			/*******************Insert INTO Comment starts*************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Insert into Comment",bufferedWriter);
			//insert Comment
			numberofInsertions = 0;
			Comment[] comments = FileOperations.readCommentFile(
			stackoverflowdbDirectory + System.getProperty("file.separator") +
					 "data" + System.getProperty("file.separator") +
					"Test_CommentFile.txt");
			
			numberofInsertions = stackoverflowdb.insertComment(comments);
			printLine(numberofInsertions + " comments are inserted.",bufferedWriter);
			addDivider(bufferedWriter);
			/***********************************************************/
			/*******************Insert INTO Comment ends***************/
			/***********************************************************/
	
			
			/***********************************************************/
			/*******************Insert INTO Reputation starts**********/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Insert into Reputation",bufferedWriter);
			//insert Reputation
			numberofInsertions = 0;
			Reputation[] reputations = FileOperations.readReputationFile(
			stackoverflowdbDirectory + System.getProperty("file.separator") +
					 "data" + System.getProperty("file.separator") +
					"Test_ReputationFile.txt");
			
			numberofInsertions = stackoverflowdb.insertReputation(reputations);
			printLine( numberofInsertions + " reputations are inserted.",bufferedWriter);
			addDivider(bufferedWriter);
			/***********************************************************/
			/*******************Insert INTO Reputation ends************/
			/***********************************************************/
			
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Find the article which has the highest rating,",bufferedWriter);
			try {
				
				QueryResult.UsernameDateRatingResult[] UsernameDateRatingResultArray = stackoverflowdb.getArticleHighestRating();
				
				//Header Line
				printLine("Username" + "\t" + "Date" + "\t" + "Rating",bufferedWriter);
				
				if(UsernameDateRatingResultArray != null) {
					for(UsernameDateRatingResult usernameDateRatingResult : UsernameDateRatingResultArray){
						printLine(usernameDateRatingResult.toString(),bufferedWriter);	
					}	
				}
				
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Find the user whose comment message does not contain 'mysql' and the comment has the lowest rating",bufferedWriter);
			try {
					
				QueryResult.UsernameMessageRatingAlltimereputationResult[] UsernameMessageRatingAlltimereputationResultList = stackoverflowdb.getCommentLowestRating();
				
				//Header Line
				printLine("Username" + "\t" + "Message" + "\t" + "Rating" + "\t" + "AlltimeReputation",bufferedWriter);

				
				if(UsernameMessageRatingAlltimereputationResultList != null) {
					for(UsernameMessageRatingAlltimereputationResult usernameMessageRatingAlltimereputationResult : UsernameMessageRatingAlltimereputationResultList){
						printLine(usernameMessageRatingAlltimereputationResult.toString(), bufferedWriter);	
					}
				}
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("List the userID, username, registration date and weekly reputation of users whose registration date is after a given date",bufferedWriter);
			try {
				
				
				QueryResult.UseridUsernameRegistrationdateWeeklyreputationResult[] UseridUsernameRegistrationdateWeeklyreputationResultArray = stackoverflowdb.getUseridUsernameAfterGivenDate("2008-09-30");
				
				//Header Line
				printLine("UserID" + "\t" + "Username" + "\t" + "RegistrationDate" + "\t" +  "WeeklyReputation",bufferedWriter);

				if(UseridUsernameRegistrationdateWeeklyreputationResultArray != null) {
						for(UseridUsernameRegistrationdateWeeklyreputationResult useridUsernameRegistrationdateWeeklyreputationResult : UseridUsernameRegistrationdateWeeklyreputationResultArray){
						printLine(useridUsernameRegistrationdateWeeklyreputationResult.toString(), bufferedWriter);	
					}	
				}
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Multiply rating of the comment by 2 whose date is after for a given date",bufferedWriter);
			try {
				
				numberofChanged = stackoverflowdb.MultiplyComment("2010-05-01");
				printLine( numberofChanged + " rows are changed.", bufferedWriter);
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
		
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("List the username, message, and rating of comments which does not contain given string in their message ant it's rating is more than given rating",bufferedWriter);
			try {
				
				
				QueryResult.UsernameMessageRatingResult[] UsernameMessageRatingResultList = stackoverflowdb.getUsernameMessageRatingMoreThanGivenRating("loop",300);
					
				//Header Line
				printLine("Username" + "\t" + "Message" + "\t" + "Rating", bufferedWriter);
			
				for(UsernameMessageRatingResult usernameMessageRatingResult : UsernameMessageRatingResultList){
					printLine(usernameMessageRatingResult.toString(),bufferedWriter);	
				}
					
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("List the userID, username and last login date of user/s who comment all the articles commented by a given user",bufferedWriter);
			try {
				
				QueryResult.UseridUsernameLastlogindateResult[] UseridUsernameLastlogindateResultList = stackoverflowdb.getUsernameMessageRatingCommentedByGivenUser("U102");
				
				//Header Line
				printLine("UserID" + "\t" + "Username" + "\t" + "LastLoginDate", bufferedWriter);
				
				for(UseridUsernameLastlogindateResult useridUsernameLastlogindateResult : UseridUsernameLastlogindateResultList){
					printLine(useridUsernameLastlogindateResult.toString(),bufferedWriter);	
				}
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("List username, comment message and rating of comments whose comment rating is more than given number of ratings for a given article,",bufferedWriter);
		
			try {
				
				QueryResult.UsernameMessageRatingResult[] UsernameMessageRatingResultList = stackoverflowdb.getNameUsernameDateRatingMoreThanGivenArticle(180,"A103");
				
				//Header Line
				printLine("Username" + "\t" + "Message" + "\t" + "Rating", bufferedWriter);
				
				for(UsernameMessageRatingResult usernameMessageRatingResult : UsernameMessageRatingResultList){
					printLine(usernameMessageRatingResult.toString(),bufferedWriter);	
				}
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("List the article name, username, article date and article rating of articles commented by this user such that none of these articles are commented by any other user,",bufferedWriter);
			try {
				
				QueryResult.NameUsernameDateRatingResult[] NameUsernameDateRatingResultList = stackoverflowdb.getUsernameDateRatingNotCommentedByAnyUser();
				
				//Header Line
				printLine("Name" + "\t" + "Username" + "\t" + "Date" + "\t" + "Rating", bufferedWriter);
				
				for(NameUsernameDateRatingResult nameUsernameDateRatingResult : NameUsernameDateRatingResultList){
					printLine(nameUsernameDateRatingResult.toString(),bufferedWriter);	
				}
					
			
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Find the article whose date is before given date and whose user has the highest weeklyReputation",bufferedWriter);
			try {
				
				
				QueryResult.UsernameDateRatingResult[] UsernameDateRatingResultList = stackoverflowdb.UsernameDateRatingHasHighestReputation("2011-01-01");
					
				//Header Line
				printLine("Username" + "\t" + "Date" + "\t" + "Rating", bufferedWriter);
			
				for(UsernameDateRatingResult usernameDateRatingResult : UsernameDateRatingResultList){
					printLine(usernameDateRatingResult.toString(),bufferedWriter);	
				}
					
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Delete rating of the articles for a given date and list the username, date and rating of these articles",bufferedWriter);
			try {
				
				
				QueryResult.UsernameDateRatingResult[] UsernameDateRatingResultList = stackoverflowdb.UsernameDateRatingDeleteAndSelect("2008-09-13");
					
				//Header Line
				printLine("Username" + "\t" + "Date" + "\t" + "Rating", bufferedWriter);
			
				for(UsernameDateRatingResult usernameDateRatingResult : UsernameDateRatingResultList){
					printLine(usernameDateRatingResult.toString(),bufferedWriter);	
				}
					
				
			} catch(Exception e) {
				printLine("Exception occured: \n\n" + e.toString(),bufferedWriter);
			}
			addDivider(bufferedWriter);
			/***********************************************************/
			/***********************************************************/
			/***********************************************************/
			
			/***********************************************************/
			/*************Drop tables starts****************************/
			/***********************************************************/
			/*addDivider(bufferedWriter);
			addInputTitle("Drop tables", bufferedWriter);
			numberofTablesDropped = 0;
			
			// Drop tables
			try {
				numberofTablesDropped = stackoverflowdb.dropTables();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			// Check if tables are dropped
			printLine("Dropped " + numberofTablesDropped + " tables.", bufferedWriter);
			
			addDivider(bufferedWriter);
			/***********************************************************/
			/*************Drop tables ends******************************/
			/***********************************************************/
			
			
			/*//Close Writer
			bufferedWriter.close();
			
			//Close Connection
			disConnect();*/
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
	}

}
