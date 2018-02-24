/*
 * Details.java
 * 
 * This class is used to show information to the user in a side panel to the right of the game board.  It displays
 * instructions on how to proceed to the next step in the selection process as well as what kind of tiles can currently
 * be selected based on the current game mode.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 8, 2016
 * 
 */

package part1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Details {
	private int w, h; // width/height of panel area
	private BoardElement[] elements; // holds the sprites for each element type
	private String pathString[]; // used to display pathfinding results
	
	public Details(int width, int height) {
		w = width;
		h = height;
		setUpElements();
	} // end of constructor
	
	// draw the panel components to the screen
	public void draw(Graphics2D g2d, int mode) {
		// draw background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(w * 2, 0, w, h);
		
		// draw text and graphics
		if (mode < BoardController.RUN) {
			drawDisplayMsg(g2d, mode);
			drawIcons(g2d, mode);
		} else if (pathString != null) {
			g2d.setColor(Color.RED);
			for (int i = 0; i < pathString.length; i++) {
				g2d.drawString(pathString[i],  (w * 2 + 5), (15 + i * 20));
			} // end for
		} // end else if
	} // end of draw (Graphics2D, int)
	
	// set up the array of board elements for use
	private void setUpElements() {
		elements = new BoardElement[BoardElement.OBSTACLE + 1];
		for (int i = BoardElement.START; i < elements.length; i++) {
			elements[i] = new BoardElement(i);
		}
	} // end of setUpElements
	
	// draw message string based on the passed parameter
	public void drawDisplayMsg(Graphics2D g2d, int mode) {
		// drawing coordinates
		int xPos = w * 2 + 5;
		int yPos = 15;
		
		// draw message
		g2d.setColor(Color.BLACK);
		switch (mode) {
			case BoardController.SELECT_START:
				g2d.drawString("Please select a starting point.", xPos, yPos);
				break;
			case BoardController.SELECT_GOAL:
				g2d.drawString("Please select the goal.", xPos, yPos);
				break;
			case BoardController.SELECT_TERRAIN:
				g2d.drawString("Please select the terrain.", xPos, yPos);
				yPos += 20;
				g2d.drawString("\nClick each tile to change.", xPos, yPos);
				break;
		}
		
		g2d.drawString("Press 'Enter' when finished", xPos, yPos + 20);
	} // end of getDisplayMsg(Graphics2D, int)
	
	// draw information icons to the screen based on the passed parameter
	public void drawIcons(Graphics2D g2d, int mode) {
		// drawing coordinates
		int xPos = w * 2 + w / 2;
		int yPos = 50;
		
		// draw tile icons
		switch (mode) {
			case BoardController.SELECT_START:
				elements[BoardElement.START].setPosition(new Point(xPos, yPos));
				elements[BoardElement.START].drawImage(g2d);
				break;
			case BoardController.SELECT_GOAL:
				elements[BoardElement.GOAL].setPosition(new Point(xPos, yPos));
				elements[BoardElement.GOAL].drawImage(g2d);
				break;
			case BoardController.SELECT_TERRAIN:
				for (int i = BoardElement.OPEN; i < elements.length; i++) {
					elements[i].setPosition(new Point(xPos, yPos + ((i - BoardElement.OPEN) * (h - yPos) / 5 + (h - yPos) / 5 / 4)));
					elements[i].drawImage(g2d);
					g2d.drawString(BoardElement.getTileName(i), (xPos - w / 2 + 15), yPos + ((i - BoardElement.OPEN) * (h - yPos) / 5 + (h - yPos) / 5 / 4 + 15));
				}
				break;
		}
	} // end of drawIcons(Graphics2D, int)
	
	// sets the string to display the pathfinding results
	public void setPathString(String[] pathString) { this.pathString = pathString; }
} // end of class Details
