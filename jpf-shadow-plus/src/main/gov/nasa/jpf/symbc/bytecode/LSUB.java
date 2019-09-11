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
 * implementation of the symbolic ISUB bytecode instruction
 * the implementation is basically the same as for IADD, just with a different operator
 */

public class LSUB extends gov.nasa.jpf.jvm.bytecode.LSUB {

	@Override
	public Instruction execute (ThreadInfo th) {
		
		
		StackFrame sf = th.getModifiableTopFrame();
		Object op_v1 = sf.getOperandAttr(1); 
		Object op_v2 = sf.getOperandAttr(3);
		
		long v1 = sf.popLong();
		long v2 = sf.popLong();
		sf.pushLong(v2 - v1); // for symbolic expressions, the concrete value actually does not matter
		
		if(!(op_v1==null && op_v2 == null && th.getExecutionMode() ==Execute.BOTH)) {
			
			//symbolic/shadow expressions of the operands and the result
			IntegerExpression shadow_v1 = null;
			IntegerExpression sym_v2 = null;

			IntegerExpression shadow_v2 = null;
			IntegerExpression sym_v1 = null;
			
			IntegerExpression shadow_result = null;
			IntegerExpression sym_result = null;
			
			//get the symbolic expressions of the operands
			if(op_v1!=null){
				if(op_v1 instanceof DiffExpression){
					shadow_v1 = (IntegerExpression)((DiffExpression)op_v1).getShadow();
					sym_v1 = (IntegerExpression)((DiffExpression)op_v1).getSymbc();
				}
				else{
					sym_v1 = (IntegerExpression) op_v1;
					shadow_v1 = sym_v1;
				}
			}
			else{
				sym_v1 = new IntegerConstant((int)v1);
				shadow_v1 = sym_v1;
			}
			
			if(op_v2!=null){
				if(op_v2 instanceof DiffExpression){
					shadow_v2 = (IntegerExpression)((DiffExpression)op_v2).getShadow();
					sym_v2 = (IntegerExpression)((DiffExpression)op_v2).getSymbc();
				}
				else{
					sym_v2 = (IntegerExpression) op_v2;
					shadow_v2 = sym_v2;
				}
			}
			else{
				sym_v2 = new IntegerConstant((int)v2);
				shadow_v2 = sym_v2;
			}
			
			//calculate the results
			if(th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.BOTH){				
				
				if(th.getExecutionMode() ==Execute.OLD){
					sym_result = new IntegerConstant(0);
				}
				
				if(shadow_v2!=null){
					if(shadow_v1!=null){
						shadow_result = shadow_v2._minus(shadow_v1);
					}
					else{
						shadow_result = shadow_v2._minus(v1);
					}
				}
				else if(shadow_v1!=null){
					shadow_result = shadow_v1._minus_reverse(v2);
				}
			}
			
			if(th.getExecutionMode() ==Execute.NEW || th.getExecutionMode() ==Execute.BOTH){
				
				if(th.getExecutionMode() ==Execute.NEW){
					shadow_result = new IntegerConstant(0);
				}
				
				if(sym_v2!=null) {
					if (sym_v1!=null){
						sym_result = sym_v2._minus(sym_v1);
					}
					else{ // v1 is concrete
						sym_result = sym_v2._minus(v1);
					}
				}
				else if (sym_v1!=null){
					sym_result = sym_v1._minus_reverse(v2);
				}
			}
			
			if(op_v1 instanceof DiffExpression || op_v2 instanceof DiffExpression || th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.NEW){
				DiffExpression result = new DiffExpression(shadow_result,sym_result);
				sf.setOperandAttr(result);
			}
			else{
				IntegerExpression result = (IntegerExpression) sym_result;
				sf.setOperandAttr(result);
			}
		
		}
		return getNext(th);
	}

}
