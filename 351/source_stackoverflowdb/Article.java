/**
 * 
 */
package ceng.ceng351.stackoverflowdb;

/**
 * @date November 28, 2016
 * @project ceng 
 *
 */
public class Article {
	
	//article(articleID:char(10), userID:char(10), name:char(80), description:char(130), date:date, rating:int)
	//The default way to store a date in a MySQL database is by using DATE. 
	//The proper format of a DATE is: YYYY-MM-DD.
	
	String articleID;
	String userID;
	String name;
	String description;
	String date;
	int rating;
	
	
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
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name = name;
	}
	public String getdescription() {
		return description;
	}
	public void setdescription(String description) {
		this.description = description;
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
	
	
	public Article(String articleID, String userID, String name, String description, String date, int rating ) {
		super();
		this.articleID = articleID;
		this.userID = userID;
		this.name = name;
		this.description = description;
		this.date = date;
		this.rating = rating;

	}

	
	/*public String toString() {
		return articleID + "\t" + userID + "\t" + rating + "\t" + date ;
	}*/

}
