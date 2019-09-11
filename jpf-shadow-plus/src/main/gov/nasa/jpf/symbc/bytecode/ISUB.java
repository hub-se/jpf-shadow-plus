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

//
//Copyright (C) 2007 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.

//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.

//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.symbc.bytecode;



import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;

/*
 * Implementation of the shadow symbolic ISUB bytecode instruction
 */

public class ISUB extends gov.nasa.jpf.jvm.bytecode.ISUB {

	@Override
	public Instruction execute (ThreadInfo ti) {
		
		
		StackFrame sf = ti.getModifiableTopFrame();
		Object op_v1 = sf.getOperandAttr(0); 
		Object op_v2 = sf.getOperandAttr(1);

		
		if(op_v1==null && op_v2 == null && ti.getExecutionMode()==Execute.BOTH) {
			return super.execute(ti);
		}
		else{	
			//Pop (concrete) operands from operand stack and push result	
			int v1 = sf.pop();
			int v2 = sf.pop();
			sf.push(v2 - v1, false);

			//Get symbolic and shadow expressions from the operands
			IntegerExpression sym_v1 = BytecodeUtils.getSymbcExpr(op_v1, v1);
			IntegerExpression shadow_v1 = BytecodeUtils.getShadowExpr(op_v1, v1); 
			
			IntegerExpression sym_v2 = BytecodeUtils.getSymbcExpr(op_v2,v2);
			IntegerExpression shadow_v2 = BytecodeUtils.getShadowExpr(op_v2, v2); 
			
			IntegerExpression sym_result = null;
			IntegerExpression shadow_result = null; 
			
			//Calculate resulting expressions depending on the execution mode
			if(ti.getExecutionMode()==Execute.OLD || ti.getExecutionMode()==Execute.BOTH){				
				
				if(ti.getExecutionMode()==Execute.OLD){
					sym_result = new IntegerConstant(0);
				}
				shadow_result = shadow_v2._minus(shadow_v1);
			}
			
			if(ti.getExecutionMode()==Execute.NEW || ti.getExecutionMode()==Execute.BOTH){
				
				if(ti.getExecutionMode()==Execute.NEW){
					shadow_result = new IntegerConstant(0);
				}
				sym_result = sym_v2._minus(sym_v1);
			}
			
			if(op_v1 instanceof DiffExpression || op_v2 instanceof DiffExpression || ti.getExecutionMode()!=Execute.BOTH){
				DiffExpression result = new DiffExpression(shadow_result,sym_result);
				sf.setOperandAttr(result);
			}
			else{
				IntegerExpression result = (IntegerExpression) sym_result;
				sf.setOperandAttr(result);
			}
		
		}
		return getNext(ti);
	}

}
