package gameClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

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

public class MyGameGUI implements Runnable, ActionListener {
	private DGraph graph;
	private Graph_GUI gui;
	private Graph_Algo ga;
	private List<fruit> fruits = new LinkedList<fruit>();
	private game_service game;
	private int senario;
	private int numOfRobots;
//	public static JRadioButton manual , automatic;
//	public static boolean mode ;

	public MyGameGUI() {
		openWindow();
		this.game = Game_Server.getServer(this.senario); // you have [0,23] games
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
		// Iterator<String> f_iter = game.getFruits().iterator();
		// while (f_iter.hasNext()) { // add fruit from server to fruit list
		// fruit fr = new fruit(f_iter.next().toString());
		// this.fruits.add(fr);
		// }
		this.gui.initGUI();
		drawFruits();
		StdDraw.show();
		start();

	}
	
	private void openWindow() {
		JFrame level = new JFrame();
		String scenario_num = JOptionPane.showInputDialog(level, "insert a level 0 - 23:");
		this.senario = Integer.parseInt(scenario_num);
		
//		JFrame mode = new JFrame();
//		mode.setTitle("choose a mode");
//		mode.setSize(500, 200);
//		mode.setLocation(450,300);
//		JPanel panel = new JPanel();
//		JButton choose = new JButton("CHOOSE");
//		manual = new JRadioButton("manual");
//		automatic = new JRadioButton("automatic");
//		
//		panel.add(manual);
//		panel.add(automatic);
//		panel.add(choose);
//		
//		mode.setContentPane(panel);
//		mode.setVisible(true);
//		
//		choose.addActionListener(this);
//
//		
	}

	public void start() {
		Thread t = new Thread(this);
		node_data n = this.graph.getNode(0);
		for (int a = 0; a < this.numOfRobots; a++) { // put the robot in start position
			n = this.graph.getNode(findFirstPos().getSrc());
			game.addRobot(n.getKey());
			StdDraw.picture(n.getLocation().x(), n.getLocation().y(), "robot.jpg", 0.002, 0.001);
		}

		game.startGame();
		t.start();

		String results = game.toString();
		System.out.println("Game Over: " + results);
	}

	public void manualMove() {
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
		t.start();
		//this.game.startGame();
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
		e = findEdgeFruit(maxF, index);
		return e;
	}

	/**
	 * Moves each of the robots along the edge, in case the robot is on a node the
	 * next destination (next edge) is chosen (randomly).
	 * 
	 * @param game
	 * @param gg
	 */
	private void moveRobots(game_service game, DGraph gg, int ind) {
		List<String> log = game.move();
		if (log != null) {
			long t = game.timeToEnd();
			for (int i = 0; i < log.size(); i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");

					for (int rid = ind; rid < this.game.getRobots().size(); rid++) {
						if (dest == -1) {
							edge_data e = nextEdge(src);
							List<node_data> nodes = this.ga.shortestPath(src, e.getSrc());
							if (nodes == null) {
								dest = e.getDest();
								game.chooseNextEdge(rid, e.getDest());
							} else {
								for (node_data n : nodes) {
									dest = n.getKey();
									game.chooseNextEdge(rid, dest);
								}
								// dest = nextNode(gg, src);
								dest = e.getDest();
								game.chooseNextEdge(rid, e.getDest());
							}
							System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
							System.out.println(ttt);

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private edge_data nextEdge(int robotPos) { // give the edge with the fruit with the shortest path
		this.fruits.clear();
		Iterator<String> f_iter = game.getFruits().iterator();
		while (f_iter.hasNext()) {
			fruit f = new fruit(f_iter.next().toString());
			this.fruits.add(f);
		}
		double minPath = Double.POSITIVE_INFINITY;
		int bestSrc = robotPos;
		int bestDest = robotPos;
		for (int i = 0; i < this.fruits.size(); i++) {
			edge_data e = findEdgeFruit(this.fruits.get(i), i);
			if (this.ga.shortestPathDist(robotPos, e.getSrc()) < minPath) {
				minPath = this.ga.shortestPathDist(robotPos, e.getSrc());
				bestSrc = e.getSrc();
				bestDest = e.getDest();
			}
		}
		return this.graph.getEdge(bestSrc, bestDest);
	}

	public edge_data findEdgeFruit(fruit fr, int index) {
		int src = 0;
		int dest = 0;
		for (node_data n : this.graph.getV()) {
			for (edge_data e : this.graph.getE(n.getKey())) {
				double dFruit = (Math.sqrt(Math.pow(n.getLocation().x() - fr.getLocation().x(), 2)
						+ Math.pow(n.getLocation().y() - fr.getLocation().y(), 2)))
						+ Math.sqrt(Math.pow(this.graph.getNode(e.getDest()).getLocation().x() - fr.getLocation().x(),
								2)
								+ Math.pow(this.graph.getNode(e.getDest()).getLocation().y() - fr.getLocation().y(),
										2));
				double dNodes = (Math.sqrt(
						Math.pow(n.getLocation().x() - this.graph.getNode(e.getDest()).getLocation().x(), 2) + Math
								.pow(n.getLocation().y() - this.graph.getNode(e.getDest()).getLocation().y(), 2)));
				double highNode = this.graph.getNode(e.getSrc()).getLocation().y()
						- this.graph.getNode(e.getDest()).getLocation().y();
				if (Math.abs(dNodes - dFruit) < 0.0001) {
					if (fr.getType() == -1) { // if its banana
						if (highNode < 1) {
							src = this.graph.getNode(e.getSrc()).getKey();
							dest = this.graph.getNode(e.getDest()).getKey();
						} else {
							src = this.graph.getNode(e.getDest()).getKey();
							dest = this.graph.getNode(e.getSrc()).getKey();
						}
					} else { // if its apple
						if (highNode > 1) {
							src = this.graph.getNode(e.getSrc()).getKey();
							dest = this.graph.getNode(e.getDest()).getKey();
						} else {
							src = this.graph.getNode(e.getDest()).getKey();
							dest = this.graph.getNode(e.getSrc()).getKey();

						}
					}
				}
			}
		}
		this.fruits.remove(index);
		return this.graph.getEdge(src, dest);
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
			StdDraw.text(this.gui.findRangeX().get_max(), this.gui.findRangeY().get_max() + 0.004, "score : " + score);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		long first = System.currentTimeMillis();
		while (game.isRunning()) {
			this.gui.initGUI();
			if (System.currentTimeMillis() - first >= 1000) {
				StdDraw.setPenColor();
				StdDraw.setPenRadius(0.02);
				StdDraw.text(this.gui.findRangeX().get_max(), this.gui.findRangeY().get_max() + 0.005,
						"time to end : " + this.game.timeToEnd() / 1000);
				StdDraw.setPenRadius();
			}
			showScore();
			StdDraw.enableDoubleBuffering();
			for (int j = 0; j < this.game.getRobots().size(); j++) {
				drawRobot();
				moveRobots(this.game, this.graph, j);
				drawFruits();
			}
			StdDraw.show();

		}
	}

	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals("start manual game")) {
			this.game.stopGame();
			manualMove();

		}
		if (str.equals("start automatic game")) {
			this.game.stopGame();
			start();
		}
	}
	


	public static void main(String[] args) {
		MyGameGUI myGame = new MyGameGUI();

	}

}
