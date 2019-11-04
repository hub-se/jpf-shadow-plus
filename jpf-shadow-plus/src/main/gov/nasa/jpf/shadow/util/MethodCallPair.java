package gov.nasa.jpf.shadow.util;
import soot.Unit;

public class MethodCallPair {
	private String callingMethod;
	private String calledMethod;
	private Unit targetUnit;
	
	public MethodCallPair(String callingMethod, String calledMethod, Unit targetUnit){
		this.callingMethod = callingMethod;
		this.calledMethod = calledMethod;
		this.targetUnit = targetUnit;
	}
	
	public String getCallingMethod(){
		return this.callingMethod;
	}
	
	public String getCalledMethod(){
		return this.calledMethod;
	}
	
	public Unit getTargetUnit(){
		return this.targetUnit;
	}
	
}
