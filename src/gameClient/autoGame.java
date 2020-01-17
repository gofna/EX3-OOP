package gameClient;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import elements.fruit;

public class autoGame {
	public static game_service game;
	private static DGraph graph;
	private static Graph_Algo ga;

	public static void moveRobots(game_service game, DGraph gg, List<fruit> fruits, int ind) {
		autoGame.game = game;
		graph = new DGraph();
		graph.init(game.getGraph());
		autoGame.ga = new Graph_Algo(graph);
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
						edge_data e = nextEdge(src, fruits);
						List<node_data> nodes = ga.shortestPath(src, e.getSrc());
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

	private static edge_data nextEdge(int robotPos, List<fruit> fruits) { // give the edge with the fruit with the
																			// shortest path
		if (fruits.isEmpty()) {
			Iterator<String> f_iter = game.getFruits().iterator();
			while (f_iter.hasNext()) {
				fruit f = new fruit(f_iter.next().toString());
				fruits.add(f);
			}
		}
		double minPath = Double.POSITIVE_INFINITY;
		int bestSrc = robotPos;
		int bestDest = robotPos;
		for (int i = 0; i < fruits.size(); i++) {
			edge_data e = findEdgeFruit(graph, fruits.get(i));
			fruits.remove(i);
			if (ga.shortestPathDist(robotPos, e.getSrc()) < minPath) {
				minPath = ga.shortestPathDist(robotPos, e.getSrc());
				bestSrc = e.getSrc();
				bestDest = e.getDest();
			}
		}
		return graph.getEdge(bestSrc, bestDest);
	}

	public static edge_data findEdgeFruit(DGraph graph, fruit fr) {
		int src = 0;
		int dest = 0;

		for (node_data n : graph.getV()) {
			for (edge_data e : graph.getE(n.getKey())) {
				double dFruit = (Math.sqrt(Math.pow(n.getLocation().x() - fr.getLocation().x(), 2)
						+ Math.pow(n.getLocation().y() - fr.getLocation().y(), 2)))
						+ Math.sqrt(Math.pow(graph.getNode(e.getDest()).getLocation().x() - fr.getLocation().x(), 2)
								+ Math.pow(graph.getNode(e.getDest()).getLocation().y() - fr.getLocation().y(), 2));
				double dNodes = (Math
						.sqrt(Math.pow(n.getLocation().x() - graph.getNode(e.getDest()).getLocation().x(), 2)
								+ Math.pow(n.getLocation().y() - graph.getNode(e.getDest()).getLocation().y(), 2)));
				double highNode = graph.getNode(e.getSrc()).getLocation().y()
						- graph.getNode(e.getDest()).getLocation().y();
				if (Math.abs(dNodes - dFruit) < 0.000001) {
					if (fr.getType() == -1) { // if its banana
						if (highNode < 1) {
							src = graph.getNode(e.getSrc()).getKey();
							dest = graph.getNode(e.getDest()).getKey();
						} else {
							src = graph.getNode(e.getDest()).getKey();
							dest = graph.getNode(e.getSrc()).getKey();
						}
					} else { // if its apple
						if (highNode > 1) {
							src = graph.getNode(e.getSrc()).getKey();
							dest = graph.getNode(e.getDest()).getKey();
						} else {
							src = graph.getNode(e.getDest()).getKey();
							dest = graph.getNode(e.getSrc()).getKey();

						}
					}
				}
			}
		}
		return graph.getEdge(src, dest);
	}

}
