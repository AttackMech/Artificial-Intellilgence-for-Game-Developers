/*
 * Board.java
 * 
 * Similar to part 1, this class is the representation of the game board.  It stores the number and type of each tile.
 * These values are randomly generated.  The home tile is selected first, then a number of random tiles for food, water,
 * and poison.  The number of these special tile types is limited to avoid clutter of the board. 
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;
import java.util.Vector;

public class Board {
	
	private int boardSize; // number of elements in the board
	private int boardPx;  // size of individual board element in pixels
	private int[] gameGrid; // holds type for each board element
	private BoardElement[] elements; // holds the sprites for each element type
	private int home; // hold positions for specified values
	private Graph gridGraph; // holds the graph representation of the game board
	private Random rand; // used to generate random numbers
	
	public Board(int screenSize, int gridSize) {
		boardSize = gridSize;
		boardPx = screenSize / gridSize;
		rand = new Random();
		
		setUpElements();
		setUpGameGrid();
		setUpConnections();
	} // end of constructor
	
	// set up the game grid area and fill with random tiles
	private void setUpGameGrid() {
		gameGrid = new int[boardSize * boardSize];
		// set random home position
		home = rand.nextInt(gameGrid.length);
		// fill with random tile types
		int tileType;
		for (int i = 0; i < gameGrid.length; i++) {
			if (i == home) {
				gameGrid[i] = BoardElement.HOME;
				continue;
			}
			
			// ensure not too many of one type in a row
			tileType = rand.nextInt((BoardElement.NUM_TYPES - 2) * 5) + 1;
			if (tileType != BoardElement.FOOD && tileType != BoardElement.POISON && tileType != BoardElement.WATER) {
				tileType = BoardElement.EMPTY;
			}
			
			gameGrid[i] = tileType;
		}
	} // end of setUpGameGrid()
	
	// set up the array of board elements for use
	private void setUpElements() {
		elements = new BoardElement[BoardElement.NUM_TYPES];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = new BoardElement(i);
			elements[i].setScaleByPx(boardPx);
		}
	} // end of setUpElements
	
	// set up all connections for the board
	public void setUpConnections() {
		Vector<Connection> connections = new Vector<Connection>();
		int nodeID = 0;
		for (int i = 0; i < gameGrid.length; i++) {
			for (int to = 0; to < 4; to++) {
				if (checkConnectNode(i, getConnectNode(i, to))) {
					connections.add(new Connection(++nodeID, 0, i, getConnectNode(i, to)));
				} // end if
			} // end inner for
		} // end outer for
		gridGraph = new Graph(connections);
	} // end of setUpConnections()
	
	// returns the node connected to the passed node based on the value
	private int getConnectNode(int node, int connectValue) {
		switch (connectValue) {
			case 0: // left
				return node - 1;
			case 1: // up
				return node - boardSize;
			case 2: // right
				return node + 1;
			case 3: // down
				return node + boardSize;
			default:
				return node;
		}
	} // end of getConnectNode(int, int)
	
	// checks the node for validity
	private boolean checkConnectNode(int fromNode, int toNode) {
		// check for upper and lower bounds
		if (toNode < 0 || toNode >= boardSize * boardSize) {
			return false;
		}
		// check side bounds
		if ((fromNode % boardSize == 0 && toNode == fromNode - 1) || (fromNode % boardSize == 15 && toNode == fromNode + 1)) {
			return false;
		}
		// check for poison
		if (gameGrid[fromNode] == BoardElement.POISON || gameGrid[toNode] == BoardElement.POISON) {
			return false;
		}
		// everything OK
		return true;
	} // end of checkConnectNode
	
	// draw the game board to the screen
	public void draw(Graphics2D g2d) {
		Point topLeft;
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, boardSize * boardPx, boardSize * boardPx);//(0, 0, boardSize * boardPx, boardSize * boardPx);
		
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < gameGrid.length; i++) {
			
			// draw borders and tile types
			topLeft = elementToPoint(i);
			if (BoardElement.validateTileType(gameGrid[i])) {
				elements[gameGrid[i]].setPosition(new Point(topLeft.x * boardPx, topLeft.y * boardPx));
				elements[gameGrid[i]].drawImage(g2d);
			} // end if
			g2d.drawRect(topLeft.x * boardPx, topLeft.y * boardPx, boardPx, boardPx);
		} // end for		
	} // end of draw(Graphics2D)
	
	// changes the given point coordinates to a single corresponding array element
	public int pointToElement(Point area) {
		if (area.x < 0 || area.y < 0 || area.x > boardSize * boardPx || area.y > boardSize * boardPx) {
			return -1;
		}
		return area.y / boardPx * boardSize + area.x / boardPx;
	} // end of pointToElement(Point)
	
	// changes the given array element to a corresponding point coordinate
	public Point elementToPoint(int element) {
		Point area = new Point(-1,-1);
		if (element < 0 || element > boardSize * boardSize) {
			return area;
		}
		
		area.y = element / boardSize;
		area.x = element - (area.y * boardSize);
		
		return area;
	} // end of elementToPoint(int)
	
	// returns the top left corner of the passed grid position 
	public Point getTopLeft(int element) {
		Point topLeft = new Point();
		topLeft = elementToPoint(element);
		topLeft.x *= boardPx;
		topLeft.y *= boardPx;
		return topLeft;
	} // end of getTopLeft(int)
	
	// returns the home position
	public int getHome() { return home; }
	
	// returns the grid or screen coordinates of the start point based on the passed parameter
	public Point getHomePoint(boolean screen) {
		// calculate grid coords
		Point homePoint = elementToPoint(home);
		
		// calculate screen coords if needed
		if (screen) {
			homePoint.x *= boardPx;
			homePoint.y *= boardPx;
		}
		
		return homePoint;
	} // end of getStartPoint(boolean)
	
	// returns the scale of the grid based on pixels or ratio based on the passed parameter
	public int getScale(boolean pixels) {
		if (pixels) {
			return boardPx;
		} else {
			return  boardPx / elements[0].getImage().getWidth();
		}
	} // end of getScale(boolean)
	
	// returns the board positions of all food elements
	public int[] getAllFood() {
		int numFood = 0;
		for (int i = 0; i < gameGrid.length; i++) {
			if (gameGrid[i] == BoardElement.FOOD) {
				numFood++;
			}
		}
		
		int[] allFood = new int[numFood];
		for (int i = 0; i < gameGrid.length; i++) {
			if (gameGrid[i] == BoardElement.FOOD) {
				allFood[--numFood] = i;
			}
		}
		
		return allFood;
	} // end of getAllFood()
	
	// returns the board positions of all water elements
	public int[] getAllWater() {
		int numWater = 0;
		for (int i = 0; i < gameGrid.length; i++) {
			if (gameGrid[i] == BoardElement.WATER) {
				numWater++;
			}
		}
		
		int[] allWater = new int[numWater];
		for (int i = 0; i < gameGrid.length; i++) {
			if (gameGrid[i] == BoardElement.WATER) {
				allWater[--numWater] = i;
			}
		}
		
		return allWater;
	} // end of getAllWater()
	
	// returns the board positions of all poison elements
	public int[] getAllPoison() {
		int numPoison = 0;
		for (int i = 0; i < gameGrid.length; i++) {
			if (gameGrid[i] == BoardElement.POISON) {
				numPoison++;
			}
		}
		
		int[] allPoison = new int[numPoison];
		for (int i = 0; i < gameGrid.length; i++) {
			if (gameGrid[i] == BoardElement.POISON) {
				allPoison[--numPoison] = i;
			}
		}
		
		return allPoison;
	} // end of getAllPoison()
	
	// returns the graph representation of the game board
	public Graph getGridGraph() { return gridGraph; }
} // end of class Board
