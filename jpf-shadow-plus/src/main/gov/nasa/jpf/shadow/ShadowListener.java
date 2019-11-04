package gov.nasa.jpf.shadow;


import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
/*
import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.jvm.bytecode.DRETURN;
import gov.nasa.jpf.jvm.bytecode.FRETURN;
import gov.nasa.jpf.jvm.bytecode.IRETURN;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.LRETURN;
import gov.nasa.jpf.jvm.bytecode.JVMReturnInstruction;*/
import gov.nasa.jpf.jvm.bytecode.GOTO;
import gov.nasa.jpf.jvm.bytecode.ICONST;
import gov.nasa.jpf.jvm.bytecode.IF_ICMPEQ;
import gov.nasa.jpf.jvm.bytecode.IF_ICMPGE;
import gov.nasa.jpf.jvm.bytecode.IF_ICMPGT;
import gov.nasa.jpf.jvm.bytecode.IF_ICMPLE;
import gov.nasa.jpf.jvm.bytecode.IF_ICMPLT;
import gov.nasa.jpf.jvm.bytecode.IF_ICMPNE;
import gov.nasa.jpf.jvm.bytecode.IfInstruction;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.JVMReturnInstruction;
import gov.nasa.jpf.jvm.bytecode.RETURN;
import gov.nasa.jpf.shadow.util.DistanceAnalyzer;
import gov.nasa.jpf.symbc.SymbolicListener;
//import gov.nasa.jpf.shadow.util.DistanceAnalyzer;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.DiffExpression;
import gov.nasa.jpf.symbc.numeric.ExecExpression;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.PathCondition.Diff;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.ShadowPCChoiceGenerator;
import gov.nasa.jpf.util.MethodSpec;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;
import gov.nasa.jpf.vm.VM;

//public class ShadowListener extends PropertyListenerAdapter{ 
public class ShadowListener extends SymbolicListener {
	
	//Method specifications for the change()-methods
	MethodSpec[] changeMethods = new MethodSpec[5]; 
	
	//Method specification for the execute(OLD/NEW) annotation
	MethodSpec execute = MethodSpec.createMethodSpec("*.execute(boolean)");
	
	//Instruction at the end of an if(execute(OLD/NEW))-block, both version are executed again after this insn
	Instruction endOfBlock; 
	
	//Reachability information for directed symbolic execution
	DistanceAnalyzer distanceAnalyzer;
	
	public ShadowListener(Config conf, JPF jpf) {
		super(conf,jpf);
		
		//Method specifications for each change(type,type)-method
		changeMethods[0] = MethodSpec.createMethodSpec("*.change(boolean,boolean)");
		changeMethods[1] = MethodSpec.createMethodSpec("*.change(int,int)");
		changeMethods[2] = MethodSpec.createMethodSpec("*.change(float,float)");
		changeMethods[3] = MethodSpec.createMethodSpec("*.change(double, double)");
		changeMethods[4] = MethodSpec.createMethodSpec("*.change(long,long)");
		
		if(ShadowInstructionFactory.directedSymEx){
			distanceAnalyzer = new DistanceAnalyzer(conf);
		}
	}
	
	//Choice generator debugging
	@Override
	public void choiceGeneratorAdvanced (VM vm, ChoiceGenerator<?> currentCG) {
		if(ShadowInstructionFactory.debugCG){
			if(currentCG instanceof ShadowPCChoiceGenerator){
				System.out.println("Shadow CG at line "+((ShadowPCChoiceGenerator)currentCG).getInsn().getLineNumber()+" advanced, choice: "+currentCG.getNextChoice());
			}
			else if(currentCG instanceof PCChoiceGenerator){
				System.out.println(((PCChoiceGenerator) currentCG).getExecutionMode()+ ": PCCG ("+currentCG.getInsn().getMnemonic()+") at line "+((PCChoiceGenerator)currentCG).getInsn().getLineNumber()+" advanced, choice: "+currentCG.getNextChoice());
			}
		}
	}
	
