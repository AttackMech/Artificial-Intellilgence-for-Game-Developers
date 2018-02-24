/*
 * Graph.java
 * 
 * This class is the exact same as that used in part 1.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.util.Vector;

public class Graph {
	Vector<Connection> allCnx;
	
	public Graph(Vector<Connection> cnxList) {
		allCnx = (Vector<Connection>) cnxList.clone();
			System.out.println("creating graph with " + allCnx.size() + " connections");
	} // end of constructor
	
	// returns the list of connections from the passed node
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
