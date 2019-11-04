package gov.nasa.jpf.shadow.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import soot.tagkit.LineNumberTag;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.UnitGraph;
import sootconnection.SootConnector;

public class DistanceAnalyzerPlus {
	private String packageName;
	private String className;
	private String classPath;
	private String[] methodNames;

	private Map<Integer, Integer> ifBranch;
	private Map<Integer, List<Integer>> distanceMetrics;
	private Map<Integer, Integer> nextLine;
	
	private boolean debugSourceLines = true;
	private boolean debugTargetLines = true;
	private boolean debugAnalyzer = true;
	
	//List of methods that contain neither a change() method nor a call to a method that can reach one
	//(as a result, we have to explore all paths in them)
	private List<String> intermediateMethods = new ArrayList<String>();
	
	private static List<String> excludePackagesList = new ArrayList<>();
	static {
		excludePackagesList.add("gov.nasa.jpf.*");
		excludePackagesList.add("java.*");
	}

	
	public DistanceAnalyzerPlus(Config conf){
		String targetClass = conf.getProperty("target");
		this.packageName = targetClass.substring(0, targetClass.lastIndexOf("."));
		this.className = targetClass.substring(targetClass.lastIndexOf(".")+1, targetClass.length());
		this.ifBranch = new HashMap<>();
		this.distanceMetrics = new HashMap<>();
		this.nextLine = new HashMap<>();
		
		
		//Methods of class to analyze
		//TODO: automatically determine the methodNames to analyze (or find a better way than simply reading them from jpf configuration file)
		String[] allMethods = conf.getStringArray("shadow.allMethods");
		assert(allMethods!=null):"You have to set the property shadow.allMethods=method1,method2,... in the .jpf configuration file";
		this.methodNames = new String[allMethods.length];
		
		int index = 0;
		for (String method : allMethods){
			this.methodNames[index] = method;
			index++;
		}	
		
		//TODO make this machine independent
		this.classPath = "/Users/minxing/Documents/eclipse-workspace/jpf-shadow-plus/build/tests/";
		//this.classPath = "/Users/Lam/Studium/Projects/jpf-shadow/code/jpf-shadow/build/tests";
		//	this.classPath = "/Users/yannic/repositories/hub/shadow-spf/code/jpf-shadow/build/tests";
		
		this.computeDistanceMetrics();
	}
	
