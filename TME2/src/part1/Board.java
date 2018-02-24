/*
 * Board.java
 * 
 * This class represents the game board.  It draws the tiles according to the type selected by the user.  Each tile is
 * drawn with a border and number, along with the BoardElement type if necessary.  Also, during the user selection phase,
 * a red border and cursor are used to indicate the currently selected tile.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 8, 2016
 * 
 */

package part1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;

public class Board {
	
	private int boardSize; // number of elements in the board
	private int boardPx;  // size of individual board element in pixels
	private int[] gameGrid; // holds type for each board element
	private BoardElement[] elements; // holds the sprites for each element type
	private int cursor, start, goal; // hold positions for specified values
	private Graph gridGraph; // holds the graph info for pathfinding
	private Sprite openImage, closedImage; // holds sprites to display pathfinding process
	private int[] openList, closedList; // holds lists of nodes for pathfinding
	
	public Board(int screenSize, int gridSize) {
		boardSize = gridSize;
		boardPx = screenSize / gridSize;
		cursor = 0;
		start = -1;
		goal = -1;
		
		setUpGameGrid();
		setUpElements();
		
		openImage = new Sprite("images/openNode.png");
		closedImage = new Sprite("images/closedNode.png");
		openImage.setScaleByPx(boardPx);
		closedImage.setScaleByPx(boardPx);
		openList = new int[0];
		closedList = new int[0];
	} // end of constructor
	
	// set up the game grid area and fill with default open tiles
	private void setUpGameGrid() {
		gameGrid = new int[boardSize * boardSize];
		fillGameGrid(-1, false);
	} // end of setUpGameGrid()
	
