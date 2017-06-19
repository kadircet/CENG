/**
 * 
 */
package ceng.ceng351.recitation;





/**
 * @author Alperen Dalkiran
 * @date Dec 2, 2016
 * @project ceng 
 *
 */
public class Evaluation {

	public static void main(String args[]) {

		int total = -1;
		
		CPL cpl = new CPL();
		
		/****************************************************/
		cpl.initialize();
		/****************************************************/
		
		/****************************************************/
		// Drop tables
		try {
			total = cpl.dropTables();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Check if tables are dropped
		System.out.println("Dropped " + total + " tables: \n");
		/****************************************************/
		
		
		/****************************************************/
		// Create tables
		try {
			total = cpl.createTables();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Check if tables are created
		System.out.println("Created " + total + " tables: \n");
		/****************************************************/
		
		/***********************************************************/
		//Insert a single player
		try {
			
			cpl.insertPlayer(new Player(8, "Valencia", "Feghouli", 24, "CM"));
			cpl.insertPlayer(new Player(10, "Barcelona", "Messi", 27, "CF"));
			cpl.insertPlayer(new Player(10, "Barcelona", "Messi", 27, "CF"));
			
		} catch (AlreadyInsertedException e) {
			System.out.println("Error: Already inserted exception thrown.");
		} catch (TeamNotExistException e) {
			System.out.println("Error: Team not exist exception thrown.");
		} catch(Exception e) {
			e.printStackTrace();
		}
		/***********************************************************/
		
		
		/***********************************************************/
		try{
			Player p = cpl.getPlayer(10, "Barcelona");
			if(p != null){
				System.out.println("Player 1:\n" + p.toString());
			}
			
		} catch(Exception e) {
			System.out.println("Exception occured: \n\n" + e.toString());
		}
		/***********************************************************/
		
		/***********************************************************/
		cpl.releaseDatabase();
		/***********************************************************/
	
	}

}