	@Override
	public void choiceGeneratorSet (VM vm, ChoiceGenerator<?> newCG) {
		if(ShadowInstructionFactory.debugCG){
			if(newCG instanceof ShadowPCChoiceGenerator){
				endOfBlock = ((ShadowPCChoiceGenerator) newCG).getEndInstruction();
				System.out.println("Registed Shadow CG with end of block instruction: "+endOfBlock.getMnemonic()
				+" during execution of the method "+endOfBlock.getMethodInfo().getName()+" at line "+((ShadowPCChoiceGenerator)newCG).getInsn().getLineNumber());
			}
			else if(newCG instanceof PCChoiceGenerator){
				System.out.println("Registered PCCG ("+newCG.getInsn().getMnemonic()+") in execution mode: "+((PCChoiceGenerator)newCG).getExecutionMode()+" at line "+((PCChoiceGenerator)newCG).getInsn().getLineNumber());
			}
			else{
				System.out.println("Registered ChoiceGenerator of class: "+newCG.getClass());
			}
		}
	}
	
	@Override
	public void choiceGeneratorProcessed (VM vm, ChoiceGenerator<?> processedCG) {
		if(ShadowInstructionFactory.debugCG){
			if(processedCG instanceof ShadowPCChoiceGenerator){
				System.out.println("Processed Shadow CG from line "+((PCChoiceGenerator)processedCG).getInsn().getLineNumber());
			}
			else if(processedCG instanceof PCChoiceGenerator){
				System.out.println("Processed PC CG from line "+((PCChoiceGenerator)processedCG).getInsn().getLineNumber());
			}
		 }
	 }
	
	
	@Override
	public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
		Instruction insn = instructionToExecute;
		ThreadInfo ti = currentThread;		
		//System.out.println("Executing insn: "+insn.getMnemonic()+" on line "+insn.getLineNumber());
		//The previous insn set the reset flag, reset execution mode now
		
		if(!BytecodeUtils.resetInstructions.isEmpty()){
			boolean reset = false;
			Execute currentExecutionMode = ti.getExecutionMode();
			for(Instruction i : BytecodeUtils.resetInstructions){
				if(insn.equals(i)){
					reset = true;
					ti.setExecutionMode(Execute.BOTH);
					if(ShadowInstructionFactory.debugChangeBoolean) System.out.println("Reset execution mode from " + currentExecutionMode + " to BOTH before executing "+insn.getMnemonic());
				}
			}
			if(reset) BytecodeUtils.resetInstructions.clear();
		}
		
		//Get the current PCChoiceGenerator and the current PathCondition
		ChoiceGenerator<?> curCg = ti.getVM().getSystemState().getChoiceGenerator();
		PCChoiceGenerator pcCg;
		
