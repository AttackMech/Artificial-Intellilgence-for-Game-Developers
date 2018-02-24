/*
 * MainPanel.java
 * 
 * This program is the main interface of the game.  It creates the JPanel and handles the running thread for the game.
 * It calls the game functions of the player, toppings, and enemy to draw and update the game.
 * 
 * The game is played by moving the mouse around the yellow portion of the stage.  The pizza will follow the player mouse
 * movements.  The player must avoid the knife and/or pizza cutter weapons thrown by the enemy, all while attempting to
 * collect the various pizza toppings that appear on screen.  The game is over when the player has been hit 4 times or
 * collects 10 of each type of topping.  The game can be quit at any time by hitting the 'ESC' key.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPanel extends JPanel
	implements Runnable {
	
	public static final int PWIDTH = 1080;
	public static final int PHEIGHT = 720;
	private static final long PERIOD = (long)(1000 / 100 * 1000000);
	private static final int NO_DELAYS_PER_YIELD = 16;
	private static final int MAX_FRAME_SKIPS = 5;
	
	private volatile boolean running = false;
	private volatile boolean gameOver = false;
	private volatile boolean isPaused = false;
	
	private Thread animator;
	private Graphics2D dblg2D;
	private Image dblImage = null;
	
	private Player player;
	private Enemy enemy;
	private Toppings toppings;
	private String info;
	
	public MainPanel() {
		info = "";
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		
		setFocusable(true);
		requestFocus();
		readyForTermination();
		
		// game components
		enemy = new Enemy(PWIDTH, PHEIGHT);
		player = new Player(PWIDTH - enemy.getImage().getWidth(), PHEIGHT);
		toppings = new Toppings();
		
	} // end of constructor
	
	// allow the user to exit the game with keyboard controls
	private void readyForTermination() {
		// game key controls
		addKeyListener ( new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				System.out.println("Key = " + keyCode);
				System.out.print("running was: " + running);
				if ((keyCode == KeyEvent.VK_ESCAPE) ||
					(keyCode == KeyEvent.VK_Q) ||
					(keyCode == KeyEvent.VK_END) ||
					((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
		
					running = false;
				}
			}
		});
	} // end of readyForTermination

	// used for initialization tasks
	public void addNotify() {
		super.addNotify();
		startGame();
	} // end of addNotify
	
	// start game by running thread
	private void startGame() {
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // end of startGame
	
	// stop game from running
	public void stopGame() { running = false; }
	
	// pause game
	public void pauseGame() { isPaused = true; }
	
	// resume game after pause
	public void resumeGame() { isPaused = false; }
	
	
	// main run thread used to create game loop and keep frame rate consistent
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		long gameStartTime = System.nanoTime();
		long frames = 0;
		
		beforeTime = System.nanoTime();
		timeDiff = 0;
		
		running = true;
		
		// game loop
		while(running) {
			++frames;
			
			gameUpdate(timeDiff, frames);
			gameRender(gameStartTime, frames);
			paintScreen();  // draw buffer to screen
			
			// calculate frame rate
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (PERIOD - timeDiff) - overSleepTime;
			
			// sleep if needed
			if (sleepTime > 0) {
				try {	
					Thread.sleep(sleepTime / 1000000L);
				} catch(InterruptedException ex) {
					overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
				}
			} else {
				excess -= sleepTime;
				overSleepTime = 0L;
				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield();
					noDelays = 0;
				}
			}
			
			beforeTime = System.nanoTime();
			
			// skip frames if needed
			int skips = 0;
			while((excess > PERIOD) && (skips < MAX_FRAME_SKIPS)) {
				excess -= PERIOD;
				gameUpdate(PERIOD, frames);
				skips++;
			}
		}
		
		System.exit(0);
	} // end of run
	
	private void gameUpdate(long time, long frames) {
		if (!isPaused && !gameOver) {
			// update game state
			
			// move player to mouse position
			PointerInfo pi = MouseInfo.getPointerInfo();
			Point pt = pi.getLocation();
			Point pt2 = pt;
			try {
				pt2 = this.getLocationOnScreen();
				player.setTarget(pt.x - pt2.x, pt.y - pt2.y);
			} catch(IllegalStateException e) {}
						
			// enemy fire weapon
			if (frames != 0 && frames % 200 == 0) {
				enemy.fireWeapon(player.getKinematic().position.x, player.getKinematic().position.y);
			}
			// make topping appear
			if (frames !=0 && frames % 300 == 0) {
				toppings.addNew(PWIDTH - enemy.getImage().getWidth(), PHEIGHT);
			}
						
			// update game entities
			player.update(time);
			enemy.update(time, player.getKinematic());
			toppings.update(time, player.getFlockTo());
			
			// collision detect
			toppings.checkForCatch(player.getHitBox());
			if (enemy.checkForHits(player.getHitBox())) {
				if (!player.hit()) {
					gameOver = true;
				}
			}
			
			// check player win
			if (toppings.full()) {
				gameOver = true;
			}
		} // end if
	} // end gameUpdate
	
	// render game objects to back buffer for later display
	private void gameRender(long time, long frames) {
		// create/set back buffer
		if (dblImage == null ) {
			dblImage = createImage(PWIDTH, PHEIGHT);
			if (dblImage == null) {
				System.out.println("Buffer image is NULL.");
				return;
			}
		} else {
			dblg2D = (Graphics2D) dblImage.getGraphics();
		
			// set background colours
			dblg2D.setColor(Color.YELLOW);
			dblg2D.fillRect(0, 0, PWIDTH, PHEIGHT);
			dblg2D.setColor(Color.WHITE);
			dblg2D.fillRect(PWIDTH - enemy.img.getWidth(), 0, PWIDTH, PHEIGHT);

			// draw game elements
			player.draw(dblg2D);
			enemy.draw(dblg2D);
			toppings.draw(dblg2D);
			
			// frame/time info
			long elapsed = (System.nanoTime() - time) / (1000000 * 1000);
			if (elapsed != 0) {
				int fps = (int)(frames / elapsed);
				info = ("TIME: " + elapsed + " s    FRAMES: " + frames + "    FPS: " + fps);
			}
			dblg2D.setColor(Color.BLACK);
			dblg2D.drawString(info, 0, 10);
			
			// draw game over message
			if (gameOver) {
				gameOverMessage(dblg2D);
			}
		} // end else
	} // end of gameRender
	
	// draw buffer to screen
	private void paintScreen() {
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dblImage != null)) {
				g.drawImage(dblImage, 0, 0, null);
			}
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	} // end of paintScreen
	
	// create game over message for display with player score
	private void gameOverMessage(Graphics2D g2D) {
		int x = PWIDTH / 2, y = PHEIGHT / 2;
		String msg = "GAME OVER score:" + toppings.getScore();
		g2D.drawString(msg, x, y);
	} // end of gameOverMessage
	
	// various Java window events
	public void windowActivated(WindowEvent e) { resumeGame(); }
	
	public void windowDeactivated(WindowEvent e) { pauseGame(); }
	
	public void windowDeIconified(WindowEvent e) { resumeGame(); }
	
	public void windowIconified(WindowEvent e) { pauseGame(); }
	
	public void windowClosing(WindowEvent e) { stopGame(); }
	
	
	// MAIN
	public static void main(String args[]) {
		
		MainPanel mainPanel = new MainPanel();

		// create a JFrame to hold the test JPanel
		JFrame app = new JFrame("Image Tests");
		app.getContentPane().add(mainPanel, BorderLayout.CENTER);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		app.pack();
		app.setResizable(false);  
		app.setVisible(true);
	} // end of main()
} // end of class MainPanel
