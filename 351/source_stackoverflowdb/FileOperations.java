/**
 * 
 */
package ceng.ceng351.stackoverflowdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @date Nov 29, 2016
 * @project ceng 
 *
 */
public class FileOperations {
	
	
	public static FileWriter createFileWriter( String path) throws IOException {

		File f = new File( path);

		FileWriter fileWriter = null;

		if( f.isDirectory() && !f.exists())
			f.mkdirs();
		else if( !f.isDirectory() && !f.getParentFile().exists())
			f.getParentFile().mkdirs();

		if( !f.isDirectory() && f.exists())
			f.delete();

		fileWriter = new FileWriter( f, false);

		return fileWriter;
	}
	
	
	public static User[] readUserFile(String pathToFile){
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null; 
		
		String strLine;
		
		List<User> userList = new ArrayList<User>();
		User[] userArray = null;
		
		int indexofFirstTab;
		int indexofSecondTab;
		int indexofThirdTab;
		
		
		//user(userID:char(10), username:char(30), registrationDate:date, lastLoginDate:date)
		String userID = null; 
		String username = null;
		String registrationDate = null;
		String lastLoginDate = null;
		
		User user = null;
		
		try {
			
			fileReader = new FileReader(pathToFile);
			bufferedReader = new BufferedReader(fileReader);
			
			//example strline
			//userID	username	registrationDate	lastLoginDate
			
			while((strLine = bufferedReader.readLine())!=null){
				
				//parse strLine
				indexofFirstTab = strLine.indexOf('\t');
				indexofSecondTab = strLine.indexOf('\t',indexofFirstTab+1);
				indexofThirdTab = strLine.indexOf('\t',indexofSecondTab+1);
				
				
				if (indexofFirstTab!=-1 && indexofSecondTab!= -1 && indexofThirdTab!=-1){
					
					userID = strLine.substring(0,indexofFirstTab);	
					username = strLine.substring(indexofFirstTab+1,indexofSecondTab);	
					registrationDate = strLine.substring(indexofSecondTab+1,indexofThirdTab);	
					lastLoginDate = strLine.substring(indexofThirdTab+1);	
					
				}else{
					System.out.println("There is a problem in User File Reading phase");
				}
					
				user = new User(userID,username,registrationDate,lastLoginDate);
				userList.add(user);
				
			}//End of while
			
			
			//Close bufferedReader
			bufferedReader.close();
		
			userArray = new User[userList.size()];
			userList.toArray(userArray);
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return userArray;
	}
		
	
	public static Article[] readArticleFile(String pathToFile){
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null; 
		
		String strLine;
		
		List<Article> articleList = new ArrayList<Article>();
		Article[] articleArray = null;
		
		int indexofFirstTab;
		int indexofSecondTab;
		int indexofThirdTab;
		int indexofFourthTab;
		int indexofFifthTab;
		
		
		//article(articleID:char(10), userID:char(10), name:char(80), description:char(130), date:date, rating:int)
		String articleID = null; 
		String userID = null;
		String name = null;
		String description = null;
		String date = null;
		int rating = -1;
		
		Article article = null;
		
		try {
			
			fileReader = new FileReader(pathToFile);
			bufferedReader = new BufferedReader(fileReader);
			
			//example strline
			//articleID	userID	name	description	date	rating
			
			while((strLine = bufferedReader.readLine())!=null){
				
				//parse strLine
				indexofFirstTab = strLine.indexOf('\t');
				indexofSecondTab = strLine.indexOf('\t',indexofFirstTab+1);
				indexofThirdTab = strLine.indexOf('\t',indexofSecondTab+1);
				indexofFourthTab = strLine.indexOf('\t',indexofThirdTab+1);
				indexofFifthTab = strLine.indexOf('\t',indexofFourthTab+1);
				
				if (indexofFirstTab!=-1 && indexofSecondTab!= -1 && indexofThirdTab!=-1 && indexofFourthTab!=-1 && indexofFifthTab!= -1){
					
					articleID = strLine.substring(0,indexofFirstTab);	
					userID = strLine.substring(indexofFirstTab+1,indexofSecondTab);	
					name = strLine.substring(indexofSecondTab+1,indexofThirdTab);	
					description = strLine.substring(indexofThirdTab+1,indexofFourthTab);	
					date = strLine.substring(indexofFourthTab+1,indexofFifthTab);	
					rating = Integer.parseInt(strLine.substring(indexofFifthTab+1));
					
				}else{
					System.out.println("There is a problem in Article File Reading phase");
				}
					
				article = new Article(articleID,userID,name,description,date,rating);
				articleList.add(article);
				
			}//End of while
			
			
			//Close bufferedReader
			bufferedReader.close();
		
			articleArray = new Article[articleList.size()];
			articleList.toArray(articleArray);
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return articleArray;
	}
	
	
	public static Comment[] readCommentFile(String pathToFile){
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null; 
		
		String strLine;
		
		List<Comment> commentList = new ArrayList<Comment>();
		Comment[] commentArray = null;
		
		int indexofFirstTab;
		int indexofSecondTab;
		int indexofThirdTab;
		int indexofFourthTab;
		int indexofFifthTab;
		
		
		//comment(commentID:char(10), articleID:char(10), userID:char(10), message:char(130), date:date, rating:int)
		String commentID = null;
		String articleID = null; 
		String userID = null;
		String message = null;
		String date = null;
		int rating = -1;
		
		Comment comment = null;
		
		try {
			
			fileReader = new FileReader(pathToFile);
			bufferedReader = new BufferedReader(fileReader);
			
			//example strline
			//CommentID	userID	articleID	message
			
			while((strLine = bufferedReader.readLine())!=null){
				
				//parse strLine
				indexofFirstTab = strLine.indexOf('\t');
				indexofSecondTab = strLine.indexOf('\t',indexofFirstTab+1);
				indexofThirdTab = strLine.indexOf('\t',indexofSecondTab+1);
				indexofFourthTab = strLine.indexOf('\t',indexofThirdTab+1);
				indexofFifthTab = strLine.indexOf('\t',indexofFourthTab+1);
				
				if (indexofFirstTab!=-1 && indexofSecondTab!= -1 && indexofThirdTab!=-1 && indexofFourthTab!=-1 && indexofFifthTab!= -1){
					
					commentID = strLine.substring(0,indexofFirstTab);	
					articleID = strLine.substring(indexofFirstTab+1,indexofSecondTab);	
					userID = strLine.substring(indexofSecondTab+1,indexofThirdTab);	
					message = strLine.substring(indexofThirdTab+1,indexofFourthTab);	
					date = strLine.substring(indexofFourthTab+1,indexofFifthTab);	
					rating = Integer.parseInt(strLine.substring(indexofFifthTab+1));
					
				}else{
					System.out.println("There is a problem in Comment File Reading phase");
				}
					
				comment = new Comment(commentID,articleID,userID,message,date,rating);
				commentList.add(comment);
				
			}//End of while
			
			
			//Close bufferedReader
			bufferedReader.close();
		
			commentArray = new Comment[commentList.size()];
			commentList.toArray(commentArray);
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return commentArray;
	}

	//Reputation
	public static Reputation[] readReputationFile(String pathToFile){
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null; 
		
		String strLine;
		
		List<Reputation> reputationList = new ArrayList<Reputation>();
		Reputation[] reputationArray = null;
		
		int indexofFirstTab;
		int indexofSecondTab;
		int indexofThirdTab;
		int indexofFourthTab;
		int indexofFifthTab;
		
		
		//reputation(reputationID:char(10), userID:char(10), weeklyReputation:int, monthlyReputation:int, yearlyReputation:int,alltimeReputation:int)
		String reputationID = null;
		String userID = null; 
		int weeklyReputation = -1;
		int monthlyReputation = -1;
		int yearlyReputation = -1;
		int alltimeReputation = -1;
		
		Reputation reputation = null;
		
		try {
			
			fileReader = new FileReader(pathToFile);
			bufferedReader = new BufferedReader(fileReader);
			
			//example strline
			//reputationID	userID	weeklyReputation	monthlyReputation	yearlyReputation	alltimeReputation
			
			while((strLine = bufferedReader.readLine())!=null){
				
				//parse strLine
				indexofFirstTab = strLine.indexOf('\t');
				indexofSecondTab = strLine.indexOf('\t',indexofFirstTab+1);
				indexofThirdTab = strLine.indexOf('\t',indexofSecondTab+1);
				indexofFourthTab = strLine.indexOf('\t',indexofThirdTab+1);
				indexofFifthTab = strLine.indexOf('\t',indexofFourthTab+1);
				
				if (indexofFirstTab!=-1 && indexofSecondTab!= -1 && indexofThirdTab!=-1 && indexofFourthTab!=-1 && indexofFifthTab!= -1){
					
					reputationID = strLine.substring(0,indexofFirstTab);	
					userID = strLine.substring(indexofFirstTab+1,indexofSecondTab);	
					weeklyReputation = Integer.parseInt(strLine.substring(indexofSecondTab+1,indexofThirdTab));	
					monthlyReputation = Integer.parseInt(strLine.substring(indexofThirdTab+1,indexofFourthTab));	
					yearlyReputation = Integer.parseInt(strLine.substring(indexofFourthTab+1,indexofFifthTab));	
					alltimeReputation = Integer.parseInt(strLine.substring(indexofFifthTab+1));
					
				}else{
					System.out.println("There is a problem in Reputation File Reading phase");
				}
					
				reputation = new Reputation(reputationID,userID,weeklyReputation,monthlyReputation,yearlyReputation,alltimeReputation);
				reputationList.add(reputation);
				
			}//End of while
			
			
			//Close bufferedReader
			bufferedReader.close();
		
			reputationArray = new Reputation[reputationList.size()];
			reputationList.toArray(reputationArray);
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return reputationArray;
	}


}
