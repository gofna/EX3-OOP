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
 * This class used to allow retrieval from MySQL Data-Base information such as-
 * 1. how many times did you play 
 * 2. you'r maximum open level 
 * 3. the best score
 * each level 
 * 4. you'r position in each level in relation to others in the
 * data-base.
 * 
 * @author Gofna Ivry and Maor Ovadia
 *
 */
public class recordsTable {
	public static final String jdbcUrl = "jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser = "student";
	public static final String jdbcUserPassword = "OOP2020student";
	public static MyGameGUI mygame;
	private static HashMap<Integer, Integer> maxMoves = new HashMap<Integer, Integer>();

	/**
	 * this function return the top scores according to given id, in each level.
	 * 
	 * @param id the id to check.
	 * @return array with the best scores in each level.
	 */
	public static double[] topScores(String id) {
		recordsTable.insertMap();
		double[] arr = new double[24];
		for (int i = 0; i <= showMaxLevel(id); i++) {
			double max = -1;
			String CustomersQuery = ("SELECT MAX(score) FROM Logs where userID=" + id + " AND levelID=" + i);
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(CustomersQuery);
				resultSet.next();
				max = resultSet.getDouble("MAX(score)");
				System.out.println("the max score in level " + i + " is " + max);
				arr[i] = max;
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

	/**
	 * the function pull the maximum level that open in the server to a given id.
	 * 
	 * @param id the id to check.
	 * @return the maximum level that open in the server.
	 */

	public static int showMaxLevel(String id) {
		int maxLevel = -1;
		String CustomersQuery = "SELECT MAX(levelID) FROM Logs where userId=" + id;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(CustomersQuery);
			resultSet.next();
			maxLevel = resultSet.getInt("MAX(levelID)");
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

	/**
	 * this function pull the numbers of games that a given id played.
	 * 
	 * @param id the id to check.
	 * @return number of times that the user played.
	 */
	public static int times(String id) {
		String CustomersQuery = "SELECT COUNT(userID) FROM Logs where userID=" + id;
		int count = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(CustomersQuery);
			resultSet.next();
			count = resultSet.getInt("COUNT(userID)");
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

	/**
	 * the function return the position of an given id, in order to the scores in a
	 * given level.
	 * 
	 * @param scenario the level to check position
	 * @param id       the id to check
	 * @return the position of the user , in order to the scores
	 */
	public static int showMyPos(int scenario, String id) {
		int count = 0;
		insertMap();
		double sameGrade = topScore1(scenario, id);
		try {
			Class.forName("com.mysql.jdbc.Driver"); // load data base
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword); // connection
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT userID ,MIN(moves), MAX(score),  levelID" + " FROM Logs"
					+ " WHERE levelID =" + scenario + " GROUP BY userID" + " ORDER BY MAX(score) DESC";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);

			while (resultSet.next()) {
				if (resultSet.getInt("UserID") != Integer.parseInt(id)
						&& resultSet.getInt("MIN(moves)") <= recordsTable.maxMoves.get(scenario)) {
					if (resultSet.getDouble("MAX(score)") != sameGrade) {
						count++;
					}
				} else if (resultSet.getInt("UserID") == 208888875
						&& resultSet.getInt("MIN(moves)") <= recordsTable.maxMoves.get(scenario)) {
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

	/**
	 * this function return the top score according to given id, and level.
	 * @param scenario the level to filter.
	 * @param id the id to check
	 * @return the top score the user got in a level.
	 */
	public static double topScore1(int scenario, String id) {
		double max = -1;
		recordsTable.insertMap();
		String CustomersQuery = ("SELECT MAX(score) FROM Logs where userID=" + id + " AND levelID=" + scenario);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(CustomersQuery);
			while (resultSet.next()) {
				max = resultSet.getDouble("MAX(score)");
				System.out.println("the max score in level " + scenario + " is " + max);
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