	// set up the array of board elements for use
	private void setUpElements() {
		elements = new BoardElement[6];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = new BoardElement(i);
			elements[i].setScaleByPx(boardPx);
		}
	} // end of setUpElements()
	
	// set up all connections for the board
	public void setUpConnections() {
		Vector<Connection> connections = new Vector<Connection>();
		int nodeID = 0;
		for (int i = 0; i < boardSize * boardSize; i++) {
			for (int to = 0; to < 4; to++) {
				if (checkConnectNode(i, getConnectNode(i, to))) {
					if (gameGrid[getConnectNode(i, to)] < BoardElement.OPEN) {
						connections.add(new Connection(++nodeID, 0, i, getConnectNode(i, to)));
					} else {
						connections.add(new Connection(++nodeID, gameGrid[getConnectNode(i, to)] - BoardElement.OPEN, i, getConnectNode(i, to)));
					} // end else
				} // end if
			} // end inner for
		} // end outer for
		gridGraph = new Graph(connections);
	} // end of setUpConnections()
	
	// returns the connection node based on the passed parameters
	private int getConnectNode(int node, int connectValue) {
		switch (connectValue) {
			case 0: // left of current node
				return node - 1;
			case 1: // up from current node
				return node - boardSize;
			case 2: // right of current node
				return node + 1;
			case 3: // down from current node
				return node + boardSize;
			default:
				return node;
		}
	} // end of getConnectNode(int, int)
	
	// checks the connection node for validity
	private boolean checkConnectNode(int fromNode, int toNode) {
		// check for upper and lower bounds
		if (toNode < 0 || toNode >= boardSize * boardSize) {
			return false;
		}
		// check side bounds
		if ((fromNode % boardSize == 0 && toNode == fromNode - 1) || (fromNode % boardSize == 15 && toNode == fromNode + 1)) {
			return false;
		}
		// check for obstacle
		if (gameGrid[toNode] == BoardElement.OBSTACLE) {
			return false;
		}
		// everything OK
		return true;
	} // end of checkConnectNode(int, int)
	
	// fills the gameGrid variable with the given type, skipping the start/goal tiles if requested
	public void fillGameGrid(int value, boolean skip) {
		for (int i = 0; i < gameGrid.length; i++) {
			if (skip && (gameGrid[i] == BoardElement.START || gameGrid[i] == BoardElement.GOAL)) {
				continue;
			}
			gameGrid[i] = value;
		}
	} // end of fillGameGrid(int, boolean)
	
	// set the grid element to the specified type
	public void setGridElement(Point area, int type) {
		if (!BoardElement.validateTileType(type)) {
			return;
		}
		gameGrid[pointToElement(area)] = type;
	} // end of setGridElement(Point, int)
	
	// draw the game board to the screen
	public void draw(Graphics2D g2d, boolean drawCursor) {
		Point topLeft;  // used to draw from the top left corner of the current tile
		String num; // used to store/draw the number of the current tile
		
		// background
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(0, 0, boardSize * boardPx, boardSize * boardPx);
		
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < gameGrid.length; i++) {
			
			// draw borders
			topLeft = elementToPoint(i);
			num = "" + i;
			if (BoardElement.validateTileType(gameGrid[i])) {
				
				elements[gameGrid[i]].setPosition(new Point(topLeft.x * boardPx, topLeft.y * boardPx));
				elements[gameGrid[i]].setScaleByPx(boardPx);
				elements[gameGrid[i]].drawImage(g2d);
			} // end if
			g2d.drawRect(topLeft.x * boardPx, topLeft.y * boardPx, boardPx, boardPx);
			g2d.drawString(num, topLeft.x * boardPx + boardPx / 2, topLeft.y * boardPx + boardPx / 2);
		} // end for

		// draw cursor if necessary
		if (drawCursor) {
			topLeft = elementToPoint(cursor);
			num = "" + cursor;
			g2d.setColor(Color.RED);
			// set stroke to solid 3px
			g2d.drawRect(topLeft.x * boardPx, topLeft.y * boardPx, boardPx, boardPx);
			g2d.drawString(num, topLeft.x * boardPx + boardPx / 2, topLeft.y * boardPx + boardPx / 2);
		}
		
		// draw nodes if pathfinding
		for (int i = 0; i < closedList.length; i++) {
			topLeft = elementToPoint(closedList[i]);
			closedImage.setPosition(new Point(topLeft.x * boardPx, topLeft.y * boardPx));
			closedImage.drawImage(g2d);
		}
		for (int i = 0; i < openList.length; i++) {
			topLeft = elementToPoint(openList[i]);
			openImage.setPosition(new Point(topLeft.x * boardPx, topLeft.y * boardPx));
			openImage.drawImage(g2d);
		}
		
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
	
	// returns a point referencing the grid element at the passed coordinates
	public Point processGridClick(int x, int y) {
		Point gridCoords = new Point(-1,-1);
		for (int i = 0; i < boardSize; i++) {
			if (i * boardPx > x) {
				gridCoords.x = i - 1;
			}
			if (i * boardPx > y) {
				gridCoords.y = i - 1;
			}
		}
		return gridCoords;
	} // end of processGridClick(int, int)
	
	// set the start position to the specified position
	public boolean setStart(int x, int y) {
		int element = pointToElement(new Point(x, y));
		if (element < 0 || element > boardSize * boardSize) {
			return false;
		}
		if (start != -1) {
			gameGrid[start] = -1;
		}
		start = element;
		gameGrid[element] = BoardElement.START;
		return true;
	} // end of setStart(int)
	
	// set the goal position to the specified position
	public boolean setGoal(int x, int y) {
		int element = pointToElement(new Point(x, y));
		if (element < 0 || element > boardSize * boardSize || element == start) {
			return false;
		}
		if (goal != -1) {
			gameGrid[goal] = -1;
		}
		goal = element;
		gameGrid[element] = BoardElement.GOAL;
		return true;
	} // end of setGoal(int)
	
	// cycle through the various types of terrain at the specified position
	public boolean setTerrain(int x, int y) {
		int element = pointToElement(new Point(x, y));
		if (element < 0 || element > boardSize * boardSize || element == start || element == goal) {
			return false;
		}
		if (++gameGrid[element] > BoardElement.OBSTACLE) {
			gameGrid[element] = BoardElement.OPEN;
		}
		return true;
	} // end of setTerrain(int, int)
	
	// sets the value of the cursor to the area of the grid specified if possible
	public void setCursor(Point mouse) {
		int newCursor = pointToElement(mouse);
		if (newCursor != -1) {
			cursor = newCursor;
		}
	} // end of setCursor(point)
	
	// returns the top left corner of the passed grid position 
	public Point getTopLeft(int element) {
		Point topLeft = new Point();
		topLeft = elementToPoint(element);
		topLeft.x *= boardPx;
		topLeft.y *= boardPx;
		return topLeft;
	} // end of getTopLeft(int)
	
	// returns the start position
	public int getStart() { return start; }
	
	// returns the grid or screen coordinates of the start point based on the passed parameter
	public Point getStartPoint(boolean screen) {
		// calculate grid coords
		Point startPoint = elementToPoint(start);
		
		// calculate screen coords if needed
		if (screen) {
			startPoint.x *= boardPx;
			startPoint.y *= boardPx;
		}
		
		return startPoint;
	} // end of getStartPoint(boolean)
	
	// returns the scale of the grid based on pixels or ratio based on the passed parameter
	public int getScale(boolean pixels) {
		if (pixels) {
			return boardPx;
		} else {
			return  boardPx / elements[0].getImage().getWidth();
		}
	} // end of getScale(booleam)
	
	// returns the goal position
	public int getGoal() { return goal; }
	
	// returns the array of tile values
	public Graph getGridGraph() { return gridGraph; }
	
	// sets the open and closed lists to the passed array values
	public void setLists(int[] open, int[] closed) {
		openList = open;
		closedList = closed;
	} // end of setLists(int[], int[])
	
} // end of class Board