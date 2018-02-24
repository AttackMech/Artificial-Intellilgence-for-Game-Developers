/*
 * GameBoard.java
 * 
 * This class is used to represent the board in a way the the minimaxing algorithm can use.  It has various functions to
 * return the moves available and such, all as outlined by the textbook in the section on minimaxing.  The key function
 * of this class is the evaluation function.  It is a vital part of the minimaxing algorithm.  The win condition checking
 * functions are the same as those in the GameController class.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 20, 2016
 * 
 */

package part1;

public class GameBoard {
	public static final int CPU_PLAYER = 2;
	public static final int USER_PLAYER = 1;
	public static final int MAX_SCORE = 4;
	
	private int[] pieces;
	private int currentPlayer;
	
	// constructors
	public GameBoard(int[] pieces, int player) {
		this.pieces = pieces;
		if (player != CPU_PLAYER || player != USER_PLAYER) {
			player = CPU_PLAYER;
		} else {
			currentPlayer = player;
		}
	} // end of constructor(int[], int)
	
	public GameBoard(GameBoard gb) {
		pieces = new int[gb.pieces.length];
		for (int i = 0; i < gb.pieces.length; i++) {
			pieces[i] = gb.pieces[i];
		}
		currentPlayer = gb.currentPlayer;
	} // end of constructor(GameBoard)
	
	
	// returns an array of possible moves
	public int[] getMoves() {
		// check number of moves available
		int moveCount = 0;
		for (int i = 0; i < Board.COLS; i++) {
			if (pieces[i] == 0) {
				++moveCount;
			}
		}
		// create and return moves
		int[] moves = new int[moveCount];
		for (int i = 0; i < pieces.length; i++) {
			// check position is available
			if (pieces[i] != 0) {
				continue;
			}
			// check for bottom row
			if (i + Board.COLS >= pieces.length) {
				moves[--moveCount] = i;
				continue;
			}
			// check other rows
			if (pieces[i + Board.COLS] != 0) {
				moves[--moveCount] = i;
			}
		}
		return moves;
	} // end of getMoves()
	
	// returns a new board with the passed move made
	public GameBoard makeMove(int move) {
		GameBoard newBoard = new GameBoard(this);
		newBoard.pieces[move] = currentPlayer;
		if (newBoard.currentPlayer == CPU_PLAYER) {
			newBoard.currentPlayer = USER_PLAYER;
		} else {
			newBoard.currentPlayer = CPU_PLAYER;
		}
		return newBoard;
	} // end of makeMove(int)
	
	// the static evaluation function to determine the score from the passed player perspective
	public int evaluate(int player) {
		int score = 0;

		int tempScore;
		for (int i = 0; i < pieces.length; i++) {
			if (pieces[i] == player) {
				System.out.println("   checking position " + i);
				tempScore = checkScore(i);
				System.out.println("   score: " + tempScore);
				if (tempScore > score) {
					score = tempScore;
				}
			}
		}

		return score;
	} // end of evaluate
	
	// checks the score at the current position
	public int checkScore(int position) {
		int score = 0;
		int player = pieces[position];

		// check horizontal
		// to the right
		for (int i = 3; i > 0 && (position + i) % Board.COLS > position % Board.COLS; i--) {
			if (pieces[position + i] != player) {
				break;
			}
			score++;
		}
		// to the left
		for (int i = 1; i < 4 && (position - i) % Board.COLS < position % Board.COLS; i++) {
			if (pieces[position - i] != player) {
				break;
			}
			score++;
		}
		
		int tempScore = 0;
		// check vertical
		if (score < MAX_SCORE) {
			// downwards
			for (int i = 3; i > 0 && position + i * Board.COLS < Board.ROWS * Board.COLS; i--) {
				if (pieces[position + i * Board.COLS] != player) {
					break;
				}
				tempScore++;
			}
			// upwards
			for (int i = 1; i < 4 && position - i * Board.COLS >= 0; i++) {
				if (pieces[position - i * Board.COLS] != player) {
					break;
				}
				tempScore++;
			}
			// update return value
			if (tempScore > score) {
				score = tempScore;
			}
		} // end if
				
		// check diagonals
		if (score < MAX_SCORE) {
			tempScore = 0;
			// right and down
			for (int i = 3; i > 0 && (position + i) % Board.COLS > position % Board.COLS
					&& (position + i * Board.COLS) < Board.ROWS * Board.COLS; i--) {
				
				if (pieces[position + i + i * Board.COLS] != player) {
					break;
				}
				tempScore++;
			}
			// left and up
			for (int i = 1; i < 4 && (position - i) % Board.COLS < position % Board.COLS
					&& position - i * Board.COLS >= 0; i--) {
				
				if (pieces[position - i - i * Board.COLS] != player) {
					break;
				}
				tempScore++;
			}
			// update return value
			if (tempScore > score) {
				score = tempScore;
			}
		} // end if
		
		// check diagonal down to up
		if (score < MAX_SCORE) {
			tempScore = 0;
			// right and up
			for (int i = 3; i > 0 && (position + i) % Board.COLS > position % Board.COLS
					&& position - i * Board.COLS >= 0; i++) {
				
				if (pieces[position + i - i * Board.COLS] != player) {
					break;
				}
				tempScore++;
			}
			// left and down
			for (int i = 1; i < 4 && (position - i) % Board.COLS < position % Board.COLS
					&& position + i * Board.COLS < Board.ROWS * Board.COLS; i--) {
				
				if (pieces[position - i + i * Board.COLS] != player) {
					break;
				}
				tempScore++;
			}
			// update return value
			if (tempScore > score) {
				score = tempScore;
			}
		} // end if
		
		// prune value and return
		if (score > MAX_SCORE) {
			score = MAX_SCORE;
		}
		return score;
	} // end of checkScore(int)
	
	// returns the current player
	public int getCurrentPlayer() { return currentPlayer; }
	
	// sets the pieces array to the passed array
	public void setPieces(int[] pieces) { this.pieces = pieces;	}
	
	// checks the win condition of the game board
	public boolean isGameOver() {
		boolean gameOver = false;
		for (int i = 0; i < pieces.length; i++) {
			if (gameOver) {
				break;
			}
			if (pieces[i] != 0) {
				gameOver = checkWin(i);
			}
		}
		return gameOver;
	} // end isGameOver
	
	// checks if there is a possible win scenario starting at the given position
	private boolean checkWin(int position) {
		int winPlayer = pieces[position];
		// check for different win conditions
		if (checkHorizontalWin(position, winPlayer) || checkVerticalWin(position, winPlayer) ||	checkDiagonalWin(position, winPlayer)) {
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
			if (pieces[position + i] != winPlayer) {
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
			if (pieces[position + Board.COLS * i] != winPlayer) {
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
				if (pieces[position + i + Board.COLS * i] != winner) {
					win = false;
					break;
				}
				win = true;
			} // end for
		} // end if
		
		// check diagonally down and left
		if (!win && position % Board.COLS >= 3 && (int)(position / Board.COLS) <= Board.ROWS - 4) {
			for (int i = 1; i < 4; i++) {
				if (pieces[position - i + Board.COLS * i] != winner) {
					win = false;
					break;
				}
				win = true;
			} // end for
		} // end if
		
		return win;
	} // end of checkDiagonalWin(int, int)
} // end of class GameBoard
