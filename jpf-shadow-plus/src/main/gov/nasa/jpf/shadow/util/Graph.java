package gov.nasa.jpf.shadow.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Unit;
import soot.tagkit.LineNumberTag;
import soot.toolkits.graph.UnitGraph;

public class Graph {
	
	private UnitGraph unitGraph;
	private List<Node> nodes;
	private Map<Unit, Node> unit2node;
	private Map<Integer, Node> lineNumberMapping;
	
	private Map<Node, Collection<Node>> nodeSuccessors;
	private Map<Node, Collection<Node>> nodePredecessors;
	
	public Graph(UnitGraph unitGraph) {
		this.unitGraph = unitGraph;
		
		nodes = new ArrayList<>();
		unit2node = new HashMap<>();
		lineNumberMapping = new HashMap<>();
		nodeSuccessors = new HashMap<>();
		nodePredecessors = new HashMap<>();
		
		// Unit to Node mapping.
		for (Iterator<Unit> i = unitGraph.iterator(); i.hasNext();){
			Unit unit = (Unit) i.next();
			if(unit.getTag("LineNumberTag")!=null) { //edited for new version soot
				int lineNumber = ((LineNumberTag) unit.getTag("LineNumberTag")).getLineNumber();
				
				Node node;
				if (lineNumberMapping.containsKey(lineNumber)) {
					node = lineNumberMapping.get(lineNumber);
					node.addUnit(unit);
				} else {
					node = new Node(unit, lineNumber);
					nodes.add(node);
					lineNumberMapping.put(lineNumber, node);
				}
				unit2node.put(unit, node);
			}
		}
		
		// Build successor and predecessor structures.
		for (Node currentNode : nodes) {
			if (!nodeSuccessors.containsKey(currentNode)) {
				nodeSuccessors.put(currentNode, new HashSet<>());
			}
			if (!nodePredecessors.containsKey(currentNode)) {
				nodePredecessors.put(currentNode, new HashSet<>());
			}
			for (Unit unit : unitGraph.getSuccsOf(currentNode.getLastUnit())) {
				if(unit.getTag("LineNumberTag") != null) {
					Node succesorNode = unit2node.get(unit);
					nodeSuccessors.get(currentNode).add(succesorNode);
					
					if (!nodePredecessors.containsKey(succesorNode)) {
						nodePredecessors.put(succesorNode, new HashSet<>());
					}
					nodePredecessors.get(succesorNode).add(currentNode);
				}
				
			}
			for (Unit unit : unitGraph.getPredsOf(currentNode.getFirstUnit())) {
				if(unit.getTag("LineNumberTag") != null) {
					Node predecessorNode = unit2node.get(unit);
					nodePredecessors.get(currentNode).add(predecessorNode);
					
					if (!nodeSuccessors.containsKey(predecessorNode)) {
						nodeSuccessors.put(predecessorNode, new HashSet<>());
					}
					nodeSuccessors.get(predecessorNode).add(currentNode);
				}	
			}
		}
	}
	
	public Node getNode(Unit unit) {
		return unit2node.get(unit);
	}
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	public Collection<Node> getSuccessors(Node node) {
		return nodeSuccessors.get(node);
	}
	
	public Collection<Node> getPredecessors(Node node) {
		return nodePredecessors.get(node);
	}
	
	public Node getNodeByLineNumber(int lineNumber) {
		
			return lineNumberMapping.get(lineNumber);
		
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Node node : nodes) {
			for (Node successor : getSuccessors(node)) {
				s.append("\n");
				s.append(node.lineNumber);
				s.append("->");
				s.append(successor.lineNumber);
			}
		}
		return s.toString();
	}
	
}