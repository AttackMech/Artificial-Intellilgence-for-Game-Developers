/*
 * Ant.java
 * 
 * This class is used to represent an individual ant in the colony.  It is almost the same as in part 1, handling drawing
 * the ant at the correct position as well as pathfinding.  It also holds the current state of the ant, and draws the
 * identification number and letter representing the current state in a colour that also represents the current state.
 * The ant behaviour is as described in the state machine in the details document for this assignment.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Ant extends Sprite {
	public class NodeRecord {
		public int node;
		public int cost;
		public Connection cnx;
		public int costSoFar;
		public int estimatedTotalCost;
	}
	
	private int id, boardSize, elementPosition, scaleFactor;
	private ArrayList<Connection> pathFound;
	private boolean isAlive, searchFood, searchWater, goHome;
	private Random rand;
	
	private NodeRecord startRecord;
	private NodeRecord current;
	private Connection[] connections;
	private ArrayList<NodeRecord> open;
	private ArrayList<NodeRecord> closed;
	private Graph graph;
	private int home, start, end;
	private Heuristic heuristic;
	
	public Ant(int id, int boardSize, int scaleFactor, int home) {
		super("images/ant.png");
		setScaleByPx(scaleFactor);
		this.scaleFactor = scaleFactor;
		this.id = id;
		this.boardSize = boardSize;
		this.home = home;
		elementPosition = home;
		setPosition(elementToPoint(home));
			System.out.println("new ant at " + home + " scale: " + scaleFactor);
			System.out.println("board: " + boardSize + " ");
		isAlive = true;
		searchFood = true;
		searchWater = false;
		goHome = false;
		
		rand = new Random();
		
		open = new ArrayList<NodeRecord>();
		closed = new ArrayList<NodeRecord>();
	} // end of constructor
	
	// changes the ant state based on the current state
	public void changeMode() {
		if (!isAlive) {
			return;
		}
		
		if (searchFood) {
			searchFood = false;
			goHome = true;
			return;
		}
		
		if (searchWater) {
			searchWater = false;
			searchFood = true;
			return;
		}
		
		if (goHome) {
			goHome = false;
			searchWater = true;
		}
	} // end of changeMode()
	
	// moves the ant one step closer along the found path towards the home position
	public void nextStepHome() {
		// ant at home, start looking for water
		if (pathFound.size() == 0) {
			changeMode();
			return;
		}
		
		// move to next spot in connection list and remove
		elementPosition = pathFound.get(0).toNode;
		pathFound.remove(0);
		setPosition(elementToPoint(elementPosition));
	} // end of nextStepHome()
	
	// moves the ant in a random direction on the board
	public void nextRandomMove() {
		while(!tryMove(rand.nextInt(4))) {}
		setPosition(elementToPoint(elementPosition));
	} // end of nextRandomMove()
	
	// attempts to adjust the position of the ant based on the passed parameter
	public boolean tryMove(int move) {
		switch (move) {
			case 0: // up
				if (elementPosition - boardSize < 0) {
					return false;
				} else {
					elementPosition -= boardSize;
					return true;
				}
			case 1: // down
				if (elementPosition + boardSize <= boardSize * boardSize) {
					return false;
				} else {
					elementPosition += boardSize;
					return true;
				}
			case 2: // left
				if (elementPosition % boardSize == 0) {
					return false;
				} else {
					elementPosition--;
					return true;
				}
			default: //right
				if ((elementPosition + 1) % boardSize == 0) {
					return false;
				} else {
					elementPosition++;
					return true;
				}
		}
	} // end of tryMove(int)
	
	// changes the given element position to a corresponding point coordinate on screen
	public Point elementToPoint(int element) {
		Point area = new Point(-scaleFactor,-scaleFactor);
		
		if (element < 0 || element > boardSize * boardSize) {
			return area;
		}
		
		area.y = element / boardSize;
		area.x = element - (area.y * boardSize);
		area.y *= scaleFactor;
		area.x *= scaleFactor;
			System.out.println("setting position = " + area.toString());
		return area;
	} // end of elementToPoint(int)
	
	// determines what the ant should do when on a food tile
	public boolean onFood() {
		if (searchFood) {
			changeMode();
			return true;
		} else {
			return false;
		}
	} // end of onFood()
	
	// determines what the ant should do when on a water tile
	public void onWater() {
		if (searchWater) {
			changeMode();
		}
	} // end of onWater()
	
	// determines what the ant should do when on a poison tile
	public void onPosion() {
		isAlive = false;
	} // end of onPoison()
	
	// updates the ant position if it is still alive
	public void update() {
		// check if alive
		if (!isAlive) {
			return;
		}
		
		// update position based on current state
		if (searchFood || searchWater) {
			nextRandomMove();
		} else {
			nextStepHome();
		}
	} // end of update()
	
	// draws the ant on screen with behaviour info
	public void draw(Graphics2D g2d) {
		drawImage(g2d);

		if (isSearchFood()) {
			g2d.setColor(Color.MAGENTA);
			g2d.drawString(id +" F", getPosition().x, getPosition().y + 10);
		} else if (isSearchWater()) {
			g2d.setColor(Color.BLUE);
			g2d.drawString(id + " W", getPosition().x, getPosition().y + 10);
		} else if (isGoHome()) {
			g2d.setColor(Color.RED);
			g2d.drawString(id + " H", getPosition().x, getPosition().y + 10);
		}
	} // end of draw(Graphics2D)
	
	// returns the current list of open nodes
	public int[] getOpenList() {
		int[] ol = new int[open.size()];
		for (int i = 0; i < open.size(); i++) {
			ol[i] = open.get(i).node;
		}
		return ol;
	} // end of getOpenList
	
	// returns the current list of closed nodes
	public int[] getClosedList() {
		int[] cl = new int[closed.size()];
		for (int i = 0; i < closed.size(); i++) {
			cl[i] = closed.get(i).node;
		}
		return cl;
	} // end of getClosedList()
	
	// getters...
	public int getElementPosition() { return elementPosition; }
	
	public boolean isSearchFood() { return searchFood; }
	
	public boolean isSearchWater() { return searchWater; }
	
	public boolean isGoHome() { return goHome; }
	
	public boolean checkAlive() { return isAlive; }
	
	// pathfinding procedure as outlined in the textbook
	public void run() {
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
		}
		// here if found goal or no more nodes to search... which one?
		if (current.node != end) {
			// run out of nodes w/o finding goal == no solution
			System.out.println("Could Not Find Pathfinding Solution");
			pathFound = null;
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
	} // end of run()
	
	// initiates the pathfinding process wtih the A* algrothim from the textbook
	public void pathfindAStar(Graph graph, Heuristic heuristic) {
		this.graph = graph;
		start = elementPosition;
		end = home;
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
		
		// begin pathfinding procedure
		run();
	} // end of pathfindAStar(Graph, Heuristic)
	
	// returns the smallest node in the list
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
	
	// reverses the current list and returns a simple array representation
	private ArrayList<Connection> reverse(ArrayList<Connection> list) {
		ArrayList<Connection> reverseList = new ArrayList<Connection>();
		for (int i = 0; i < list.size(); i++) {
			reverseList.add(0, list.get(i));
		}
		
		return reverseList;
	} // end of reverse(ArrayList)
	
	// finds the given node in the list provided
	private NodeRecord findNode(ArrayList<NodeRecord> list, int node) {
		for (NodeRecord	nr : list) {
			if (nr.node == node) {
				return nr;
			}
		}
		System.out.println("No Matching Node Found");
		return null;
	} // end of findNode(ArrayList, int)
	
	// finds the given node in either the open/closed lists
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
	} // end of findNode(ArrayList, ArrayList, int int)
	
	// determines if the given list contains the given node
	private boolean listContains(ArrayList<NodeRecord> list, int node) {
		for (NodeRecord nr : list) {
			if (nr.node == node) {
				return true;
			}
		}
		return false;
	} // end of listContains(ArrayList, int)
} // end of class Ant
