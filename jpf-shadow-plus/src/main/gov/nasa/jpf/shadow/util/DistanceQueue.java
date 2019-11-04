package gov.nasa.jpf.shadow.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class DistanceQueue {
	
//	private FibonacciHeap<Node> heap;
//	private Map<Node, FibonacciHeapNode> mapping;
	private List<Node> heap;
	private static Comparator<Node> comparator = new Comparator<Node>() {

		@Override
		public int compare(Node node1, Node node2) {
			
			if (node1.distance != null && node2.distance != null) {
				return node1.distance - node2.distance;
			} else if (node1.distance == null && node2.distance != null) {
				return 1;
			} else if (node1.distance != null && node2.distance == null) {
				return -1;
			} else {
				return 0;
			}
			
			
		}
	};
	
	public DistanceQueue() {
		
//		this.heap = new FibonacciHeap<Node>(new Comparator<Node>() {
//
//			@Override
//			public int compare(Node node1, Node node2) {
//				return node1.distance - node2.distance;
//			}
//			
//		});
//		mapping = new HashMap<>();
		
		this.heap = new ArrayList<>();
		
		
	}
	
	public boolean isEmpty() {
		return this.heap.isEmpty();
	}
	
	public void add(Node node) {
//		FibonacciHeapNode heapNode = this.heap.insert(node);
//		mapping.put(node, heapNode);
		heap.add(node);
		
		Collections.sort(heap, comparator);
	}
	
	public Node extractMinimum() {
		return heap.remove(0);
	}
	
	public void update(Node node) {
		Collections.sort(heap, comparator);
	}
	
}
