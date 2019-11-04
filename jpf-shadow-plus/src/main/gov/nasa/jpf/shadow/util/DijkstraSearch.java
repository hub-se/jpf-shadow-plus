package gov.nasa.jpf.shadow.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DijkstraSearch {

	public void searchBackward(Graph g, Node startNode, Collection<Node> targetNodes) {
		Map<Node, Node> prev = new HashMap<>();
		DistanceQueue Q = new DistanceQueue();

		for (Node node : g.getNodes()) {
			node.setInitDistance(node.equals(startNode) ? 0 : Integer.MAX_VALUE);
			prev.put(node, null); // Predecessor of v
			Q.add(node);
			/*
			 * if(node.equals(startNode)){ node.setInitDistance(0); Q.add(node); } else{
			 * node.setInitDistance(Integer.MAX_VALUE); }
			 */
		}

		int targetCounter = 0;
		while (!Q.isEmpty()) {
			Node u = Q.extractMinimum();

			if (targetNodes.contains(u)) {
				// Search for targetNode finished.
				targetCounter++;
				if (targetCounter == targetNodes.size()) {
					break;
				}
			}

			if (u.distance == Integer.MAX_VALUE) {
				// i.e. u ist not reachable and then no other remaining node
				break;
			}

			if (u.distance < Integer.MAX_VALUE) {
				int alt = u.distance + 1;
				for (Node v : g.getPredecessors(u)) {
					if (v.distance == Integer.MAX_VALUE || alt < v.distance) {
						v.distance = alt;
						prev.put(v, u);
						Q.update(v);

					}

				}
			}

		}
	}

}
