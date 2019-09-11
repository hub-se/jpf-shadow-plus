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

public class LNEG extends gov.nasa.jpf.jvm.bytecode.LNEG{
	
	@Override
	public Instruction execute (ThreadInfo th) {
		//return super.execute(ss, ks, th);
		
		StackFrame sf = th.getModifiableTopFrame();
		Object op_v = sf.getLongOperandAttr();
		
		long v = sf.popLong();
		sf.pushLong(-v);

		if(!(op_v == null && th.getExecutionMode() ==Execute.BOTH)){
			IntegerExpression shadow_v = null;
			IntegerExpression sym_v = null;
			IntegerExpression shadow_result = null;
			IntegerExpression sym_result = null;
			
			sf.pushLong(-v);
			if(op_v != null){
				if(op_v instanceof DiffExpression){
					shadow_v = (IntegerExpression)((DiffExpression)op_v).getShadow();
					sym_v = (IntegerExpression)((DiffExpression)op_v).getSymbc();
				}
				else{
					shadow_v = (IntegerExpression) op_v;
					sym_v = shadow_v;
				}
			}
			else{
				shadow_v = new IntegerConstant((int)v);
				sym_v = shadow_v;
			}
			
			if(th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.BOTH){
				if(th.getExecutionMode() ==Execute.OLD){
					sym_result = sym_v;
				}	
				shadow_result = shadow_v._neg();
			}
			
			if(th.getExecutionMode() ==Execute.NEW || th.getExecutionMode() ==Execute.BOTH){
				if(th.getExecutionMode() ==Execute.NEW){
					shadow_result = shadow_v;
				}		
				sym_result = sym_v._neg();
			}
			
			
			if(op_v instanceof DiffExpression || th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.NEW){
				DiffExpression result = new DiffExpression(shadow_result,sym_result);
				sf.setLongOperandAttr(result);
			}
			else{
				IntegerExpression result = (IntegerExpression) sym_result;
				sf.setLongOperandAttr(result);
			}
				
			
		}		
		//System.out.println("Execute INEG: "+result);
		
		return getNext(th);
	}
}
