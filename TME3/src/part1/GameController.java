/*
 * GameController.java
 * 
 * This class is used to control the various game entities and handle the differing game modes.  The four modes are: 
 * 1 - user turn - where the user selects the position to place their game piece
 * 2 - user move - in which the selected position in the previous mode animates to fall into the correct position on the
 * 					game board
 * 3 - cpu turn - where the minimaxing algorithm is invoked to allow the computer player to select the best move position
 * 4 - cpu move - similar to mode 2, but this time animating the computer players game piece
 * 
 * Additionally, this class also stores and updates the array that represents the chip positions in the game, passing it
 * to be drawn or updated as necessary for different functions.  This array is also used to check for the various win
 * conditions with several modularized functions.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 20, 2016
 * 
 */

package part1;

import java.awt.Graphics2D;

public class GameController {
	private static final int USER_TURN = 0;
	private static final int USER_MOVE = 1;
	private static final int CPU_TURN = 2;
	private static final int CPU_MOVE = 3;
	private static final int MODES = 4;
	
	private static final int TIE = 0;
	private static final int USER_CHIP = 1;
	private static final int CPU_CHIP = 2;
	
	private int[] chips;
	private int mode;
	private int winner;
	private int userSelect;
	private int dropChip;
	private boolean gameOver;
	private Board brd;
	private CPUPlayer cpu;
	
	public GameController() {
		chips = new int[Board.ROWS * Board.COLS];
		mode = 0;
		userSelect = -1;
		dropChip = -1;
		
		brd = new Board();
		cpu = new CPUPlayer();
		
		System.out.println("BOARD:");
		System.out.println("   cell W:" + Board.getCellWidth() + "  cell H: " + Board.getCellHeight());
		System.out.println("");
	} // end of constructor
	
	// updates the game based on the current game mode
	public void update(long frames) {
		// check game over
		if (gameOver) {
			return;
		}
		// update the game depending on the mode
		switch (mode) {
			case USER_MOVE:
				if (frames % 10 == 0) {
					nextDropPosition(true);
				}
				break;
			case CPU_TURN:
				doCPUTurn();
				break;
			case CPU_MOVE:
				if (frames % 10 == 0) {
					nextDropPosition(false);
				}
				break;
		}
	} // end of update(long)
	
	// draw the various game elements to the screen
	public void draw(Graphics2D g2d) {
		// draw board and chips
		if (mode == USER_TURN) {
			brd.draw(g2d, chips, userSelect);
		} else {
			brd.draw(g2d, chips, -1);
		}
	} // end of draw(Graphics2D)
	
	// moves the placed chip vertically down into it's position on the board
	private void nextDropPosition(boolean user) {
		// remove previous chip
		if (dropChip - Board.COLS >= 0) {
			chips[dropChip - Board.COLS] = 0;
		}
		// move chip to new position
		if (user){
			chips[dropChip] = USER_CHIP;
		} else {
			chips[dropChip] = CPU_CHIP;
		}
		// check for last position in column
		if (dropChip + Board.COLS < Board.COLS * Board.ROWS && chips[dropChip + Board.COLS] == 0) {
			dropChip += Board.COLS;
		} else {
			nextMode();
		}
	} // end of nextDropPosition
	
	// adds user chip at the selected position if it's the user turn
	public void checkMouseClick(int mx, int my) {
		if (mode != USER_TURN) {
			return;
		}
		
		if (Board.checkInSelectRow(mx, my) && chips[brd.getSelectPosition(mx)] == 0) {
			dropChip = brd.getSelectPosition(mx);
			nextMode();
		}
	} // end of checkMouseClick(int, int)
	
	// highlight the selected position if user turn
	public void trackMouse(int mx, int my) {
		if (mode != USER_TURN) {
			return;
		}
		
		if (Board.checkInSelectRow(mx, my)) {
			userSelect = brd.getSelectPosition(mx);
		} else {
			userSelect = -1;
		}
	} // end of trackMouse(int, int)
	
	// calculates the next move for the CPU and stops the game if error with move selection
	private void doCPUTurn() {
		dropChip = cpu.getMove(chips);
		if (dropChip < 0) {
			System.out.println("INVALID CPU MOVE");
			mode = -1;
			return;
		} else {
			nextMode();
		}
	} // end of doCPUTurn()
	
	// change to the next mode in the sequence
	private void nextMode() {
		if (++mode >= MODES) {
			mode = USER_TURN;
		}
		// check for game over (win/tie)
		if (mode == USER_TURN || mode == CPU_TURN) {
			isGameOver();
		}
	} // end of nextMode()
	
	// checks for game over conditions
	private void isGameOver() {
		gameOver = false;
		// check for win conditions
		for (int i = 0; i < chips.length; i++) {
			if (gameOver) {
				break;
			}
			if (chips[i] != 0) {
				 gameOver = checkWin(i);
			}
		}
		
		// check for tie
		if (!gameOver) {
			winner = TIE;
			gameOver = isBoardFull();
		}
	}
	
	// checks if there is a possible win scenario starting at the given position
	private boolean checkWin(int position) {
		int winPlayer = chips[position];
		// check for different win conditions
		if (checkHorizontalWin(position, winPlayer) || checkVerticalWin(position, winPlayer) ||	checkDiagonalWin(position, winPlayer)) {
			winner = winPlayer;
			return true;
		}
		return false;
	} // end checkWin(int)
	
	// check the pieces for a horizontal win going right
	private boolean checkHorizontalWin(int position, int winPlayer) {
		boolean win = false;
		// make sure at least 4 available positions right
		if (position % Board.COLS > Board.COLS - 4) {
			return win;
		}
		// check positions to the right
		for (int i = 1; i < 4; i++) {
			if (chips[position + i] != winPlayer) {
				win = false;
				break;
			}
			win = true;
		}
		return win;
	} // end of checkHorizontalWin(int, int)
	
	// check the pieces for a vertical win downward
	private boolean checkVerticalWin(int position, int winPlayer) {
		boolean win = false;
		// make sure at least 4 available positions down
		if ((int)(position / Board.COLS) > Board.ROWS - 4) {
			return win;
		}
		// check positions downward
		for (int i = 1; i < 4; i++) {
			if (chips[position + Board.COLS * i] != winPlayer) {
				win = false;
				break;
			}
			win = true;
		}
		
		return win;
	} // end of checkVerticalWin(int, int)
	
	// check the pieces for vertical win down and left/right
	private boolean checkDiagonalWin(int position, int winner) {
		boolean win = false;
		// check diagonally down and right
		if (position % Board.COLS <= Board.COLS - 4 && (int)(position / Board.COLS) <= Board.ROWS - 4) {
			for (int i = 1; i < 4; i++) {
				if (chips[position + i + Board.COLS * i] != winner) {
					win = false;
					break;
				}
				win = true;
			} // end for
		} // end if
		
		// check diagonally down and left
		if (!win && position % Board.COLS >= 3 && (int)(position / Board.COLS) <= Board.ROWS - 4) {
			for (int i = 1; i < 4; i++) {
				if (chips[position - i + Board.COLS * i] != winner) {
					win = false;
					break;
				}
				win = true;
			} // end for
		} // end if
		
		return win;
	} // end of checkDiagonalWin(int, int)
	
	// check if the board is full
	private boolean isBoardFull() {
		for (int i = 0; i < chips.length; i++) {
			if (chips[i] == 0) {
				return false;
			}
		}
		return true;
	} // end of isBoardFull()
	
	// getters...
	public boolean getGameOver() { return gameOver; }
	
	public int getWinner() { return winner; }
} // end of class GameController.java
