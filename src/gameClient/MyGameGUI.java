package gameClient;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import elements.fruit;
import gui.Graph_GUI;
import utils.Point3D;
import utils.StdDraw;

/**
 * this class represent a graphical game by using the GameServer API. the class
 * allowing to choose a scenario to the game, place the robots and play the game
 * manually or watch a automatic mode game. in addition, the class allowing to
 * displays the score , and the time left to the end of the game. the manually
 * game allow to play the game by mouse press (on a robot, and the on node on
 * the graph).
 * 
 * @author Gofna Ivry and Maor Ovadia
 *
 */
public class MyGameGUI implements Runnable {
	private DGraph graph;
	private Graph_GUI gui;
	private Graph_Algo ga;
	static List<fruit> fruits = new LinkedList<fruit>();
	public game_service game;
	public int scenario;
	private int numOfRobots;
	public boolean auto;

	/**
	 * Constructor to the graphical window of the game. initializing the scenario
	 * game, the graph, the fruits on the graph and print the info game.
	 */

	public MyGameGUI() {
		chooseScenario();
		if (StdDraw.id != null && StdDraw.id != "") {
			Game_Server.login(Integer.parseInt(StdDraw.id));
		}
		this.game = Game_Server.getServer(this.scenario); // you have [0,23] games
		StdDraw.game = this;
		String g = game.getGraph();
		this.graph = new DGraph();
		this.graph.init(g);
		this.ga = new Graph_Algo(this.graph);
		this.gui = new Graph_GUI(this.graph);
		String info = game.toString();
		JSONObject line;

		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			this.numOfRobots = ttt.getInt("robots");
			System.out.println(info);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.gui.initGUI();
		drawFruits();
		StdDraw.show();
	}

	/**
	 * auxiliary function to let the user choose the scenario by using JFrame
	 * window.
	 */
	private void chooseScenario() {
		JFrame level = new JFrame();
		String scenario_num = JOptionPane.showInputDialog(level, "insert a level 0 - 23:");
		this.scenario = Integer.parseInt(scenario_num);
	}

	/**
	 * this function let the user to place the robot before the game in strategic
	 * place. the function use JFrame window and ask the user to place the robot by
	 * give a node number. after- send to the server the node and use StdDraw to
	 * show the robot icon position on the graphical window, and start the game and
	 * the tread.
	 */
	public void placeRobot() {
		Thread t = new Thread(this);
		for (int i = 0; i < this.numOfRobots; i++) { // ask the user to insert start positions for each robot
			JFrame start = new JFrame();
			String node = JOptionPane.showInputDialog(start,
					"you have " + this.numOfRobots + " robots , choose start position to the robot number " + (i + 1)
							+ "\n (bettween 0 - " + (this.graph.nodeSize() - 1) + ")");

			game.addRobot(Integer.parseInt(node));
			node_data n = this.graph.getNode(Integer.parseInt(node));
			StdDraw.picture(n.getLocation().x(), n.getLocation().y(), "data/robot.PNG", 0.002, 0.001);
			StdDraw.show();
		}
		t.start();
		this.game.startGame();
	}

	int id = -1;
	int nextNode = -1;
	int posR = -1;

