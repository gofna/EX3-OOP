package gameClient;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import utils.StdDraw;

public class autoGame implements Runnable {
	private DGraph graph;
	private Graph_Algo ga;
	private List<fruit> fruits = new LinkedList<fruit>();
	private game_service game = Game_Server.getServer(0);;
	private int numOfRobots;
	//private int senario;
	private MyGameGUI gui;
	

	public autoGame() {
		MyGameGUI gui = new MyGameGUI();
		this.game = gui.game;
		String g = game.getGraph();
		this.graph = new DGraph();
		this.graph.init(g);
		this.ga = new Graph_Algo(this.graph);
		String info = game.toString();
		JSONObject line;

		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			this.numOfRobots = ttt.getInt("robots");
			System.out.println(info);
			System.out.println(g);
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				fruit fr = new fruit(f_iter.next());
				this.fruits.add(fr);
				//System.out.println(f_iter.next());
				}	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		start();
		
	}

	public void start() {
		node_data n = this.graph.getNode(0);
		for (int a = 0; a < this.numOfRobots; a++) { // put the robot in start position
			n = this.graph.getNode(findFirstPos().getSrc());
			this.game.addRobot(n.getKey());
			//StdDraw.picture(n.getLocation().x(), n.getLocation().y(), "robot.jpg", 0.002, 0.001);
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
		e = findEdgeFruit(maxF, index);
		return e;
	}

	public void moveRobots(game_service game, DGraph gg, int ind) {
		List<String> log = game.move();
		if (log != null) {
			long time = game.timeToEnd();
			for (int i = 0; i < log.size(); i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
						if (dest == -1) {
							edge_data e = nextEdge(src);
							List<node_data> nodes = this.ga.shortestPath(src, e.getSrc());
							if (nodes == null) {
								dest = e.getDest();
								game.chooseNextEdge(ind, e.getDest());
							} else {
								for (node_data n : nodes) {
									dest = n.getKey();
									game.chooseNextEdge(ind, dest);
								}
								// dest = nextNode(gg, src);
								dest = e.getDest();
								game.chooseNextEdge(ind, e.getDest());
							}
							System.out.println("Turn to node: " + dest + "  time to end:" + (time / 1000));
							System.out.println(ttt);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private edge_data nextEdge(int robotPos) { // give the edge with the fruit with the shortest path
		this.fruits.clear();
		Iterator<String> f_iter = this.game.getFruits().iterator();
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

	public void run() {
		while (game.isRunning()) {
			for (int j = 0; j < this.game.getRobots().size(); j++) {
				this.moveRobots(game, this.graph, j);
			}
		}
	}
	
	public static void main(String[] args) {
		//autoGame auto = new autoGame(0);
	}

}
