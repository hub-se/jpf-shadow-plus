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
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.ThreadInfo.Execute;

public class FADD extends gov.nasa.jpf.jvm.bytecode.FADD {
	@Override
	public Instruction execute (ThreadInfo th) {

		StackFrame sf = th.getModifiableTopFrame();
		
		Object op_v1 = sf.getOperandAttr(); 
		float v1 = Types.intToFloat(sf.pop());

		Object op_v2 = sf.getOperandAttr();
		float v2 = Types.intToFloat(sf.pop());
		
		//push concrete result on stack
		sf.push(Types.floatToInt(v1+v2),false);
		
		if(!(op_v1==null && op_v2==null&& th.getExecutionMode() == Execute.BOTH)){			
			RealExpression shadow_v1 = null; //symbolic expression of the first operand
			RealExpression sym_v1 = null; //shadow expression of the first operand
			
			RealExpression shadow_v2 = null; //symbolic expression of the second operand
			RealExpression sym_v2 = null; //shadow expression of the second operand
			
			RealExpression shadow_result = null;  //symbolic expression of the result 
			RealExpression sym_result = null; //shadow expression of the result 
						
			//get symbolic expressions of the operand attributes
			if(op_v1!=null){
				if(op_v1 instanceof DiffExpression){//Symbolic and Shadow Expressions are encapsulated in a DiffExpression-object
					shadow_v1 = (RealExpression)((DiffExpression)op_v1).getShadow();
					sym_v1 = (RealExpression)((DiffExpression)op_v1).getSymbc();
				}
				else{//no DiffExpression yet, which means sym and shadow for this operand are the same
					sym_v1 = (RealExpression) op_v1;
					shadow_v1 = sym_v1;
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
					sym_v2 = (RealExpression) op_v2;
					shadow_v2 = sym_v2;
				}
			}
			else{
				sym_v2 = new RealConstant(v2);
				shadow_v2 = sym_v2;
			}
			
			//now calculate the resulting expressions
			if(th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.BOTH){
		
				if(th.getExecutionMode() ==Execute.OLD){//the new shadow expression wont change
					sym_result = new RealConstant(0);					
				}
				
				//add the symbolic expressions
		  		if(shadow_v1!=null) {
		  			if (shadow_v2!=null){
		  				shadow_result = shadow_v1._plus(shadow_v2);
		  			}
					else{ // v2 is concrete
						shadow_result = shadow_v1._plus(v2);
					}
				}
				else if (shadow_v2!=null){
						shadow_result = shadow_v2._plus(v1);
				}
			}
			
			if(th.getExecutionMode() ==Execute.NEW || th.getExecutionMode() ==Execute.BOTH){
				
				if(th.getExecutionMode() ==Execute.NEW){//the old symbolic expression wont change
					shadow_result = new RealConstant(0);			
				}
				
				if(sym_v1!=null) {
		  			if (sym_v2!=null){
		  				sym_result = sym_v1._plus(sym_v2);
		  			}
					else{ // v2 is concrete
						sym_result = sym_v1._plus(v2);
					}
				}
				else if (sym_v2!=null){
						sym_result = sym_v2._plus(v1);
				}
			}
			
			//if at least one of the operands was a DiffExpression, the result will also be an DiffExpression
			//also, executing only the old or the new version will cause a divergence as well
			if(op_v1 instanceof DiffExpression || op_v2 instanceof DiffExpression || th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.NEW){
				DiffExpression result = new DiffExpression(shadow_result,sym_result);
				sf.setOperandAttr(result);
			}
			else{
				RealExpression result = (RealExpression) sym_result;
				sf.setOperandAttr(result);
			}
		}
		//System.out.println("Execute IADD: "+result);	
		return getNext(th);
	}
}
