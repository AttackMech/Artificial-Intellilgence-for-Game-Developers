/*
 * CPUPlayer.java
 * 
 * This is the class that handles the computer player turns.  This is where the main minimaxing algorithm is featured.  The
 * algorithm is the same as outlined in the textbook for this course, and described in the accompanying description document
 * for this assignment.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 20, 2016
 * 
 */

package part1;

public class CPUPlayer {
	
	public static final int CPU_WIN = 4;
	public static final int USER_WIN = -4;
	public static final int INFINITY = 100;
	public static final int MAX_DEPTH = 3;
	
	// empty constructor
	public CPUPlayer() { }
	
	// minimaxing algorithm as seen in the textbook
	private ScoreAndMove minimax(GameBoard board, int player, int maxDepth, int currentDepth) {
		// check if done recursion
		if (board.isGameOver() || currentDepth == maxDepth) {
			System.out.println("FINISH RECURSION");
			return new ScoreAndMove(board.evaluate(player), -1);
		}
		
		// otherwise bubble up values from below
		ScoreAndMove best = new ScoreAndMove();
		best.move = -1;
		if (board.getCurrentPlayer() == player) {
			best.score = -INFINITY;
		} else {
			best.score = INFINITY;
		}
		
		// go through each move
		GameBoard newBoard;
		ScoreAndMove current = new ScoreAndMove();
		int[] availableMoves = board.getMoves();
		
		for (int move : availableMoves) {
			
			newBoard = board.makeMove(move);
			
			// recurse
			current = minimax(newBoard, player, maxDepth, currentDepth + 1);
			
			// update the best score
			if (board.getCurrentPlayer() == player) {
				if (current.score > best.score) {
					best.score = current.score;
					best.move = move;
				}
			} else {
				if (current.score < best.score) {
					best.score = current.score;
					best.move = move;
				} // end if
			} // end else
		} // end for
		
		// return best score and move
		return best;
	} // end of minimax(GameBoard, int, int, int)
	
	// returns the position to drop the CPU piece
	public int getMove(int[] pieces) {
		ScoreAndMove sam = minimax(new GameBoard(pieces, GameBoard.CPU_PLAYER), GameBoard.CPU_PLAYER, MAX_DEPTH, 0);
		return sam.move % Board.COLS;
	} // end of getMove(int[])
} // end of class CPUPlayer
