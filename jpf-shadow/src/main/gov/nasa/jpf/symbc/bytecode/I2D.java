/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package gov.nasa.jpf.symbc.bytecode;


import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;


/**
 * Convert int to double
 * ..., value => ..., result
 */
public class I2D extends gov.nasa.jpf.jvm.bytecode.I2D {
	
@Override
public Instruction execute (ThreadInfo ti) {
	  
	Object op_v = ti.getModifiableTopFrame().getOperandAttr(); 
		
	if(op_v == null && ti.getExecutionMode() == Execute.BOTH) {
		return super.execute(ti); 
	}
	else {
		
		// here we get a hold of the current path condition and 
		// add an extra mixed constraint sym_dval==sym_ival

		ChoiceGenerator<?> cg; 
		if (!ti.isFirstStepInsn()) { // first time around
			cg = new PCChoiceGenerator(1); // only one choice 
			ti.getVM().getSystemState().setNextChoiceGenerator(cg);
			return this;  	      
		} else {  // this is what really returns results
			cg = ti.getVM().getSystemState().getChoiceGenerator();
			assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + cg;
		}	
			
		// get the path condition from the 
		// previous choice generator of the same type 

		PathCondition pc;
		ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

		if (prev_cg == null)
			pc = new PathCondition(); // TODO: handling of preconditions needs to be changed
		else 
			pc = ((PCChoiceGenerator)prev_cg).getCurrentPC();
		assert pc != null;
			
		StackFrame sf = ti.getModifiableTopFrame();
		int v = sf.pop();
		sf.pushLong(0); // for symbolic expressions, the concrete value does not matter
		
		IntegerExpression sym_v = BytecodeUtils.getSymbcExpr(op_v, v);
		IntegerExpression shadow_v = BytecodeUtils.getShadowExpr(op_v, v);
		
		SymbolicReal sym_dval = new SymbolicReal();
		pc._addDet(Comparator.EQ, sym_dval, sym_v);

		if(op_v instanceof DiffExpression){
			SymbolicReal shadow_dval = new SymbolicReal();
			DiffExpression result_dval = new DiffExpression(shadow_dval, sym_dval);
			sf.setLongOperandAttr(result_dval);
			pc._addDet(Comparator.EQ, shadow_dval, shadow_v);
		}
		else{
			sf.setLongOperandAttr(sym_dval);
		}
		
		
		if(!pc.simplify())  { // not satisfiable
			ti.getVM().getSystemState().setIgnored(true);
		} else {
			((PCChoiceGenerator) cg).setCurrentPC(pc);
		}
		
		return getNext(ti);
	}
    
  }

}
