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


import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.DiffExpression;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.ThreadInfo.Execute;


/**
 * Multiply float
 * ..., value1, value2 => ..., result
 */
public class FMUL extends gov.nasa.jpf.jvm.bytecode.FMUL {

  @Override
  public Instruction execute (ThreadInfo th) {
	StackFrame sf = th.getModifiableTopFrame();

	Object op_v1 = sf.getOperandAttr(); 
    float v1 = Types.intToFloat(sf.pop());
    Object op_v2 = sf.getOperandAttr();
    float v2 = Types.intToFloat(sf.pop());
    
    float r = v1 * v2;
	sf.push(Types.floatToInt(r), false); 

    if(!(op_v1==null && op_v2==null&& th.getExecutionMode() == Execute.BOTH)){
    	RealExpression shadow_v1 = null;
    	RealExpression sym_v1 = null;
    	
    	RealExpression shadow_v2 = null;
    	RealExpression sym_v2 = null;
    	
	    RealExpression shadow_result = null;
	    RealExpression sym_result = null;
	    
	    if(op_v1!=null){
	    	if(op_v1 instanceof DiffExpression){
	    		shadow_v1 = (RealExpression)((DiffExpression)op_v1).getShadow();
	    		sym_v1 = (RealExpression)((DiffExpression)op_v1).getSymbc();
	    	}
	    	else{
	    		shadow_v1 = (RealExpression)op_v1;
	    		sym_v1 = shadow_v1;
	    	}
	    }
	    else{
			//op_v1 is concrete
			sym_v1 = new RealConstant(v1);
			shadow_v1 = sym_v1;
		}
	    
	    if(op_v2!=null){
	    	if(op_v2 instanceof DiffExpression){
	    		shadow_v2 = (RealExpression)((DiffExpression)op_v2).getShadow();
	    		sym_v2 = (RealExpression)((DiffExpression)op_v2).getSymbc();
	    	}
	    	else{
	    		shadow_v2 = (RealExpression)op_v2;
	    		sym_v2 = shadow_v2;
	    	}
	    }
	    else{
			sym_v2 = new RealConstant(v2);
			shadow_v2 = sym_v2;
		}
	    
	    if(th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.BOTH){
	    	if(th.getExecutionMode() ==Execute.OLD){
	    		sym_result = new RealConstant(0);
	    	}
	    	
			if(shadow_v2!=null) {
				if (shadow_v1!=null)
					shadow_result = shadow_v2._mul(shadow_v1);
				else // v1 is concrete
					shadow_result = shadow_v2._mul(v1);
			}
			else if (shadow_v1!=null){
				shadow_result = shadow_v1._mul(v2);
			}
	    }
	    
	    if(th.getExecutionMode() ==Execute.NEW || th.getExecutionMode() ==Execute.BOTH){
	    	if(th.getExecutionMode() ==Execute.NEW){
	    		shadow_result = new RealConstant(0);
	    	}
	    	
			if(sym_v2!=null) {
				if (sym_v1!=null)
					sym_result = sym_v2._mul(sym_v1);
				else // v1 is concrete
					sym_result = sym_v2._mul(v1);
			}
			else if (sym_v1!=null){
				sym_result = sym_v1._mul(v2);
			}
	    }
	    
	    if(op_v1 instanceof DiffExpression || op_v2 instanceof DiffExpression || th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.NEW){
	    	DiffExpression result = new DiffExpression(shadow_result,sym_result);
	    	sf.setOperandAttr(result);
	    }
	    else{
	    	RealExpression result = sym_result;
	    	sf.setOperandAttr(result);
	    }
	    		
    }
	
	//System.out.println("Execute FMUL: "+ result);

    return getNext(th);
  }

}
