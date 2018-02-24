/*
 * Ant.java
 * 
 * This class is represents the ant in the game.  It does the pathfinding from the start to the goal.  Pathfinding is done
 * using a separate thread so that it can be slowed down for display.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 8, 2016
 * 
 */

package part1;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class Ant extends Sprite implements Runnable {
	public class NodeRecord {
		public int node;
		public int cost;
		public Connection cnx;
		public int costSoFar;
		public int estimatedTotalCost;
	}
	
	private Sprite openImage, closedImage;
	private int scaleFactor;
	private Connection[] pathFound;
	private Thread pathFinding;
	private String[] pathString;
	
	private NodeRecord startRecord;
	private NodeRecord current;
	private Connection[] connections;
	private ArrayList<NodeRecord> open;
	private ArrayList<NodeRecord> closed;
	private Graph graph;
	private int start, end;
	private Heuristic heuristic;
	private boolean isRunning;
	
	public Ant(int scaleFactor, Point start) {
		super("images/ant.png");
		this.scaleFactor = scaleFactor;
		isRunning = false;
		setScaleByPx(scaleFactor);
		setPosition(start);
			System.out.println("new ant at " + start.x + "," + start.y + " scale: " + scaleFactor);
		
		openImage = new Sprite("images/openNode.png");
		closedImage = new Sprite("images/closedNode.png");
		openImage.setScaleByPx(scaleFactor);
		closedImage.setScaleByPx(scaleFactor);
		
		open = new ArrayList<NodeRecord>();
		closed = new ArrayList<NodeRecord>();
	} // end of constructor
	
	// draws the ant
	public void draw(Graphics2D g2d) {
		drawImage(g2d);
	} // end of draw(Graphic2D
	
	// returns the current list of open nodes as a simple array
	public int[] getOpenList() {
		int[] ol = new int[open.size()];
		for (int i = 0; i < open.size(); i++) {
			ol[i] = open.get(i).node;
		}
		return ol;
	} // end getOpenList()
	
	// returns the current list of closed nodes as simple array
	public int[] getClosedList() {
		int[] cl = new int[closed.size()];
		for (int i = 0; i < closed.size(); i++) {
			cl[i] = closed.get(i).node;
		}
		return cl;
	} // end of getClosedList()
	
	// returns the state of the pathfinding process
	public boolean isRunning() { return isRunning; }
	
	// returns a string of connections for the path found
	public String[] getPathString() { return pathString; }
	
	// runs the pathfinding algorithm as described in the textbook
	public void run() {
		isRunning = true;
		
		// iterate through processing each node
		while (open.size() > 0) {
			
			// find smallest element in the open list by estimate total cost
			current = getSmallest(open);
				System.out.println("\n   open list = " + open.size() + " nodes,  closed list = " + closed.size() + " current: " + current.node);
			// terminate if goal node
			if (current.node == end) {
				break;
			}
			
			// otherwise get outgoing connections
			connections = graph.getConnections(current.node);
			
			// loop through each connection
			int endNode, endNodeCost, endNodeHeuristic;
			NodeRecord endNodeRecord;
				System.out.println("   CNX CHECK ON " + connections.length + " CNX");
			for (Connection cnx : connections) {
				// get cost estimate for end node
					System.out.println("      NodeID: " + cnx.id + "  from: " + cnx.fromNode + "  to: " + cnx.toNode + "  cost: " + cnx.cost);
				endNode = cnx.getToNode();
				endNodeCost = current.costSoFar + cnx.getCost();
				
				// if node close maybe skip or remove from closed list
				if (listContains(closed, endNode)) {
						System.out.println("         closed contains end node");
					// find record in closed list corresponding to endNode
					//endNodeRecord = closed.find(endNode);
					endNodeRecord = findNode(closed, endNode);
					
					// skip if no shorter route
					if (endNodeRecord.costSoFar <= endNodeCost) {
							System.out.println("         NO SHORTER ROUTE!");
						continue;
					}
					
					// otherwise remove from closed list
					closed.remove(endNodeRecord);
					
					// use node's old cost values to calc heuristic w/o calling expensive heuristic func
					endNodeHeuristic = endNodeRecord.cost - endNodeRecord.costSoFar;
				}
				
				// skip if node is open and not found better route
				else if (listContains(open, endNode)) {
						System.out.println("    open contains end node");
					// find record in open list corresponding to endNode
					endNodeRecord = findNode(open, endNode);//open.find(endNode);
					
					// skip if route is not better
					if (endNodeRecord.costSoFar <= endNodeCost) {
						continue;
					}
					
					// use node's old cost values to calc heuristic w/o calling expensive heuristic func
					endNodeHeuristic = endNodeRecord.cost - endNodeRecord.costSoFar;
				}
					
				// otherwise we have unvisited node, so need to make record
				else {
						System.out.println("      unvisited node needs new record");
					endNodeRecord = new NodeRecord();
					endNodeRecord.node = endNode;
					
					// need to calc heuristic value with function b/c no existing node record
					endNodeHeuristic = heuristic.estimate(endNode);
				}
				
				// here if need to update node
				// update cost/estimate/connection
				endNodeRecord.cost = endNodeCost;
				endNodeRecord.cnx = cnx;
				endNodeRecord.estimatedTotalCost = endNodeCost + endNodeHeuristic;
				
				// add to open list
				if (!listContains(open, endNode)) {
					open.add(endNodeRecord);
				}
			}
			
			// finished w/connections for current node, so add to closed list and remove from open list
			open.remove(current);
			closed.add(current);
	
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// here if found goal or no more nodes to search... which one?
		if (current.node != end) {
			// run out of nodes w/o finding goal == no solution
			System.out.println("Could Not Find Pathfinding Solution");
			pathFound = null;
			pathString = new String[1];
			pathString[0] = "NO PATH AVAILABLE!";
		} else {
			// compile list of connections in path
			ArrayList<Connection> path = new ArrayList<Connection>();
				System.out.println("   Compiling cnx list...");
			// work back along path, accumulating connections
			while (current.node != start) {
				path.add(current.cnx);
				current = findNode(open, closed, current.node, current.cnx.getFromNode());
			}
			
			// reverse path and return
			pathFound = reverse(path);
		}
		isRunning = false;
	} // end of run()
	
	// begins the process of pathfinding using the A* algorithm outlined in the textbook
	public void pathfindAStar(Graph graph, int start, int end, Heuristic heuristic) {
		pathFinding = new Thread(this);
		this.graph = graph;
		this.start = start;
		this.end = end;
		this.heuristic = heuristic;

		// initialize the record for the start node
		startRecord = new NodeRecord();
		current = new NodeRecord();
		startRecord.node = start;
		startRecord.cnx = null;
		startRecord.cost = 0;
		startRecord.costSoFar = 0;
		startRecord.estimatedTotalCost = heuristic.estimate(start);
		
		// initialize the open and closed lists
		open = new ArrayList<NodeRecord>();
		closed = new ArrayList<NodeRecord>();
		open.add(startRecord);
		
		pathFinding.start();
	} // end of pathfindAStar(Graph, int, int, Heuristic)
	
	// finds the smallest node in the passed record
	private NodeRecord getSmallest(ArrayList<NodeRecord> openList) {
		NodeRecord smallest = openList.get(0);
		for (Iterator<NodeRecord> i = openList.iterator(); i.hasNext();) {
			NodeRecord nr = i.next();
			if (nr.estimatedTotalCost < smallest.estimatedTotalCost) {
				smallest = nr;
			}			
		}
		return smallest;
	} // end of getSmallest(ArrayList)
	
	// reverses the passed ArrayList returning a simple array
	private Connection[] reverse(ArrayList<Connection> list) {
		Connection[] reverseList = new Connection[list.size()];

		for (int i = 0; i < list.size(); i++) {
			reverseList[list.size() - i - 1] = list.get(i);
		}

		// create string for found path
		int totalCost = 0;
		pathString = new String[reverseList.length + 4];
		pathString[0] = "PATH FOUND!";
		pathString[1] = " ";
		for (int i = 0; i < reverseList.length; i++) {
			pathString[i + 2] = ("CNX " + i + ": " + reverseList[i].fromNode + " to " + reverseList[i].toNode + "  cost: " + reverseList[i].cost + "\n");
			totalCost += reverseList[i].cost;
		}

		pathString[pathString.length - 2] = " ";
		pathString[pathString.length - 1] = ("TOTAL COST: " + totalCost);
		return reverseList;
	} // end of reverse(ArrayList)
	
	// finds a given node in the passed ArrayList
	private NodeRecord findNode(ArrayList<NodeRecord> list, int node) {
		for (NodeRecord	nr : list) {
			if (nr.node == node) {
				return nr;
			}
		}
		System.out.println("No Matching Node Found");
		return null;
	} // end of findNode(ArrayList, int)
	
	// finds a given node in either the open/closed list passed
	private NodeRecord findNode(ArrayList<NodeRecord> open, ArrayList<NodeRecord> closed, int toNode, int currentNode) {
		// check closed list
		for (NodeRecord nr : closed) {
			if (nr.node == currentNode) {
				return nr;
			}
		}
		// check open list
		for (NodeRecord nr : open) {
			if (nr.node == currentNode) {
				return nr;
			}
		}
		System.out.println("No Matching NodeRecord Found");
		return null;
	} // end of findNode(ArrayList, ArrayList, int, int)
	
	// determines if the list contains the passed node
	private boolean listContains(ArrayList<NodeRecord> list, int node) {
		for (NodeRecord nr : list) {
			if (nr.node == node) {
				return true;
			}
		}
		return false;
	} // end of listContains(ArrayList, int)
} // end of class Ant
