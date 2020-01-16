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
		gui.auto = true;
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
			Iterator<String> f_iter = gui.game.getFruits().iterator();
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


	



	





}
