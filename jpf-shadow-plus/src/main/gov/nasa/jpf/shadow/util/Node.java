package gov.nasa.jpf.shadow.util;

import java.util.ArrayList;
import java.util.List;

import soot.Unit;

public class Node {
	
	List<Unit> units;
	Integer distance;
	int lineNumber;
	
	public Node(Unit unit, int lineNumber, int distance) {
		units = new ArrayList<>();
		units.add(unit);
		this.lineNumber = lineNumber;
		this.distance = distance;
	}
	
	public Node(Unit unit, int lineNumber) {
		units = new ArrayList<>();
		units.add(unit);
		this.lineNumber = lineNumber;
	}
	
	
	public void setInitDistance(Integer distance) {
		this.distance = distance;
	}
	
	public Integer getDistance() {
		return this.distance;
	}
	
	public int getLineNumber() {
		return this.lineNumber;
	}
	
	public void addUnit(Unit unit) {
		this.units.add(unit);
	}
	
	public Unit getFirstUnit() {
		return this.units.get(0);
	}
	
	public Unit getLastUnit() {
		return this.units.get(this.units.size() - 1);
	}
	
}
