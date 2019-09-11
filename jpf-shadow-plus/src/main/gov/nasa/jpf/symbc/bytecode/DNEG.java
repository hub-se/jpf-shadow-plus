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
// Copyright (C) 2006 United States Government as represented by the
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


import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.symbc.numeric.*;

/**
 * Negate double ..., value => ..., result
 */
public class DNEG extends gov.nasa.jpf.jvm.bytecode.DNEG {
	
	@Override
	public Instruction execute(ThreadInfo th) {

		StackFrame sf = th.getModifiableTopFrame();
		Object op_v = sf.getLongOperandAttr();
		
		double v = Types.longToDouble(sf.popLong());

		sf.pushLong(Types.doubleToLong(-v));

		if(!(op_v == null && th.getExecutionMode() ==Execute.BOTH)){
			
			RealExpression shadow_v = null;
			RealExpression sym_v = null;
			RealExpression shadow_result = null;
			RealExpression sym_result = null;
			
			if(op_v != null){
				if(op_v instanceof DiffExpression){
					shadow_v = (RealExpression)((DiffExpression)op_v).getShadow();
					sym_v = (RealExpression)((DiffExpression)op_v).getSymbc();
				}
				else{
					sym_v = (RealExpression) op_v;
					shadow_v = sym_v;
				}
			}
			else{
				sym_v = new RealConstant(v);
				shadow_v = sym_v;
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
			
			if(op_v instanceof DiffExpression || th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.BOTH){
				DiffExpression result = new DiffExpression(shadow_result,sym_result);
				sf.setLongOperandAttr(result);
			}
			else{
				RealExpression result = (RealExpression) sym_result;
				sf.setLongOperandAttr(result);
			}

		}
		//System.out.println("Execute DNEG: " + sf.getLongOperandAttr());
		return getNext(th);
	}

}
