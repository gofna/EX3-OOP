package gui;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Range;
import utils.StdDraw;

/**
 * this class display a graph on a window. to show the graph optimally, the
 * class make all the adjustments such as: - change the scales window according
 * to the vertices value and edges on the graph. -put an image in size that
 * fits the window.
 * 
 * @author Maor Ovadia and Gofna Ivry.
 *
 */
public class Graph_GUI implements Runnable {

	private graph g = new DGraph();
	private int count;
	private static Range Rx = new Range(0, 0);
	private static Range Ry = new Range(0, 0);

	/**
	 * constructor function , to initialize the graph to draw on the GUI window and
	 * to choose the size of the window.
	 * 
	 * @param g the graph to draw.
	 */

	public Graph_GUI(graph g) {
		this.g = g;
		StdDraw.gui = this;
		StdDraw.setCanvasSize(750, 500);
		StdDraw.enableDoubleBuffering();
		initGUI();
		this.count = this.g.getMC();
		Thread t1 = new Thread(this);
		t1.start();

	}

	/**
	 * this function draw the graph on the window using StdDrow , drawing the nodes,
	 * the nodes number, the edges and the directions of the edges(by green points).
	 */
	public void initGUI() {
		StdDraw.clear();

		Rx = findRangeX();
		Ry = findRangeY();
		StdDraw.setXscale(Rx.get_min() - 0.003, Rx.get_max() + 0.003);
		StdDraw.setYscale(Ry.get_min() - 0.003, Ry.get_max() + 0.003);
		StdDraw.picture(Rx.get_min() + 0.012, Ry.get_min() + 0.003, "data/game background.PNG");
		if (this.g != null) {
			for (node_data n : this.g.getV()) {
				StdDraw.setPenColor(Color.BLUE);
				StdDraw.filledCircle(n.getLocation().x(), n.getLocation().y(), 0.00018); // draw circle blue
				StdDraw.text(n.getLocation().x() + 0.0003, n.getLocation().y() + 0.0003, String.valueOf(n.getKey()));
				if (this.g.getE(n.getKey()) != null) {
					for (edge_data e : this.g.getE(n.getKey())) {
						StdDraw.setPenColor(Color.RED);
						StdDraw.line(n.getLocation().x(), n.getLocation().y(), g.getNode(e.getDest()).getLocation().x(),
								g.getNode(e.getDest()).getLocation().y());

						StdDraw.setPenColor(Color.BLACK);
						double x = (n.getLocation().x() + g.getNode(e.getDest()).getLocation().x()) / 2;
						double y = (n.getLocation().y() + g.getNode(e.getDest()).getLocation().y()) / 2;
						// StdDraw.text(x + 0.0005, y + 0.0005, String.valueOf(e.getWeight()));

						StdDraw.setPenRadius();
						StdDraw.setPenColor(Color.GREEN);
						double x1 = 0.1 * n.getLocation().x() + 0.9 * g.getNode(e.getDest()).getLocation().x();
						double y1 = 0.1 * n.getLocation().y() + 0.9 * g.getNode(e.getDest()).getLocation().y();
						StdDraw.filledCircle(x1, y1, 0.0001);
						StdDraw.setPenColor(Color.BLACK);
					}
				}
			}
		}

	}

	public void run() {
		try {
			while (true) {
				if (this.count != this.g.getMC()) {
					initGUI();

					this.count = this.g.getMC();
				}
			}
		} catch (Exception e) {
		}

	}

	/**
	 * this function find the range of the X scale, by finding the minimum x position of node, 
	 * and maximum x position of node
	 * @return the range of the nodes.
	 */
	public Range findRangeX() {
		if (g.nodeSize() != 0) {
			double min = Integer.MAX_VALUE;
			double max = Integer.MIN_VALUE;
			Collection<node_data> V = g.getV();
			for (node_data node : V) {
				if (node.getLocation().x() > max)
					max = node.getLocation().x();
				if (node.getLocation().x() < min)
					min = node.getLocation().x();
			}
			Range ans = new Range(min, max);
			Rx = ans;
			return ans;
		} else {
			Range Default = new Range(-100, 100);
			Rx = Default;
			return Default;
		}
	}

	/**
	 * this function find the range of the y scale, by finding the minimum y position of node, 
	 * and maximum y position of node
	 * @return the range of the nodes.
	 */
	public Range findRangeY() {
		if (g.nodeSize() != 0) {
			double min = Integer.MAX_VALUE;
			double max = Integer.MIN_VALUE;
			Collection<node_data> V = g.getV();
			for (node_data node : V) {
				if (node.getLocation().y() > max)
					max = node.getLocation().y();
				if (node.getLocation().y() < min)
					min = node.getLocation().y();
			}
			Range ans = new Range(min, max);
			Ry = ans;
			return ans;
		} else {
			return new Range(-100, 100);
		}
	}

}
