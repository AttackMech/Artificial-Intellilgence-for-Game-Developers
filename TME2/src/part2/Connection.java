/*
 * Connection.java
 * 
 * This class is the exact same as is used in part 1.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

public class Connection {
	public int id;
	public int cost;
	public int fromNode;
	public int toNode;
	
	public Connection(int id, int cost, int fromNode, int toNode) {
		this.id = id;
		this.cost = cost;
		this.fromNode = fromNode;
		this.toNode = toNode;
	} // end of constructor
	
	// returns the non-negative cost of the connection
	public int getCost() {
		return cost;
	} // end of getCost()
	
	// returns the node that the connection came from
	public int getFromNode() {
		return fromNode;
	} // end of getFromNode()
	
	// returns the node that the connection leads to
	public int getToNode() {
		return toNode;
	} // end of getToNode()
} // end of class Connection