		if(curCg instanceof PCChoiceGenerator){
			pcCg = (PCChoiceGenerator) curCg;
		}
		else{
			pcCg = curCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class); //also returns ShadowPCCg
		}
		
		//The current pathcondition
		PathCondition  pc = null;
		
		if(pcCg != null){
			pc = pcCg.getCurrentPC();
			
			/*
			 * When executing if-instructions, we have to take care of the following cases:
			 * 1. During the exploration of a diffpath, we have to handle the special case if(change(boolean,boolean)) 
			 *    (specifically, skip the evaluation of the first (old) boolean expression since it affects the path condition)
			 * 2. If we enabled the directed symbolic execution strategy, we have to set the choices of the PCChoiceGenerator accordingly. 
			 */
			if(insn instanceof IfInstruction){
				if(pc.isDiffPC()){
					checkChangeDuringDiff(vm, ti, (IfInstruction)insn);
				}
				
				/*
				 * Handle directed symbolic execution strategy (only necessary if we are currently not exploring a diffpath):
				 * 1. After the if-instruction has registered the choice generator (ti.isFirstStepInsn()), we want to adjust the choices 
				 * 	  based on the reachability of change()-methods
				 * 2. We only want to do this once (pcCg.choicesSet), otherwise this would result in an infinite-loop
				 */
				else if(ShadowInstructionFactory.directedSymEx && ti.isFirstStepInsn() && !pcCg.choicesSet()){
					//The current (newly registered) cg has no pc yet, look up the previous one
					PCChoiceGenerator prevCg = pcCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
					
					//Only modify choices if we haven't executed a change()-method along the path
					if(!prevCg.getCurrentPC().containsDiffExpr()){
						setNextPaths((IfInstruction)insn,pcCg);
					}
				}
			}
		}
		
		
		/* 
		 * Handle if(execute(OLD/NEW)) blocks.
		 * ShadowPCChoiceGenerators are registered whenever we execute an if(execute(OLD/NEW))-block.
		 * In this block, only the old (or new, respectively) version is being executed.
		 * The endOfBlock instruction marks the first instruction at the end of the if(execute(...))-block.
		 * Starting from this instruction, both program versions are being executed again.
		 */
		
		//Get the last registered ShadowPCChoiceGenerator
		ShadowPCChoiceGenerator shadowCg;
		if(curCg instanceof ShadowPCChoiceGenerator){
			shadowCg = (ShadowPCChoiceGenerator)curCg;
		}
		else{
			shadowCg = curCg.getPreviousChoiceGeneratorOfType(ShadowPCChoiceGenerator.class);
		}
		
		if(shadowCg != null){
			endOfBlock = ((ShadowPCChoiceGenerator) shadowCg).getEndInstruction();
			
			//We are leaving an if(execute(OLD/NEW))-block, execute both versions from here on
			//We also have to register a cg so that the current cg is consistent with the execution mode
			//(necessary for BytecodeUtils.getIfInsnExecutionMode())
			if(insn.equals(endOfBlock)){
				if(!ti.isFirstStepInsn()){
					ti.setExecutionMode(Execute.BOTH);
					PCChoiceGenerator nextCg = new PCChoiceGenerator(1);
					nextCg.setOffset(insn.getPosition());
					nextCg.setMethodName(insn.getMethodInfo().getFullName());
					nextCg.setExecutionMode(Execute.BOTH);
					nextCg.setPC(pc, 0);
					ti.getVM().getSystemState().setNextChoiceGenerator(nextCg);
					ti.skipInstruction(insn);
				}
				return;
			}
		}
		

		
		/*
		 * When executing a return instruction, we have to handle the following cases:
		 * 1. A special method has been executed, i.e. a change() or execute() method.
		 *    - change()-methods are used to annotate the expressions that have changed after a patch 
		 *    - execute()-methods are used as a branching condition in order to execute only one program version,
		 *      e.g. an if(execute(OLD)) block denotes code that is only executed in the old version 
		 *      
		 * 2. If one program version returns before the other one (i.e. inside an if(execute(OLD)) or if(execute(NEW))-block), 
		 *    the respective execution path has to be marked as a diffpath. 
		 */
		if (insn instanceof JVMReturnInstruction){ 
			MethodInfo mi = insn.getMethodInfo();
			StackFrame sf = ti.getModifiableTopFrame();

			//Check whether the returned method matches with one of the change methods
			int index = -1;
			for(int i = 0; i < changeMethods.length; i++){
				if(changeMethods[i].matches(mi)){
					index = i;
					break;
				}
			}
			
			if(index != -1){ //change() detected
				processChangeMethod(index, ti, insn, pcCg, pc);
				/*
				if(pc==null){
					//change() before we have executed any if-instruction -> turn off directed symbolic execution
					ShadowInstructionFactory.directedSymEx = false;
				}
				*/
				return;
			}

			//Check for execute(OLD) or execute(NEW) annotation, switch execution mode accordingly
			if(execute.matches(mi)){
				pc.markContainsDiffExpr();
				pcCg.setCurrentPC(pc);
				//1 -> true (execute old version only), 0 -> false (execute new version only)
				int value = sf.getLocalVariable(1);
				if(value == 0){
					sf.setOperandAttr(new ExecExpression(0)); 
				}
				else{
					sf.setOperandAttr(new ExecExpression(1));
				}
				return;
			}
			
			//Check whether we returned during execution mode OLD
			if(ti.getExecutionMode() != Execute.BOTH){
				assert(shadowCg != null);
				
				//Mark path as diffpath, except if a different method (called inside the block) returned 
				MethodInfo returnMethodInfo = insn.getMethodInfo();
				
				if(returnMethodInfo.equals(shadowCg.getMethodInfo())){
					if(!pc.isDiffPC()){ //do not overwrite already set diff information
						pc.markAsDiffPC(insn.getLineNumber(), Diff.diffReturn);
						pcCg.setCurrentPC(pc);
					}
				}
				else{
					return;
				}
				
				//If we are currently executing the old version, we want to skip the return and continue at the end of the block
				//since we marked the path as a diffpath, only the new version will be explored from that point on
				if(ti.getExecutionMode() == Execute.OLD){
					Instruction next = shadowCg.getEndInstruction();
					//Pop the return value as we skip the return instruction (except for void)
					if(!(insn instanceof RETURN)){
						sf.pop(); 
					}
					System.out.println("Old version returned at line " + insn.getLineNumber() + " continue executing new version only; next instruction: " + next);
					//currentThread.setNextPC(next);
					ti.skipInstruction(next);
					return;
				}
				else{
					//Do nothing?
					return;
				}	
			}
		}
	}
	
	
	public void processChangeMethod(int index, ThreadInfo ti, Instruction insn, PCChoiceGenerator cg, PathCondition pc){
		//TODO: change to use generic expressions and only distinguish between occupied stack slots
		StackFrame sf = ti.getModifiableTopFrame();
		try{
			pc.markContainsDiffExpr();
			cg.setCurrentPC(pc);
		}catch(java.lang.NullPointerException npe){
			System.err.println("change() has been called without actually symbolically executing a method. Please check the source code and .jpf configuration file.");
			System.exit(1);
		}
		switch(index){
		case 0: //Matched with change(boolean,boolean)
			//The evaluated boolean expressions (0->true, 1->false; it's the other way round cause the generated bytecode always contains the negated if-insn)
			
			int old_value = sf.getLocalVariable(1);
			int new_value = sf.getLocalVariable(2);

			Object old_expr = sf.getLocalAttr(1);
			Object new_expr = sf.getLocalAttr(2);
			
			old_expr = BytecodeUtils.getShadowExpr(old_expr, old_value);
			new_expr = BytecodeUtils.getSymbcExpr(new_expr, new_value);
			
			if(!ti.isFirstStepInsn()){
				PCChoiceGenerator nextCg;
				if(pc.isDiffPC()){
					nextCg = new PCChoiceGenerator(1);
					nextCg.setPC(pc, 0);
				}
				else{
					nextCg = new PCChoiceGenerator(4);
				}
				nextCg.setOffset(insn.getPosition());
				nextCg.setMethodName(insn.getMethodInfo().getFullName());
				nextCg.setExecutionMode(Execute.BOTH);
				ti.getVM().getSystemState().setNextChoiceGenerator(nextCg);
				ti.skipInstruction(insn);
				return;
			}
			else{
				//If we are currently in a diff path, we can simply set the new expr as the resulting attribute
				if(pc.isDiffPC()){
					sf.setOperandAttr(new_expr);
					return;
				}
				
				//No diffpath, check for possible divergences (4-way forking)
				PCChoiceGenerator curCg = (PCChoiceGenerator) ti.getVM().getSystemState().getChoiceGenerator();
				PCChoiceGenerator prevCg = curCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
				PathCondition nextPc;
				
				if(prevCg == null){
					nextPc = new PathCondition();
				}
				else{
					nextPc = prevCg.getCurrentPC();
					assert(nextPc.containsDiffExpr());
				}
				
				int choice = curCg.getNextChoice();
				ti.setExecutionMode(curCg.getExecutionMode());
				assert(ti.getExecutionMode()==Execute.BOTH);
				
				switch(choice){
				case 0: //True path
					nextPc._addDet(Comparator.EQ, (IntegerExpression)new_expr, 0);
					nextPc._addDet(Comparator.EQ, (IntegerExpression)old_expr, 0);
					if(!nextPc.simplify()){
						ti.getVM().getSystemState().setIgnored(true);
					}
					break;
				case 1: //False path
					nextPc._addDet(Comparator.EQ, (IntegerExpression)new_expr, 1);
					nextPc._addDet(Comparator.EQ, (IntegerExpression)old_expr, 1);
					if(!nextPc.simplify()){
						ti.getVM().getSystemState().setIgnored(true);
					}
					break;
				case 2: //Diff true
					nextPc._addDet(Comparator.EQ, (IntegerExpression)new_expr, 0);
					nextPc._addDet(Comparator.EQ, (IntegerExpression)old_expr, 1);
					if(!nextPc.simplify()){
						ti.getVM().getSystemState().setIgnored(true);
					}
					break;
				case 3:
					nextPc._addDet(Comparator.EQ, (IntegerExpression)new_expr, 1);
					nextPc._addDet(Comparator.EQ, (IntegerExpression)old_expr, 0);
					if(!nextPc.simplify()){
						ti.getVM().getSystemState().setIgnored(true);
					}
					break;
				}
				curCg.setCurrentPC(nextPc);
				DiffExpression result = new DiffExpression((IntegerExpression)old_expr,(IntegerExpression)new_expr);
				sf.setOperandAttr(result);
				return;
			}
		case 1:
			//Matched with change(int,int)
						
			//Get the current symbolic expressions of the two params, index 0 refers to 'this'
			Object op_v1 = sf.getLocalAttr(1);
			Object op_v2 = sf.getLocalAttr(2);
			
			int v1 = sf.getLocalVariable(1);
			int v2 = sf.getLocalVariable(2);
			
			
			DiffExpression result;
			IntegerExpression shadow_expr_i = BytecodeUtils.getShadowExpr(op_v1, v1);
			IntegerExpression sym_expr_i = BytecodeUtils.getSymbcExpr(op_v2, v2);
			
			result = new DiffExpression(shadow_expr_i,sym_expr_i);
			sf.setOperandAttr(result);
			return;
			
		case 2:
			//Matched with change(float,float) probably not needed as float operands will be casted to double if they are not used like 3.14f

			Object op_v3 = sf.getLocalAttr(1);
			Object op_v4 = sf.getLocalAttr(2);
			
			float v3 = sf.getFloatLocalVariable(1);
			float v4 = sf.getFloatLocalVariable(2);
			
			DiffExpression result_f;
			RealExpression shadow_expr_f = BytecodeUtils.getShadowExpr(op_v3, v3);
			RealExpression sym_expr_f = BytecodeUtils.getSymbcExpr(op_v4, v4);
			
			result_f = new DiffExpression(shadow_expr_f,sym_expr_f);
			sf.setOperandAttr(result_f);
			return;
			
		case 3:
			//Matched with change(double,double)
			Object op_v5 = sf.getLongLocalAttr(1);
			Object op_v6 = sf.getLongLocalAttr(3); //double and long operands occupy two consecutive attribute slots
			
			double v5 = sf.getDoubleLocalVariable(1);
			double v6 = sf.getDoubleLocalVariable(3);
			
			DiffExpression result_d;
			RealExpression shadow_expr_d = BytecodeUtils.getShadowExpr(op_v5, v5);
			RealExpression sym_expr_d = BytecodeUtils.getSymbcExpr(op_v6, v6);

			result_d = new DiffExpression(shadow_expr_d,sym_expr_d);
			sf.setLongOperandAttr(result_d);
			return;
			
		case 4:
			//Matched with change(long,long)
			Object op_v7 = sf.getLongLocalAttr(1);
			Object op_v8 = sf.getLongLocalAttr(3);
			
			long v7 = sf.getLongLocalVariable(1);
			long v8 = sf.getLongLocalVariable(3);

			DiffExpression result_l;
			IntegerExpression shadow_expr_l = BytecodeUtils.getShadowExpr(op_v7, v7);
			IntegerExpression sym_expr_l = BytecodeUtils.getSymbcExpr(op_v8, v8);
			
			result_l = new DiffExpression(shadow_expr_l,sym_expr_l);
			sf.setLongOperandAttr(result_l);
			return;
		default:
			//should not happen
			assert(false);
			return;
		}
	}
	
	
	public void checkChangeDuringDiff(VM vm, ThreadInfo ti, IfInstruction insn){
		/*
		 * We are currently exploring a diff path and are about to execute an if-instruction.
		 * If the if-insn is executed in an if(change(boolean,boolean)) statement, 
		 * we have to skip the evaluation of the first (the old) boolean expression,
		 * since it would modify the path condition.
		 * 
		 */
		
		//We only have to handle the case where the if-insn is executed in the OLD version of change(boolean,boolean)
		if(BytecodeUtils.getIfInsnExecutionMode(insn, ti) == Execute.OLD){
			ti.setExecutionMode(Execute.BOTH); //TODO: or NEW?
			//First search for bytecode pattern that pushes the old result on stack	
			MethodInfo mi = insn.getMethodInfo();
			Instruction first = insn;
			Instruction second = first.getNext();
			Instruction third = second.getNext();
			boolean foundPattern = false;

			while(!foundPattern){

				if(first instanceof ICONST && second instanceof GOTO && third instanceof ICONST){
					assert(!(third.getNext() instanceof JVMInvokeInstruction));
					foundPattern = true;
					
					//Skip the evaluation of the old boolean expression by removing the operands from the stack
					//and setting the next insn to the first insn that evaluates the new expression
					
					//These insns have two operands
					if(insn instanceof IF_ICMPEQ ||
							insn instanceof IF_ICMPGE ||
							insn instanceof IF_ICMPGT ||
							insn instanceof IF_ICMPLE ||
							insn instanceof IF_ICMPLT ||
							insn instanceof IF_ICMPNE){
						ti.getModifiableTopFrame().pop();	
					}
							
					ti.getModifiableTopFrame().pop();
					ti.getModifiableTopFrame().push(0);
					
					//Skip bytecode instruction to the evaluation of the new boolean expression
					ti.setNextPC(third.getNext());
					return;
				}
				else{
					first = second;
					second = third;
					third = third.getNext();
					assert(mi.containsLineNumber(third.getLineNumber()));
				}
			}
		}
	}

	/* Determines the possible paths we can explore in order to reach a change()-method.
	 * Note: If the branching condition is e.g. x!=10 in the source code, the compiler actually generates the insn IFEQ.
	 * This means that if a change()-method is only reachable from the if-block, the PCChoiceGenerator
	 * only consists of the choice 1 (the false path of the negated If-insn). 
	 */
	public void setNextPaths(IfInstruction insn,PCChoiceGenerator curCg){

		int ifLineNumber = insn.getLineNumber();
		int elseLineNumber = insn.getTarget().getLineNumber();
		
		//reachability information based on the actual source code
		boolean exploreTruePath = this.distanceAnalyzer.checkReachability(ifLineNumber, insn.getMethodInfo().getName());
		boolean exploreFalsePath = this.distanceAnalyzer.checkReachability(elseLineNumber, insn.getMethodInfo().getName());
		
		if(exploreTruePath){
			if(exploreFalsePath){
				//both paths can reach a change()-method, choices: 0,1 (true and false path)
				curCg.resetAndSetChoices(0, 1, 1);
				if(ShadowInstructionFactory.debugDirectedSymEx) System.out.println("If-insn at line: "+ifLineNumber+"->True/False");
			}
			else{
				//only the true path can reach a change()-method, choice: 1 (see note)
				curCg.resetAndSetChoices(1, 1, 1);
				if(ShadowInstructionFactory.debugDirectedSymEx) System.out.println("If-insn at line: "+ifLineNumber+"->True");
			}
		}
		else{
			if(exploreFalsePath){
				//only the false path can reach a change()-method, choice: 0
				curCg.resetAndSetChoices(0, 0, 1);
				if(ShadowInstructionFactory.debugDirectedSymEx) System.out.println("If-insn at line: "+ifLineNumber+"->False");
			}
			else{
				//none of the two paths can reach a change()-method, backtrack?
				curCg.setDone();
				if(ShadowInstructionFactory.debugDirectedSymEx) System.out.println("If-insn at line: "+ifLineNumber+"->None");
			}
		}	
	}
}
