/*
 * MainPanel.java
 * 
 * As with TME1, the MainPanel class is used as the main interface for the game.  It handles the timing, updating, and
 * drawing functions through the BoardController class.
 * 
 * To play the game, the user selects a number of ants for the colony to start with, then a finite state machine runs
 * as described in the details document for this assignement.  As usual, the game can be exited with usual keys.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPanel extends JPanel
	implements Runnable {
	
	public static final int PWIDTH = 720;
	public static final int PHEIGHT = 720;
	private static final long PERIOD = (long)(1000 / 60 * 1000000);
	private static final int NO_DELAYS_PER_YIELD = 16;
	private static final int MAX_FRAME_SKIPS = 5;
	
	private volatile boolean running = false;
	private volatile boolean gameOver = false;
	private volatile boolean isPaused = false;
	
	private Thread animator;
	private Graphics2D dblg2D;
	private Image dblImage = null;
	
	private BoardController bc;
	
	public MainPanel() {

		setBackground(Color.RED);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		
		setFocusable(true);
		requestFocus();
		readyForTermination();
		
		// game components
		bc = new BoardController(PWIDTH, PHEIGHT);
		
		addMouseListener( new MouseAdapter() {
	      public void mousePressed(MouseEvent e)
	      { bc.checkMouseClick(e.getX(), e.getY()); }
	    });
		
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
				} else if (keyCode == KeyEvent.VK_ENTER) {
					bc.checkEnter();
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
		long excess = 0L;
		long gameStartTime = System.nanoTime();
		long frames = 0L;
		int noDelays = 0;
		
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

	// update game state
	private void gameUpdate(long time, long frames) {
		if (!isPaused && !gameOver) {
			// update game entities
			if (frames % 60 == 0 && frames != 0) {
				bc.update();
			} // end if
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
			dblg2D.setColor(Color.DARK_GRAY);
			dblg2D.fillRect(0, 0, PWIDTH, PHEIGHT);

			// draw game elements
			bc.draw(dblg2D);
			
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
	private void gameOverMessage(Graphics2D g2d) {
		String msg = "GAME OVER";
		
		g2d.setColor(Color.WHITE);
		g2d.drawString(msg, PWIDTH / 2, PHEIGHT / 2);
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
		JFrame app = new JFrame("ANTS");
		app.getContentPane().add(mainPanel, BorderLayout.CENTER);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		app.setResizable(false);
		app.pack();
		app.setVisible(true);
	} // end of main()
} // end of class MainPanel
