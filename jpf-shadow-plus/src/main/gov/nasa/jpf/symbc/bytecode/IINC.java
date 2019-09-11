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
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;

/*
 * Implementation of the shadow symbolic IINC bytecode instruction
 */

public class IINC extends gov.nasa.jpf.jvm.bytecode.IINC {
	public IINC(int localVarIndex, int incConstant){
		super(localVarIndex, incConstant);
	}
  @Override
  public Instruction execute (ThreadInfo ti) {

    StackFrame sf = ti.getModifiableTopFrame();
    Object op_v = sf.getLocalAttr(index);
    
    if (op_v == null && ti.getExecutionMode() == Execute.BOTH) {
    	// we'll do the concrete execution
    	return super.execute(ti);
    }
    else { //either the operand is symbolic (i.e. op_v != null) or the execution mode is not BOTH
    	int v = sf.getLocalVariable(index);
    	sf.setLocalVariable(index, v+increment, false);
    	
    	//Symbolic and shadow expressions of the operand and result
    	IntegerExpression sym_v = BytecodeUtils.getSymbcExpr(op_v, v);
    	IntegerExpression shadow_v = BytecodeUtils.getShadowExpr(op_v, v);
    	
    	IntegerExpression sym_result = null;
    	IntegerExpression shadow_result = null;
    	
    	if(ti.getExecutionMode()==Execute.OLD || ti.getExecutionMode()==Execute.BOTH){
    		if(ti.getExecutionMode()==Execute.OLD){
    				sym_result = sym_v;  
    		}
    		shadow_result = shadow_v._plus(increment);
    	}
    	
    	if(ti.getExecutionMode()==Execute.NEW || ti.getExecutionMode()==Execute.BOTH){
    		if(ti.getExecutionMode()==Execute.NEW){
    				shadow_result = shadow_v;
    		}
    		sym_result = sym_v._plus(increment);
    	}

    	
    	if(op_v instanceof DiffExpression || ti.getExecutionMode() != Execute.BOTH){
    		DiffExpression result = new DiffExpression(shadow_result,sym_result);
    		sf.setLocalAttr(index, result);
    	}
    	else{
    		IntegerExpression result = (IntegerExpression) sym_result;
    		sf.setLocalAttr(index,result);
    	}

    	//System.out.println("IINC "+sf.getLocalAttr(index));
    }
    return getNext(ti);
  }

}
