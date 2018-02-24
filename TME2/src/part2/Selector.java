/*
 * Selector.java
 * 
 * This class is used at the beginning of the game to select the number of ants for the colony to begin with.  It displays
 * 2 buttons to increase/decrease he starting number.  Only values from 1-20 are allowed.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Selector {
	private int screenX, screenY;
	private Sprite upButton, downButton;
	
	public Selector(int screenX, int screenY) {
		this.screenX = screenX;
		this.screenY = screenY;
		
		setUpButtons();
	} // end of constructor
	
	// set up user selection button sprites
	private void setUpButtons() {	
		upButton = new Sprite("images/upButton.png");
		downButton = new Sprite("images/downButton.png");
		
		Point buttonPos = new Point(screenX / 2, screenY / 2 + upButton.getImage().getHeight());
		upButton.setFocusMid();
		upButton.setPosition(new Point(buttonPos));
		
		buttonPos.translate(0, upButton.getImage().getHeight());
		downButton.setFocusMid();
		downButton.setPosition(new Point(buttonPos));
	} // end of setUpButtons()
	
	// draw buttons and ant number on screen
	public void draw(Graphics2D g2d, int num) {
		// clear background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, screenX, screenY);
		// draw number
		g2d.setColor(Color.BLACK);
		g2d.drawString("Number of Ants: " + num, screenX / 2 - 50, screenY / 2);
		// draw buttons
		upButton.drawImage(g2d);
		downButton.drawImage(g2d);
	} // end of draw(Graphics2D)
	
	// check if user click matches a button position
	public int getClickValue(int mx, int my) {
		Point topLeft, bottomRight;
		// check up button
		topLeft = new Point(upButton.getPosition());
		topLeft.translate(-upButton.getImage().getWidth() / 2, -upButton.getImage().getHeight() / 2);
		bottomRight = new Point((int)topLeft.getX() + upButton.getImage().getWidth(), (int)topLeft.getY() + upButton.getImage().getHeight());
		
		if (mx > topLeft.x && mx < bottomRight.x && my > topLeft.y && my < bottomRight.y) {
				System.out.println("up click ");
			return 1;
		}
		
		// check down button
		topLeft = new Point(downButton.getPosition());
		topLeft.translate(-downButton.getImage().getWidth() / 2, -downButton.getImage().getHeight() / 2);
		bottomRight = new Point(topLeft.x + downButton.getImage().getWidth(), topLeft.y + downButton.getImage().getHeight());

		if (mx > topLeft.x && mx < bottomRight.x && my > topLeft.y && my < bottomRight.y) {
			return -1;
		}
		// no button clicked
		return 0;
	} // end of getClickValue(int, int)
} // end of class Selector
