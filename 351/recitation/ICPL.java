/**
 * 
 */
package ceng.ceng351.recitation;



/**
 * @author Alperen Dalkiran
 * @date Dec 2, 2016
 * @project ceng 
 * 
 *  This interface contains the methods you should implement. You should create
 * a class named CPL which implements interface ICPL, i.e. all methods in this
 * interface.
 *
 */
public interface ICPL {
	
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
	 * 
	 * Release this connection's object database and JDBC resources immediately.
	 * 
	 */
	public void releaseDatabase();
	
	
	/**
	 * Should drop the tables if exists when called. You can assume that
	 * the database will be empty when this function is called. It will be
	 * called only once during evaluation.
	 * 
	 * @return 0 if tables are deleted successfully, any positive number otherwise.
	 */public int dropTables();
	
	
	
	/**
	 * Should create the necessary tables when called. You can assume that
	 * the database will be empty when this function is called. It will be
	 * called only once during evaluation.
	 * 
	 * @return 0 if tables are created successfully, any positive number otherwise.
	 */
	public int createTables();
	
	
	/**
	 * Should insert a player into the database.
	 * 
	 * @param players Player object to be inserted.
	 * @throws AlreadyInsertedException If a player with the same team and number
	 *  	has been inserted before.
	 * @throws TeamNotExistException If there is no such a team whose name equals
	 * 		to teamname field of Player object.
	 */
	public void insertPlayer(Player player) throws AlreadyInsertedException, TeamNotExistException;		// 2 points

	
	/**
	 * Should retrieve the given player.
	 * 
	 * @param number Number of the player.
	 * @param teamname Team of the player.
	 * @return Player object for the given player.
	 */
	public Player getPlayer(int number, String teamname);
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	

	

	
	
	
		
	
		
	
	
	
	
	
	
	
		
	
}