	public void computeDistanceMetrics(){
		SootConnector sc = SootConnector.getInstance(this.packageName, this.className, this.classPath, excludePackagesList);
		
		//List containing pairs <method name, target unit>, where target unit leads to a change() for the method
		List<Pair<String,Unit>> allTargetUnits = this.getTargetUnits(sc);
				
		for(String method: this.methodNames){
			UnitGraph unitGraph = sc.getCFGForMethodName(method);
			Iterator<Unit> u = unitGraph.iterator();
			
			List<Unit> targetUnits = new ArrayList<Unit>();
			List<Unit> branchUnits = new ArrayList<Unit>();
			
			//Determine relevant target units
			for(Pair<String,Unit> targetUnit: allTargetUnits){
				String methodName = targetUnit._1;
				if(methodName.equals(method)){
					Unit target = targetUnit._2;
					targetUnits.add(target);
				}
			}
			
			//No reachable change()-method, we have to execute all paths in this method
			if(targetUnits.size()==0){
				this.intermediateMethods.add(method);
				continue;
			}
			
			//Old implementation
			//change
			Unit next;
		
			//int lineLimit = 0;
			//Stack<Integer> elseLineNumber = new Stack<Integer>();
			//change
			
			if (u.hasNext()){
				next = u.next();
			
			while(u.hasNext()){   // need to fix
				Unit unit = next;
				next = u.next();
				int nextLineNumber = 0;
				if (unit.branches()){
					assert(unit.getTag("LineNumberTag") != null):"LineNumberTag doesn't exist";
					int ifLineNumber = ((LineNumberTag) unit.getTag("LineNumberTag")).getLineNumber();
					
					List<Unit> elseUnits = unitGraph.getSuccsOf(unit);
					Unit ifUnit = elseUnits.get(0);
					int lineNumber = ((LineNumberTag) ifUnit.getTag("LineNumberTag")).getLineNumber();
					//branchLineNumbers.add(lineNumber);
					branchUnits.add(ifUnit);
					if(debugSourceLines)System.out.println("this: " + ifLineNumber);
					if(debugSourceLines)System.out.println("next unit: " + lineNumber);
					
					
					nextLineNumber = ((LineNumberTag) next.getTag("LineNumberTag")).getLineNumber();
					
					
					if(nextLineNumber > ifLineNumber){
						if(debugSourceLines)System.out.println("nextLineNumber "+nextLineNumber+" > ifLineNumber "+ ifLineNumber);
						if(this.nextLine.get(ifLineNumber)==null){
							this.nextLine.put(ifLineNumber, nextLineNumber);
							if(debugSourceLines)System.out.println("nextLineNumber: (null)" + nextLineNumber);
						}else{
							int before = this.nextLine.get(ifLineNumber);
							if(nextLineNumber <= before && nextLineNumber> ifLineNumber){
								this.nextLine.put(ifLineNumber, nextLineNumber);
								if(debugSourceLines)System.out.println("next: (smaller)" + nextLineNumber);
							}
						}
					}
					
					//need to change
					if(lineNumber>ifLineNumber){
						if(debugSourceLines)System.out.println("lineNumber>ifLineNumber");
						if (this.nextLine.get(ifLineNumber)==null){
						ifBranch.put(ifLineNumber, lineNumber);
						if(debugSourceLines)System.out.println("if-> branch: "+ ifLineNumber + ": "+lineNumber);
						if(debugSourceLines)System.out.println("nextLine: null");
						}else{
							if(lineNumber<= nextLine.get(ifLineNumber)){
								ifBranch.put(ifLineNumber, lineNumber);
								if(debugSourceLines)System.out.println("if-> branch: "+ ifLineNumber + ": "+lineNumber);
								if(debugSourceLines)System.out.println("nextLine: not null");
							}
						}
					}
					
					if(elseUnits.size()>=2){
						int elseSize = elseUnits.size() -1;   //modified
						int i = 1;                            //modified
						while(elseSize > 0) {                 //modified  
							//handle if --- else if --- else -- or switch
							Unit elseUnit = elseUnits.get(i);
							assert(elseUnit.getTag("LineNumberTag")!=null):"LineNumerTag doesn't exist.";
							lineNumber = ((LineNumberTag) elseUnit.getTag("LineNumberTag")).getLineNumber();
							//branchLineNumbers.add(lineNumber);
							branchUnits.add(elseUnit);
							elseSize--;
							i ++;
						}
						/* old version
						//handle if --- else ---
						Unit elseUnit = elseUnits.get(1);
						lineNumber = ((LineNumberTag) elseUnit.getTag("LineNumberTag")).getLineNumber();
						//branchLineNumbers.add(lineNumber);
						branchUnits.add(elseUnit);
						*/
							
					}
					if(debugSourceLines)System.out.println("___________");
					
				}		
			}
			}
			if(debugAnalyzer) {
				System.out.println("Ready to calculate shortest Path");
			}
			//collect information of reachability into distanceMetrics
			shortestPathInCFG(branchUnits, targetUnits, unitGraph);
		}
		
		//print distanceMetrics
		//Note: this used to print the distance for ALL if-insn
		for (Integer lineNumber : distanceMetrics.keySet()){
			List<Integer> list = this.distanceMetrics.get(lineNumber);
			System.out.print(lineNumber+": ");
			for (Integer reachable: list){
				System.out.print(reachable+" ");
			}
			System.out.println();
		}
		
		
	}
	