	/**
	 * this function check(after mouse is pressed) if the the user click on a robot.
	 * the function check if the "click" was in the range of the robot icon by using
	 * the server to get the position and the id of each robot on the window. if yes
	 * - it change the global variables - id(which robot choose) and the posR(the
	 * position of this robot) - to use this information to the next click.
	 */
	public void checkClickR() {
		List<String> log = game.getRobots();
		if (log != null) {
			for (int i = 0; i < log.size(); i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					String pos = ttt.getString("pos");
					Point3D p = new Point3D(pos);
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");

					if (StdDraw.mouseX() > p.x() - 0.002 && StdDraw.mouseX() < p.x() + 0.002
							&& StdDraw.mouseY() > p.y() - 0.001 && StdDraw.mouseY() < p.y() + 0.001) {
						id = rid;
						posR = src;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * this function check(after mouse is pressed) if the the user click on one of
	 * the nodes. the function check if the "click" was in the range of a node
	 * position. if yes- it change the global variable - nextNode, to use it in the
	 * move function.
	 */
	public void checkClickN() {
		for (node_data n : this.graph.getV()) {
			if (StdDraw.mouseX() > n.getLocation().x() - 0.0003 && StdDraw.mouseX() < n.getLocation().x() + 0.0003
					&& StdDraw.mouseY() > n.getLocation().y() - 0.0003
					&& StdDraw.mouseY() < n.getLocation().y() + 0.0003 && id != -1) {
				nextNode = n.getKey();
			}
		}
	}

	/**
	 * this function use the server to move the robot on the game by get the robot
	 * id, and the next node for the robot and make a move to this node. the
	 * function let to move jest 1-2 nodes by one click.
	 * 
	 * @param id      the robot id
	 * @param nextKey the next node to move the robot
	 */

	public void manualMove(int id, int nextKey) {

		this.game.move();
		List<node_data> path = this.ga.shortestPath(posR, nextKey);
		posR = nextKey;
		if (path != null) {
			for (node_data n : path) {
				game.chooseNextEdge(id, n.getKey());
			}
		}
		game.chooseNextEdge(id, nextKey);
	}

	/**
	 * this function draw the robot from the start of the game and draw every move
	 * on the edges by using the server to get the current position of the robots.
	 * draw with StdDraw. in addition, the function send the position of the robot
	 * to the class "KML_loger" to document the moves of the robot.
	 */
	public void drawRobot() {
		List<String> log = game.getRobots();
		if (log != null) {
			for (int i = 0; i < log.size(); i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					String pos = ttt.getString("pos");
					Point3D p = new Point3D(pos);
					StdDraw.picture(p.x(), p.y(), "data/robot.PNG", 0.002, 0.001);
					if (p != null) {
						KML_Loger.createPlacemark(p.x(), p.y(), "ski", scenario%2 == 0 , game.timeToEnd()); //if scenario even - 30 sec
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * this function draw the fruits from the start of the game and draw every
	 * change on the graph by using the server to get the current position of the
	 * fruits. draw with StdDraw. in addition, the function send the position of the
	 * fruits to the class "KML_loger". to document the position and the eating the
	 * fruits by the robots.
	 */
	private void drawFruits() {
		MyGameGUI.fruits.clear();
		Iterator<String> fruits = game.getFruits().iterator();
		while (fruits.hasNext()) {
			fruit fr = new fruit(fruits.next());
			MyGameGUI.fruits.add(fr); // add to fruit list
			StdDraw.picture(fr.getLocation().x(), fr.getLocation().y(), fr.getImage(), 0.001, 0.0007);
			if (fr != null) {
				if (fr.getType() == -1) {
					KML_Loger.createPlacemark(fr.getLocation().x(), fr.getLocation().y(), "banana",scenario%2 == 0 , game.timeToEnd() );
				} else {
					KML_Loger.createPlacemark(fr.getLocation().x(), fr.getLocation().y(), "apple", scenario%2 == 0 , game.timeToEnd());
				}
			}
		}
	}

	/**
	 * this function start a automatic game if the user choose the automatic option.
	 * the function get the best node to the robot (near the edge with a fruit with
	 * the best value), send this position to the server and show the robot place in
	 * the window game by using stdDraw. after-start the game and the thread.
	 */
	public void start() {
		if (fruits.isEmpty()) {
			Iterator<String> f_iter = game.getFruits().iterator();
			while (f_iter.hasNext()) {
				fruit f = new fruit(f_iter.next().toString());
				fruits.add(f);
			}
		}
		node_data n = this.graph.getNode(0);
		for (int a = 0; a < this.numOfRobots; a++) { // put the robot in start position
			n = this.graph.getNode(findFirstPos().getSrc());
			this.game.addRobot(n.getKey());
			StdDraw.picture(n.getLocation().x(), n.getLocation().y(), "data/robot.PNG", 0.002, 0.001);
		}

		if (this.game.isRunning()) {
			StdDraw.clear();
			this.game.stopGame();
		}
		this.game.startGame();
		Thread t = new Thread(this);
		t.start();
	}

	/**
	 * this function find the edge with a fruit with the best value. assigned to
	 * place the robot at the beginning of a automatic game.
	 * 
	 * @return the edge with a fruit with the best value.
	 */
	private edge_data findFirstPos() { // find an edge with fruit to put the robot in the node
		double maxVal = 0;
		fruit maxF = null;
		int i;
		int index = 0;
		edge_data e = this.graph.getEdge(0, 1);
//		if( this.numOfRobots > 1) {
//			
//		}
		for (i = 0; i < MyGameGUI.fruits.size(); i++) { // find the fruit with the best value
			if (MyGameGUI.fruits.get(i).getValue() > maxVal) {
				maxVal = MyGameGUI.fruits.get(i).getValue();
				maxF = MyGameGUI.fruits.get(i);
				index = i;
			}
		}
		e = autoGame.findEdgeFruit(this.graph, maxF);
		fruits.remove(index);
		return e;
	}

	int jj = 0;

	public void run() {
		long first = System.currentTimeMillis();
		while (game.isRunning()) {
			StdDraw.enableDoubleBuffering();
			this.gui.initGUI();
			if (System.currentTimeMillis() - first >= 1000) {
				showTime();
			}
			showScore();
			if (!this.auto) {
				if (StdDraw.isMousePressed()) {
					checkClickR();
					checkClickN();
				}
				if (posR != -1 && nextNode != -1 && id != -1) {
					manualMove(id, nextNode);

				}
			} else { // auto game mode
				long dt = 100;
				insertFruits();
//				for (int j = 0; j < numOfRobots; j++) {
				this.game = autoGame.moveRobots(this.game, this.graph, fruits);
				try {
					List<String> stat = game.getRobots();
					for (int i = 0; i < stat.size(); i++) {
						System.out.println(jj + ") " + stat.get(i));
					}
					Thread.sleep(dt);
					jj++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// }
			drawRobot();
			drawFruits();
			StdDraw.show();

		}
		String results = this.game.toString();
		BufferedReader objReader = null;
		try {
			objReader = new BufferedReader(new FileReader("data/" + this.scenario + ".kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String remark = objReader.toString();
		game.sendKML(remark);
		System.out.println("Game Over: " + results);
	}

	private void showTime() {
		StdDraw.setPenColor(Color.WHITE);
		StdDraw.setPenRadius(0.02);
		StdDraw.text(this.gui.findRangeX().get_max(), this.gui.findRangeY().get_max() + 0.002,
				"time to end : " + this.game.timeToEnd() / 1000);
		StdDraw.setPenRadius();
	}

	/**
	 * the function show the score of the game on the screen by using the server to
	 * get the score. displaying the score on the window game by using stdDraw.
	 */
	private void showScore() {
		try {
			String info = game.toString();
			JSONObject line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int score = ttt.getInt("grade");
			StdDraw.text(this.gui.findRangeX().get_max(), this.gui.findRangeY().get_max() + 0.0015, "score : " + score);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void insertFruits() {
		MyGameGUI.fruits.clear();
		Iterator<String> f_iter = game.getFruits().iterator();
		while (f_iter.hasNext()) {
			fruit f = new fruit(f_iter.next().toString());
			fruits.add(f);
		}

	}

	/*
	 * this function to get the game.
	 */
	public game_service getGame() {
		return this.game;
	}

	/**
	 * this function to set the game.
	 * 
	 * @param game the gem to set.
	 */
	public void setGame(game_service game) {
		this.game = game;
	}

	public static void main(String[] args) {
		StdDraw.setCanvasSize(800, 480);
		StdDraw.picture(0.5, 0.5, "data/start game.JPG");

	}

}
