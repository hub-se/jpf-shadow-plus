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
// Copyright (C) 2007 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.symbc.bytecode;


import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;

/*
 * Implementation of the shadow symbolic INEG bytecode instruction
 */

public class INEG extends gov.nasa.jpf.jvm.bytecode.INEG{
	
	@Override
	public Instruction execute (ThreadInfo ti) {
		//return super.execute(ss, ks, th);
		
		StackFrame sf = ti.getModifiableTopFrame();
		Object op_v = sf.getOperandAttr();
		
		int v = sf.pop();
		sf.push(-v, false);

		if(!(op_v == null && ti.getExecutionMode()==Execute.BOTH)){
	    	//Symbolic and shadow expressions of the operand and result
	    	IntegerExpression sym_v = BytecodeUtils.getSymbcExpr(op_v, v);
	    	IntegerExpression shadow_v = BytecodeUtils.getShadowExpr(op_v, v);
	    	
	    	IntegerExpression sym_result = null;
	    	IntegerExpression shadow_result = null;
			
			if(ti.getExecutionMode()==Execute.OLD || ti.getExecutionMode()==Execute.BOTH){
				if(ti.getExecutionMode()==Execute.OLD){
					sym_result = sym_v;	
				}
				shadow_result = shadow_v._neg();
			}
			
			if(ti.getExecutionMode()==Execute.NEW || ti.getExecutionMode()==Execute.BOTH){
				if(ti.getExecutionMode()==Execute.NEW){
					shadow_result = shadow_v;
				}	
				sym_result = sym_v._neg();
			}
			
			
			if(op_v instanceof DiffExpression || ti.getExecutionMode() != Execute.BOTH){
				DiffExpression result = new DiffExpression(shadow_result,sym_result);
				sf.setOperandAttr(result);
			}
			else{
				IntegerExpression result = (IntegerExpression) sym_result;
				sf.setOperandAttr(result);
			}
				
			
		}		
		//System.out.println("Execute INEG: "+result);
		
		return getNext(ti);
	}
}
