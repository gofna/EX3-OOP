package gameClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a simple example of using MySQL Data-Base. Use this
 * example for writing solution.
 * 
 * @author boaz.benmoshe
 *
 */
public class recordsTable {
	public static final String jdbcUrl = "jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser = "student";
	public static final String jdbcUserPassword = "OOP2020student";
	public static MyGameGUI mygame;
	private static HashMap<Integer, Integer> maxMoves = new HashMap<Integer, Integer>();

	/**
	 * Simple main for demonstrating the use of the Data-base
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int id1 = 208888875; // "dummy existing ID
		int level = 0;
//		allUsers();
//		printLog();
		showMe();
		topScores("208888875");
		showMaxLevel("208888875");
		// String kml = getKML(id1, level);
		// System.out.println("data/" + level+".kml");
		// System.out.println(kml);

	}

	public static double[] topScores(String id) {
		recordsTable.insertMap();
		double[] arr = new double[24];
		for (int i = 0; i <= showMaxLevel(id); i++) {
			double max = -1;
			String CustomersQuery = ("SELECT * FROM Logs where userID=208888875 AND levelID=" + i);
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(CustomersQuery);
				while (resultSet.next()) {
					if (resultSet.getDouble("score") > max) {
						max = resultSet.getDouble("score");
					}
				}
				if (max != -1) { // should be with maxMove
//						System.out.println("max score in level " + i + " is " + max);
					arr[i] = max;
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
		}
		return arr;
	}

	public static int showMaxLevel(String id) {
		int maxLevel = -1;
		String CustomersQuery = "SELECT * FROM Logs where userId=" + id;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(CustomersQuery);
			while (resultSet.next()) {
				if (resultSet.getInt("levelID") > maxLevel) {
					maxLevel = resultSet.getInt("levelID");
				}
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
		return maxLevel;
	}

	public static int showMe() {
		String CustomersQuery = "SELECT * FROM Logs where userID=208888875";
		int count = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(CustomersQuery);
			while (resultSet.next()) {
//				System.out.println("Id: " + resultSet.getInt("UserID") + "," + resultSet.getInt("levelID") + ","+
//						resultSet.getDouble("score")+ "," + resultSet.getInt("moves") + ","+resultSet.getInt("logID")+ ","  + resultSet.getDate("time"));

				count++;
			}
			System.out.println("you played : " + count + " Times");
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
		return count;
	}
	
	public static int showMyPos(int scenario) {
		ArrayList<Double> top = new ArrayList<Double>();
		String CustomersQuery = "SELECT * FROM Logs where levelID="+scenario;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(CustomersQuery);
			while (resultSet.next()) {
				double[] t = topScores(""+resultSet.getInt("userID"));
				top.add(t[scenario]);
				
			}
			
			top.s
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
		return 0;
		
	}

	public static void insertMap() {
		recordsTable.maxMoves.put(0, 290);
		recordsTable.maxMoves.put(1, 580);
		recordsTable.maxMoves.put(3, 580);
		recordsTable.maxMoves.put(5, 500);
		recordsTable.maxMoves.put(9, 580);
		recordsTable.maxMoves.put(11, 580);
		recordsTable.maxMoves.put(13, 580);
		recordsTable.maxMoves.put(16, 290);
		recordsTable.maxMoves.put(19, 580);
		recordsTable.maxMoves.put(20, 290);
		recordsTable.maxMoves.put(23, 1140);
	}

}