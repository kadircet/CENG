/**
 * 
 */
package ceng.ceng351.stackoverflowdb;

/**
 * @date November 28, 2016
 * @project ceng 
 *
 */
public class Comment {
	
	//comment(commentID:char(10), articleID:char(10), userID:char(10), message:char(130), date:date, rating:int)
	//The default way to store a date in a MySQL database is by using DATE. 
	//The proper format of a DATE is: YYYY-MM-DD.
	
	String commentID;
	String articleID;
	String userID;
	String message;
	String date;
	int rating;
	
	
	public String getcommentID() {
		return commentID;
	}
	public void setcommentID(String commentID) {
		this.commentID = commentID;
	}
	public String getarticleID() {
		return articleID;
	}
	public void setarticleID(String articleID) {
		this.articleID = articleID;
	}
	public String getuserID() {
		return userID;
	}
	public void setuserID(String userID) {
		this.userID = userID;
	}
	public String getmessage() {
		return message;
	}
	public void setmessage(String message) {
		this.message = message;
	}
	public String getdate() {
		return date;
	}
	public void setdate(String date) {
		this.date = date;
	}
	public int getrating() {
		return rating;
	}
	public void setrating(int rating) {
		this.rating = rating;
	}
	
	
	public Comment(String commentID, String articleID, String userID, String message, String date, int rating ) {
		super();
		this.commentID = commentID;
		this.articleID = articleID;
		this.userID = userID;
		this.message = message;
		this.date = date;
		this.rating = rating;

	}

}
