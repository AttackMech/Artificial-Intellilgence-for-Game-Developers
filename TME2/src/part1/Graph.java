/*
 * Graph.java
 * 
 * This class is used to represent the connection graph between various game tiles.  It is the same as the class described
 * in the textbook for this course.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 8, 2016
 * 
 */

package part1;

import java.util.Vector;

public class Graph {
	Vector<Connection> allCnx;
	
	public Graph(Vector<Connection> cnxList) {
		allCnx = (Vector<Connection>) cnxList.clone();
			System.out.println("creating graph with " + allCnx.size() + " connections");
	} // end of constructor
	
	// returns an array of all valid connections between the pased tile and other game tiles
	public Connection[] getConnections(int fromNode) {
		Vector<Connection> cnxList = new Vector<Connection>();
		for (Connection cnx : allCnx) {
			if (cnx.fromNode == fromNode) {
				cnxList.addElement(cnx);
			}
		}
		return cnxList.toArray(new Connection[cnxList.size()]);
	} // end of getConnections(int)
	
	// returns the size of the connection list
	public int getCnxSize() { return allCnx.size(); }
} // end of class Graph
