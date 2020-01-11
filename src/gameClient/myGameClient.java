package gameClient;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;

public class myGameClient {
	private DGraph graph;

	public void start(int scenario_num) {
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String g = game.getGraph();
		this.graph.init(g);
		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int numOfRobots = ttt.getInt("robots");
			System.out.println(info);
			System.out.println(g);
			// the list of fruits should be considered in your solution
			Iterator<String> fruits = game.getFruits().iterator();
			while (fruits.hasNext()) {
				System.out.println(fruits.next());
			}
			int src_node = 0; // arbitrary node, you should start at one of the fruits
			for (int a = 0; a < numOfRobots; a++) {
				game.addRobot(src_node + a);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		game.startGame();
		// should be a Thread!!!
		while (game.isRunning()) {
			moveRobots(game, this.graph);
		}
		String results = game.toString();
		System.out.println("Game Over: " + results);
	}
	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 */
	private static void moveRobots(game_service game, DGraph gg) {
		List<String> log = game.move();
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
				
					if(dest==-1) {	
						dest = nextNode(gg, src);
						game.chooseNextEdge(rid, dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return the dest node
	 */
	private static int nextNode(DGraph g, int src) {
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		Iterator<edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}
}
