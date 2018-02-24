/*
 * ScoreAndMove.java
 * 
 * This simple class is used to store integer values representing the score and move, allowing the minimaxing algorithm to
 * return multiple values at once.  Variables are made public for direct access.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 20, 2016
 * 
 */

package part1;

public class ScoreAndMove {
	public int score;
	public int move;
	
	// empty constructor
	public ScoreAndMove() {}
	
	// copy constructor
	public ScoreAndMove(int score, int move) {
		this.score = score;
		this.move = move;
	}
} // end of class ScoreAndMove
