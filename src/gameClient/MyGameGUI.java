package gameClient;

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
import sun.net.sdp.SdpSupport;
import utils.Point3D;
import utils.StdDraw;

public class MyGameGUI implements Runnable {
	DGraph graph;
	public Graph_GUI gui;
	private Graph_Algo ga;
	List<fruit> fruits = new LinkedList<fruit>();
	public game_service game;
	public int senario;
	public int numOfRobots;
	public boolean auto;

	public MyGameGUI() {
		openWindow();
		this.game = Game_Server.getServer(this.senario); // you have [0,23] games
		StdDraw.game = this;
		Thread t = new Thread(gui);
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

	public game_service getGame() {
		return this.game;
	}

	public void setGame(game_service game) {
		this.game = game;
	}

	public void openWindow() {
		JFrame level = new JFrame();
		String scenario_num = JOptionPane.showInputDialog(level, "insert a level 0 - 23:");
		this.senario = Integer.parseInt(scenario_num);
	}

	public void placeRobot() {
		Thread t = new Thread(this);
		for (int i = 0; i < this.numOfRobots; i++) { // ask the user to insert start positions for each robot
			JFrame start = new JFrame();
			String node = JOptionPane.showInputDialog(start,
					"you have " + this.numOfRobots + " robots , choose start position to the robot number " + (i + 1)
							+ "\n (bettween 0 - " + (this.graph.nodeSize() - 1) + ")");

			game.addRobot(Integer.parseInt(node));
			node_data n = this.graph.getNode(Integer.parseInt(node));
			StdDraw.picture(n.getLocation().x(), n.getLocation().y(), "robot.jpg", 0.002, 0.001);
			StdDraw.show();
		}
		if(this.game.isRunning()) {
			StdDraw.clear();
			this.game.stopGame();
		}
		t.start();
		this.game.startGame();
	}

	int id = -1;
	int nextNode = -1;
	int posR = -1;

	public void manualMove(int id, int nextKey) {
		this.game.move();
		List<node_data> path = this.ga.shortestPath(posR, nextKey);
		posR = nextKey;
		if (path != null) {
			for (node_data n : path) {
				System.out.println("key of n path " + n.getKey());
				int dest = n.getKey();
				game.chooseNextEdge(id, dest);
			}
		}
		game.chooseNextEdge(id, nextKey);
	}

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
						// System.out.println("id " + id);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void checkClickN() {
		for (node_data n : this.graph.getV()) {
			if (StdDraw.mouseX() > n.getLocation().x() - 0.0003 && StdDraw.mouseX() < n.getLocation().x() + 0.0003
					&& StdDraw.mouseY() > n.getLocation().y() - 0.0003
					&& StdDraw.mouseY() < n.getLocation().y() + 0.0003 && id != -1) {
				nextNode = n.getKey();
				System.out.println("key + id  " + nextNode + "   " + id);
				;
			}
		}
	}

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
					StdDraw.picture(p.x(), p.y(), "robot.jpg", 0.002, 0.001);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void drawFruits() {
		this.fruits.clear();
		Iterator<String> fruits = game.getFruits().iterator();
		while (fruits.hasNext()) {
			fruit fr = new fruit(fruits.next());
			this.fruits.add(fr); // add to fruit list
			StdDraw.picture(fr.getLocation().x(), fr.getLocation().y(), fr.getImage(), 0.001, 0.001);
		}
	}

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
	
	public void start() {
		node_data n = this.graph.getNode(0);
		for (int a = 0; a < this.numOfRobots; a++) { // put the robot in start position
			n = this.graph.getNode(findFirstPos().getSrc());
			this.game.addRobot(n.getKey());
			StdDraw.picture(n.getLocation().x(), n.getLocation().y(), "robot.jpg", 0.002, 0.001);
		}
		
		
		if(this.game.isRunning()) {
			StdDraw.clear();
			this.game.stopGame();
		}
		this.game.startGame();
		Thread t = new Thread(this);
		t.start();
		String results = this.game.toString();
		System.out.println("Game Over: " + results);
	}
	
	private edge_data findFirstPos() { // find an edge with fruit to put the robot in the node
		double maxVal = 0;
		fruit maxF = null;
		int i;
		int index = 0;
		edge_data e = this.graph.getEdge(0, 1);
		for (i = 0; i < this.fruits.size(); i++) { // find the fruit with the best value
			if (this.fruits.get(i).getValue() > maxVal) {
				maxVal = this.fruits.get(i).getValue();
				maxF = this.fruits.get(i);
				index = i;
			}
		}
		e = autoGame.findEdgeFruit(this.graph, maxF);
		fruits.remove(index);
		return e;
	}
	


	public void run() {
		long first = System.currentTimeMillis();
		while (game.isRunning()) {
			StdDraw.enableDoubleBuffering();
			if(!this.auto){
				if (StdDraw.isMousePressed()) {
					checkClickR();
					checkClickN();
				}
				if (posR != -1 && nextNode != -1 && id != -1) {
					manualMove(id, nextNode);
				}
			}
			else { //auto game mode 
				for (int j = 0; j < this.game.getRobots().size(); j++) {
					autoGame.moveRobots(this.game, this.graph,fruits, j);

				}
				
			}

			this.gui.initGUI();
			if (System.currentTimeMillis() - first >= 1000) {
				StdDraw.setPenColor();
				StdDraw.setPenRadius(0.02);
				StdDraw.text(this.gui.findRangeX().get_max(), this.gui.findRangeY().get_max() + 0.002,
						"time to end : " + this.game.timeToEnd() / 1000);
				StdDraw.setPenRadius();
			}
			showScore();
			for (int j = 0; j < this.game.getRobots().size(); j++) {
				drawRobot();
				drawFruits();
			}
			StdDraw.show();

		}
	}

	public static void main(String[] args) {
		StdDraw.setCanvasSize(800, 600);
		// MyGameGUI myGame = new MyGameGUI();

	}

}
