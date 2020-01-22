package gameClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;

import sun.swing.text.CountingPrintable;

/**
 * This class represents a simple example of using MySQL Data-Base. Use this
 * example for writing solution.
 * 
 * @author boaz.benmoshe
 *
 */
public class SimpleDB {
	public static final String jdbcUrl = "jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser = "student";
	public static final String jdbcUserPassword = "OOP2020student";

	/**
	 * Simple main for demonstrating the use of the Data-base
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int id1 = 208888875; // "dummy existing ID
		int level = 0;
//		allUsers();
		printLog();
//		showMe();
		String kml = getKML(id1, level);
	//	System.out.println("data/" + level+".kml");
		System.out.println(kml);
		
	}
	
	
	
	public static void showMe() {
		String CustomersQuery = "SELECT * FROM Logs where userID=208888875";
		int count = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(CustomersQuery);
			while (resultSet.next()) {
				System.out.println("Id: " + resultSet.getInt("UserID") + "," + resultSet.getInt("levelID") + ","+
						resultSet.getDouble("score")+ "," + resultSet.getInt("moves") + ","+resultSet.getInt("logID")+ ","  + resultSet.getDate("time"));
				count++;
			}
			System.out.println("you played : "+ count + " Times");
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * simply prints all the games as played by the users (in the database).
	 * 
	 */
	public static void printLog() {
//		LinkedList<Integer> id = new LinkedList<Integer>();
//		HashMap<Double, LinkedList<Integer>> sort = new HashMap<Double, LinkedList<Integer>>();
		try {
			Class.forName("com.mysql.jdbc.Driver"); // load data base
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword); // connection
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);

			while (resultSet.next()) {
				System.out.println("Id: " + resultSet.getInt("UserID") + ", level:\t" + resultSet.getInt("levelID")
				+ ", score:\t" + resultSet.getDouble("score") + ", moves:\t" + resultSet.getInt("moves")+ ", numbre:\t" + resultSet.getInt("logID"));
					
			}
			resultSet.close();
			statement.close();
			connection.close();
		}

		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * this function returns the KML string as stored in the database (userID,
	 * level);
	 * 
	 * @param id
	 * @param level
	 * @return
	 */
	public static String getKML(int id, int level) {
		String ans = null;
		String allCustomersQuery = "SELECT * FROM Users where userID=" + id + ";";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			if (resultSet != null && resultSet.next()) {
				ans = resultSet.getString("kml_" + level);
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ans;
	}

	public static int allUsers() {
		int ans = 0;
		String allCustomersQuery = "SELECT * FROM Users;";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			while (resultSet.next()) {
				System.out.println("Id: " + resultSet.getInt("UserID"));
				ans++;
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ans;
	}
}
