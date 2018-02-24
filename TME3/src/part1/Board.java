/*
 * Board.java
 * 
 * This class is mainly used to represent the game board visually to the player.  It handles the drawing of the game grid
 * and draws the chips on the board using the passed array of chips from the GameController class.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 20, 2016
 * 
 */

package part1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Board {
	public static final int ROWS = 6;
	public static final int COLS = 7;
	public static final int WIDTH = MainPanel.PWIDTH * 2 / 3;
	public static final int HEIGHT = MainPanel.PHEIGHT;
	
	// empty constructor
	public Board() { }
	
	// draw the game board on screen
	public void draw(Graphics2D g2d, int[] chips, int select) {
		// background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		
		// lines
		g2d.setColor(Color.BLACK);
		for (int i = 1; i <= ROWS; i++) {
			g2d.drawLine(0, i * getCellHeight(), WIDTH, i * getCellHeight());
		}
		for (int i = 0; i <= COLS; i++) {
			g2d.drawLine(i * getCellWidth(), 0, i * getCellWidth(), HEIGHT);
		}
		
		// chips
		Point topLeft;
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < chips.length; i++) {
			if (chips[i] == 1) {
				topLeft = getTopLeft(i);
				g2d.drawOval(topLeft.x, topLeft.y, getChipDiam(), getChipDiam());
			} else if (chips[i] == 2) {
				topLeft = getTopLeft(i);
				g2d.fillOval(topLeft.x, topLeft.y, getChipDiam(), getChipDiam());
			}
		} // end for
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(WIDTH, 0, WIDTH * 3 / 2, HEIGHT);
		
		// draw selector if needed
		drawSelect(g2d, select);
	} // end of draw(Graphics2D)
	
	// draw the current user selection for placing a piece
	private void drawSelect(Graphics2D g2d, int position) {
		// draw cell background
		g2d.setColor(Color.RED);
		g2d.fillRect(getCellWidth() * position, 0, getCellWidth(), getCellHeight());
	} // end of drawSelect
	
	// get the diameter of a chip to fit in a cell
	public static int getChipDiam() {
		int radius;
		if (getCellHeight() < getCellWidth()) {
			radius = getCellHeight();
		} else {
			radius = getCellWidth();
		}
		return radius -3;
	} // end of getChipDiam
	
	// checks if the given position is in the game selection row
	public static boolean checkInSelectRow(int x, int y) {
		if (x >= 0 && x <= COLS * getCellWidth() && y >= 0 && y <= getCellHeight()) {
			return true;
		}
		return false;
	} // end of checkInSelectRow(int, int)
	
	// finds the position within the select row
	public int getSelectPosition(int x) {
		// check valid range
		if (x < 0 || x > COLS * getCellWidth()) {
			return -1;
		}
		
		// find position in row
		int position = 0;
		while (x > (position * getCellWidth())) {
			++position;
		}
		return --position;
	} // end of getSelectPosition(int)
	
	// returns the top left corner of the cell as a point
	public static Point getTopLeft(int cell) {
		return new Point((cell % COLS) * getCellWidth(), (int)(cell / COLS) * getCellHeight() + getCellHeight());
	}
	
	// returns the width of a game cell
	public static int getCellWidth() { return WIDTH / COLS; }
	
	// returns the height of a game cell
	public static int getCellHeight() { return HEIGHT / (ROWS + 1); }
	
	// returns the midpoint of a given game cell
	public static Point getCellMid(int col, int row) {
		return new Point(col * getCellWidth() - getCellWidth() / 2, row * getCellHeight() - getCellHeight() / 2);
	} // end of getCellMid(int, int)
} // end of class Board
