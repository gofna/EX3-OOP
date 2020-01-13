package gameClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Fruit;
import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Edge;
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
	private List<Integer> robots = new LinkedList<Integer>();
	private game_service game;
	private int senario;

	public MyGameGUI() {

		JFrame level = new JFrame();
		String scenario_num = JOptionPane.showInputDialog(level, "insert a level 0 - 23:");

		this.senario = Integer.parseInt(scenario_num);
		JFrame mode = new JFrame();
		JButton manual = new JButton("manual");
		JButton automatic = new JButton("automatic");
		mode.add(manual);
		mode.add(automatic);

		manual.addActionListener(this);
		automatic.addActionListener(this);
		start(this.senario);

	}

	public void start(int scenario_num) {
		Thread t = new Thread(this);
		this.game = Game_Server.getServer(scenario_num); // you have [0,23] games
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
			int numOfRobots = ttt.getInt("robots");
			System.out.println(info);
			System.out.println(g);
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while (f_iter.hasNext()) {
				fruit fr = new fruit(f_iter.next().toString());
				this.fruits.add(fr);
			}
			node_data n = this.graph.getNode(0);
			for (int a = 0; a < numOfRobots; a++) { // put the robot in start position
				n = findFruitNode();
				game.addRobot(n.getKey());
				StdDraw.picture(n.getLocation().x(), n.getLocation().y(), "robot.jpg", 0.002, 0.001);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		game.startGame();
		t.start();

		String results = game.toString();
		System.out.println("Game Over: " + results);
	}

	private node_data findFruitNode() { // find an edge with fruit to put the robot in the node
		double maxVal = 0;
		fruit maxF = null;
		for (int i = 0; i < this.fruits.size(); i++) {
			if (this.fruits.get(i).getValue() > maxVal) {
				maxVal = this.fruits.get(i).getValue();
				maxF = this.fruits.get(i);
				this.fruits.remove(i);
			}
		}
		System.out.println("best : \n" + maxVal);
		for (node_data n : this.graph.getV()) {
			for (edge_data e : this.graph.getE(n.getKey())) {
				double dFruit = (Math.sqrt(Math.pow(n.getLocation().x() - maxF.getLocation().x(), 2)
						+ Math.pow(n.getLocation().y() - maxF.getLocation().y(), 2)))
						+ Math.sqrt(Math.pow(this.graph.getNode(e.getDest()).getLocation().x() - maxF.getLocation().x(),
								2)
								+ Math.pow(this.graph.getNode(e.getDest()).getLocation().y() - maxF.getLocation().y(),
										2));
				double dNodes = (Math.sqrt(
						Math.pow(n.getLocation().x() - this.graph.getNode(e.getDest()).getLocation().x(), 2) + Math
								.pow(n.getLocation().y() - this.graph.getNode(e.getDest()).getLocation().y(), 2)));
				double highNode = this.graph.getNode(e.getSrc()).getLocation().y()
						- this.graph.getNode(e.getDest()).getLocation().y();
				if (Math.abs(dNodes - dFruit) < 0.0001) {
					if (maxF.getType() == -1) { // if its banana
						if (highNode < 1)
							return this.graph.getNode(e.getSrc());
						else
							return this.graph.getNode(e.getDest());
					} else { // if its apple
						if (highNode > 1)
							return this.graph.getNode(e.getSrc());
						else
							return this.graph.getNode(e.getDest());
					}
				}

			}
		}
		return this.graph.getNode(0);
	}

	/**
	 * Moves each of the robots along the edge, in case the robot is on a node the
	 * next destination (next edge) is chosen (randomly).
	 * 
	 * @param game
	 * @param gg
	 */
	private void moveRobots(game_service game, DGraph gg) {
		List<String> log = game.move();
		if (log != null) {
			long t = game.timeToEnd();
			for (int i = 0; i < log.size(); i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");

					if (dest == -1) {
						edge_data e = nextEdge(src);
						System.out.println("src ed" + e.getSrc() + "dest ed" + e.getDest());
						List<node_data> nodes = this.ga.shortestPath(src, e.getSrc());
						System.out.println("aaaaaaaaaaaaaaaaaaaa" + nodes.size() );
						for (node_data n : nodes) {
							dest = n.getKey();
							
							game.chooseNextEdge(rid, dest);
						}
						// dest = nextNode(gg, src);

						game.chooseNextEdge(rid, e.getDest());
						System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
						System.out.println(ttt);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private edge_data nextEdge(int robotPos) {
		double minPath = Double.POSITIVE_INFINITY;
		int src = 0;
		int dest = 0;
		int bestSrc = 0;
		int bestDest = 0;
		for (fruit fr : this.fruits) {
			for (node_data n : this.graph.getV()) {
				for (edge_data e : this.graph.getE(n.getKey())) {
					double dFruit = (Math.sqrt(Math.pow(n.getLocation().x() - fr.getLocation().x(), 2)
							+ Math.pow(n.getLocation().y() - fr.getLocation().y(), 2)))
							+ Math.sqrt(Math
									.pow(this.graph.getNode(e.getDest()).getLocation().x() - fr.getLocation().x(), 2)
									+ Math.pow(this.graph.getNode(e.getDest()).getLocation().y() - fr.getLocation().y(),
											2));
					double dNodes = (Math.sqrt(
							Math.pow(n.getLocation().x() - this.graph.getNode(e.getDest()).getLocation().x(), 2) + Math
									.pow(n.getLocation().y() - this.graph.getNode(e.getDest()).getLocation().y(), 2)));
					double highNode = this.graph.getNode(e.getSrc()).getLocation().y()
							- this.graph.getNode(e.getDest()).getLocation().y();
					if (Math.abs(dNodes - dFruit) < 0.0001) {
						System.out.println("src is   " + bestSrc );
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
						if (this.ga.shortestPathDist(robotPos, src) < minPath) {
							minPath = this.ga.shortestPathDist(robotPos, src);
							bestSrc = src;
							bestDest = dest;
						}
					}

				}
			}

		}
		return this.graph.getEdge(bestSrc, bestDest);
	}

	/**
	 * a very simple random walk implementation!
	 * 
	 * @param g
	 * @param src
	 * @return the dest node
	 */
//	private int nextNode(DGraph g, int src) {
//		
//		int ans = -1;
//		Collection<edge_data> ee = g.getE(src);
//		Iterator<edge_data> itr = ee.iterator();
//		int s = ee.size();
//		int r = (int) (Math.random() * s);
//		int i = 0;
//		while (i < r) {
//			itr.next();
//			i++;
//		}
//		ans = itr.next().getDest();
//		return ans;
//		return 0;
//	}

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
			this.fruits.clear();
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
			moveRobots(this.game, this.graph);
			drawFruits();
			drawRobot();
			StdDraw.show();

		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == "manual") {

		}
		if (e.getSource() == "automatic") {
			start(this.senario);
		}

	}

	public static void main(String[] args) {
		MyGameGUI myGame = new MyGameGUI();

	}

}
