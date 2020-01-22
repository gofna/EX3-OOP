package gameClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import utils.StdDraw;

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
//		int id1 = 208888875; // "dummy existing ID
//		int level = 0;
////		allUsers();
////		printLog();
//		times();
//		showMyPos(0);
////		topScores("208888875");
//		showMaxLevel("208888875");
//		// String kml = getKML(id1, level);
//		// System.out.println("data/" + level+".kml");
//		// System.out.println(kml);

	}

	public static double[] topScores(String id) {
		recordsTable.insertMap();
		double[] arr = new double[24];
		for (int i = 0; i <= showMaxLevel(id); i++) {
			double max = -1;
			String CustomersQuery = ("SELECT MAX(score) FROM Logs where userID="+id + " AND levelID=" + i);
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(CustomersQuery);
				while (resultSet.next()) {
						max = resultSet.getDouble("MAX(score)");
						System.out.println("the max score in level " + i + " is " +max);
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

	public static int times(String id) {
		String CustomersQuery = "SELECT * FROM Logs where userID="+id;
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

	public static int showMyPos(int scenario , String id) {
		int count = 0;
		insertMap();
		double sameGrade = topScore1(scenario, id);
		try {
			Class.forName("com.mysql.jdbc.Driver"); // load data base
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword); // connection
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT userID ,MIN(moves), MAX(score),  levelID" + " FROM Logs"
					+ " WHERE levelID ="+ scenario + " GROUP BY userID" + " ORDER BY MAX(score) DESC";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);

			while (resultSet.next()) {
				if (resultSet.getInt("UserID") != Integer.parseInt(id)
						&& resultSet.getInt("MIN(moves)") <= recordsTable.maxMoves.get(scenario)) {
					if ( resultSet.getDouble("MAX(score)") != sameGrade) {
						System.out.println("Id: " + resultSet.getInt("UserID") + ", level:\t"
								+ resultSet.getInt("levelID") + ", score:\t" + resultSet.getDouble("MAX(score)")
								+ ", moves:\t" + resultSet.getInt("MIN(moves)"));
						count++;
					}
				} else if (resultSet.getInt("UserID") == 208888875
						&& resultSet.getInt("MIN(moves)") <= recordsTable.maxMoves.get(scenario)) {
					System.out.println("Id: " + resultSet.getInt("UserID") + ", level:\t" + resultSet.getInt("levelID")
							+ ", score:\t" + resultSet.getDouble("MAX(score)") + ", moves:\t"
							+ resultSet.getInt("MIN(moves)"));
					System.out.println("my position in scenario " + scenario + " is: " + count);

					break;
				}

			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public static double topScore1(int scenario,String id) {
		double max = -1;
		recordsTable.insertMap();
			String CustomersQuery = ("SELECT MAX(score) FROM Logs where userID="+id+ " AND levelID=" + scenario);
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(CustomersQuery);
				while (resultSet.next()) {
						max = resultSet.getDouble("MAX(score)");
						System.out.println("the max score in level " + scenario + " is " +max);
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
		return max;
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