package gov.nasa.jpf.shadow;

import gov.nasa.jpf.Config;
//import gov.nasa.jpf.shadow.util.DistanceAnalyzer;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;


public class ShadowInstructionFactory extends SymbolicInstructionFactory {
	static public boolean directedSymEx = false; //Use directed symbolic execution strategy
	static public boolean debugCG = false; //Print choice generator information during execution
	static public boolean debugDirectedSymEx = false; //Directed symbolic execution
	static public boolean debugSolver = false; //Print constraint solver warnings (e.g. timeouts)
	static public boolean debugChangeBoolean = false; //Debug if(change(boolean,boolean)) stmts
	
	public ShadowInstructionFactory(Config conf){
		super(conf);
		
		String debugChoiceGenerator  = conf.getProperty("debug.choiceGenerators");
		if (debugChoiceGenerator != null && debugChoiceGenerator.equals("true")) {
			debugCG = true;
			System.out.println("debug.choiceGenerators=true");
		}
		
		String debugConstraintSolver  = conf.getProperty("debug.solver");
		if (debugConstraintSolver != null && debugConstraintSolver.equals("true")) {
			debugSolver = true;
			System.out.println("debug.solver=true");
		}
		
		String directedSym = conf.getProperty("shadow.directedSearch");
		if (directedSym != null && directedSym.equals("true")) {
			System.out.println("shadow.directedSearch=true");
			directedSymEx = true;
		}
		
		String debugDirectedSE = conf.getProperty("debug.directedSearch");
		if (debugDirectedSE != null && debugDirectedSE.equals("true")) {
			System.out.println("debug.directedSearch=true");
			debugDirectedSymEx = true;
		}
		
		String changeBoolean  = conf.getProperty("debug.changeBoolean");
		if (changeBoolean != null && changeBoolean.equals("true")) {
			debugChangeBoolean = true;
			System.out.println("debug.changeBoolean=true");
		}
		
		System.out.println("Running jpf-shadow-plus...");
	}
}