	private List<Pair<String,Unit>> getTargetUnits(SootConnector sc){
		//List containing pairs of <method name, target unit>, where target units are units inside 
		//the method that are able to reach a change (either by directly calling a change() or another method
		//that is able to reach a change())
		List<Pair<String,Unit>> targetUnits = new ArrayList<Pair<String,Unit>>();
		
		//List containing method calls as pairs of (calling method, called method).
		//This is used in order to determine whether a method can reach a change() by calling
		//another method (transitive reachability)
		List<MethodCallPair> methodCalls = new ArrayList<MethodCallPair>();

		//Determine all method calls and initial target lines (methods that already contain a change() method)
		for(String methodName: methodNames){
			Iterator<Edge> i = sc.getIteratorOnCallsOutOfMethodName(methodName);
			while(i.hasNext()){
				Edge e = i.next();
				MethodOrMethodContext me = e.getTgt();
				SootMethod calledMethod = me.method();
				String signature = calledMethod.getSubSignature();
								
				//Method call is a change() method
				if(signature.equals("int change(int,int)")
						||signature.equals("boolean change(boolean,boolean)")
						||signature.equals("double change(double,double)")
						||signature.equals("float change(float,float)")
						||signature.equals("long change(long,long)")
						||signature.equals("boolean execute(boolean)")){
					
					Pair<String, Unit> targetCall = new Pair<String, Unit>(methodName, e.srcUnit());				
					targetUnits.add(targetCall);
					if(debugTargetLines) System.out.println("Calls: "+methodName+" is calling a change()-method");
				}
				//Method call besides change()
				else{
					MethodCallPair methodCall = new MethodCallPair(methodName, calledMethod.getName(), e.srcUnit());
					methodCalls.add(methodCall);
					if(debugTargetLines) System.out.println("Calls: "+methodName+" is calling the method "+calledMethod.getName());
				}	
			}
		}
		
		/* Now compute transitive closure of <method name, target unit> pairs where target unit is either
		 * a call to a change() method or to a method that can reach a change().
		 * 
		 * The list allTargetUnits always contains methods that are able to reach a change().
		 * If we have a pair (calling method, called method) where the called method can
		 * reach a change(), the calling method can also reach a change() and we can add it to the targetUnits.
		 * 
		 * For example, the first iteration adds methods that call other methods that contain a change().
		 * (Since the list targetUnits initially contains methods that have a change()-method)
		 * 
		 * The second iteration adds the methods to the list that call the methods added in the previous iteration
		 * as an intermediate step to reach a change().
		 * 
		 * This is repeated until no more pairs are added to targetUnits.
		 */  
		boolean listUpdated = true;
				
		//Indices of methodCalls we want to remove from the methodCalls list before the next iteration
		//If we remove the item while still iterating over the list a ConcurrentModificationException will be thrown 
		List<MethodCallPair> processedMethodCalls = new ArrayList<MethodCallPair>();
		
		//New targetUnits to be added to the targetCalls list (avoid ConcurrentModificationException)
		List<Pair<String,Unit>> newTargetUnits = new ArrayList<Pair<String,Unit>>();
		
		while(listUpdated){
			listUpdated = false;	
			for(MethodCallPair m : methodCalls){
				String callingMethod = m.getCallingMethod();
				String calledMethod = m.getCalledMethod();
				Unit callUnit = m.getTargetUnit();
				
				//Iterate over all pairs that can reach a change()
				for(Pair<String,Unit> t : targetUnits){
					String reachingMethod = t._1;
					if(calledMethod.equals(reachingMethod)){
						//The calling method is able to reach a change() through the called method
						newTargetUnits.add(new Pair<String,Unit>(callingMethod, callUnit));
						processedMethodCalls.add(m);
						listUpdated = true;
						if(debugTargetLines)System.out.println("Closure: "+m.getCallingMethod()+" is able to reach a change via "+reachingMethod);
					}
				}
			}
			
			//Add new targetPairs
			targetUnits.addAll(newTargetUnits);
			newTargetUnits.clear();
			
			//Remove processed methodCalls
			methodCalls.removeAll(processedMethodCalls);
			processedMethodCalls.clear();
		}
		
		return targetUnits;
	}
	
	
	//calculate the distances between branches and targets (in the same method)
	private void shortestPathInCFG(List<Unit> branchUnits, List<Unit> targetUnits, UnitGraph unitGraph) {					
		//Extract graph. Reversed search, i.e. search from target node backward in the graph.
		Graph graph = new Graph(unitGraph);
		
		Set<Node> sourceNodes = new HashSet<Node>();
		Set<Node> targetNodes = new HashSet<Node>();
						
		// Get list of nodes for source(branch) units.
		for (Unit source : branchUnits) {
				if(source!=null) {
					sourceNodes.add(graph.getNode(source));
				}	
		}
		
		if(debugAnalyzer) {
			System.out.println("sourceNode size:"+ sourceNodes.size());
		}
						
		// Get list of nodes for target units.
		for (Unit target : targetUnits){
				if(target !=null) {
					targetNodes.add(graph.getNode(target));
				}		
		}
		if(debugAnalyzer) {
			System.out.println("targeNode size:"+ targetNodes.size());
		}			
		//using sourceNode(if-branches) as start node
		for(Node sourceNode: sourceNodes){
		
			new DijkstraSearch().searchBackward(graph, sourceNode, targetNodes);
			List<Integer> distanceList = new ArrayList<Integer> ();
								
		/*	for (Node target: targetNodes){
				if(graph.getNodeByLineNumber(target.getLineNumber()).getDistance()!=Integer.MAX_VALUE
						&& sourceNode.getLineNumber() <= target.getLineNumber()){
						distanceList.add(target.getLineNumber());
				}
			}*/
			
			this.distanceMetrics.put(sourceNode.getLineNumber(), distanceList);
		}
		if(debugAnalyzer) {
			System.out.println("Ready to trace back from target nodes");
		}
		//using targetNode(change()-method) as start node		
		
		for(Node targetNode: targetNodes){
				new DijkstraSearch().searchBackward(graph, targetNode, sourceNodes);
							
				for (Node source: sourceNodes){
						if(graph.getNodeByLineNumber(source.getLineNumber()).getDistance()!=Integer.MAX_VALUE
								/*|| source.getLineNumber() > targetNode.lineNumber      //modified*/
								/*&& source.getLineNumber() <= targetNode.lineNumber*/ ){
								List<Integer> distanceList = this.distanceMetrics.get(source.getLineNumber());
								distanceList.add(targetNode.getLineNumber());
								//this.distanceMetrics.remove(source.getLineNumber());
								this.distanceMetrics.put(source.getLineNumber(), distanceList);
						}
				}
		}
		if(debugAnalyzer) {
			System.out.println("DijkstraSearch() completed.");
		}
	
	}
	
	//given the line number of an if-branch (or else-branch), test if it can reach any change()-method
	public boolean checkReachability(Integer bLineNumber, String methodName){
		boolean reach = false;
		int realLineNumber = bLineNumber;
		
		if(this.intermediateMethods.contains(methodName)){
			return true;
		}
		
		if(this.ifBranch.containsKey(realLineNumber)){
			if (this.distanceMetrics.get(realLineNumber)!=null){   // change-annotation is within if-condition
				if(this.distanceMetrics.get(realLineNumber).contains(realLineNumber)){
					reach = true;
				}
			}
			
			if(this.ifBranch.get(realLineNumber)!=null){
				realLineNumber = ifBranch.get(realLineNumber);
			}
		}
			
		if(this.distanceMetrics.get(realLineNumber)!=null){
			if(!this.distanceMetrics.get(realLineNumber).isEmpty())
				reach = true;
		}
			
		return reach;
	}
}
