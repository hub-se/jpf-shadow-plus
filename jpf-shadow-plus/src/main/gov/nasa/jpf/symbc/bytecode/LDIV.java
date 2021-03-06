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
import gov.nasa.jpf.symbc.numeric.PathCondition.Diff;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;

public class LDIV extends gov.nasa.jpf.jvm.bytecode.LDIV {

	@Override
	public Instruction execute (ThreadInfo th) {
		StackFrame sf = th.getModifiableTopFrame();
		Object op_v1 = sf.getOperandAttr(1);
		Object op_v2 = sf.getOperandAttr(3);

		if(op_v1==null && op_v2==null && th.getExecutionMode() == Execute.BOTH){
			return super.execute(th); // we'll still do the concrete execution
		}
		/*
		 * Either we have at least one symbolic operand and/or we execute only
		 * the old or only the new version (which results in divergent results).
		 * In both cases, we should check whether the denominator can be zero,
		 * which affects the path condition.
		 */
		
		if (!th.isFirstStepInsn()) { // first time around
			PCChoiceGenerator nextCg;
			if(th.getExecutionMode() ==Execute.BOTH){
				nextCg = new PCChoiceGenerator(4);
			}
			else{
				//if we only execute one version, we only have to check for == 0 and != 0
				nextCg = new PCChoiceGenerator(2);
			}
			
			nextCg.setOffset(this.position);
			nextCg.setMethodName(this.getMethodInfo().getFullName());
			nextCg.setExecutionMode(th.getExecutionMode());
			th.getVM().setNextChoiceGenerator(nextCg);
			return this;
		} 
		else{  // this is what really returns results
			long v1 = sf.popLong();
			long v2 = sf.popLong();
			sf.pushLong(0);
			
			//symbolic and shadow expressions of the operands and the result
			IntegerExpression shadow_v1 = null; 
			IntegerExpression sym_v1 = null; 
			
			IntegerExpression shadow_v2 = null; 
			IntegerExpression sym_v2 = null; 
			
			IntegerExpression shadow_result = null; 
			IntegerExpression sym_result = null; 
			
			//first get the symbolic expressions of the operands
			if(op_v1!=null){
				if(op_v1 instanceof DiffExpression){
					shadow_v1 = (IntegerExpression)((DiffExpression)op_v1).getShadow();
					sym_v1 = (IntegerExpression)((DiffExpression)op_v1).getSymbc();
				}
				else{//no DiffExpression yet, which means sym and shadow for this operand are the same
					sym_v1 = (IntegerExpression) op_v1;
					shadow_v1 = sym_v1;
				}
			}
			else{
				//concrete
				sym_v1 = new IntegerConstant(v1);
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
				sym_v2 = new IntegerConstant(v2);
				shadow_v2 = sym_v2;
			}
			
			
			PCChoiceGenerator curCg = (PCChoiceGenerator) th.getVM().getChoiceGenerator();
			PathCondition pc;
			PCChoiceGenerator prevCg = curCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
			
			//get current path condition
			if(prevCg == null){
				//TODO: probably not necessary as getPrevCGofType(PCChoiceGenerator) can also return a ShadowPCCg
				prevCg = curCg.getPreviousChoiceGeneratorOfType(ShadowPCChoiceGenerator.class);
				if(prevCg == null){
					pc = new PathCondition();
				}
				else{
					pc = prevCg.getCurrentPC();
				}
			}
			else{
				pc = prevCg.getCurrentPC();
			}
			assert(pc != null);
			
			th.setExecutionMode(curCg.getExecutionMode());
			int choice = curCg.getNextChoice();
			switch(choice){
			case 0: //denominator is zero --> throw arithmetic exception
				if(th.getExecutionMode() == Execute.NEW || th.getExecutionMode() == Execute.BOTH){
					pc._addDet(Comparator.EQ, sym_v1, 0);
				}
				if(th.getExecutionMode() == Execute.OLD || th.getExecutionMode() == Execute.BOTH){
					pc._addDet(Comparator.EQ, shadow_v1, 0);
				}
				
				if(!pc.simplify()){
					th.getVM().getSystemState().setIgnored(true);
					return this.getNext(th);
				}
				else{
					pc.markAsDiffPC(this.getLineNumber(),Diff.divByZero); //technically not a diffpath, just to generate test case
					curCg.setCurrentPC(pc);
					return th.createAndThrowException("java.lang.ArithmeticException","div by 0");
				}
				
			case 1: //denominator is not zero --> set result and continue normally
				if(th.getExecutionMode() == Execute.NEW || th.getExecutionMode() == Execute.BOTH){
					pc._addDet(Comparator.NE, sym_v1, 0);
				}
				if(th.getExecutionMode() == Execute.OLD || th.getExecutionMode() == Execute.BOTH){
					pc._addDet(Comparator.NE, shadow_v1, 0);
				}
				
				if(!pc.simplify()){
					th.getVM().getSystemState().setIgnored(true);
				}
				else{
					/*
					 * We might want to determine results based on the execution mode.
					 * However, the StackFrame already considers the execution mode when
					 * propagating values.
					 */
					curCg.setCurrentPC(pc);
					if(op_v2!=null){
						sym_result = sym_v2._div(sym_v1);
						shadow_result = shadow_v2._div(shadow_v1);
					}
					else{
						//somehow using div_reverse returns wrong results (literally reversed)
						//result = shadow_v1._div_reverse(v2);
						sym_v2 = new IntegerConstant(v2); 
						shadow_v2 = sym_v2;
						
						if(v2==0){
							sym_result = sym_v2; // = 0
							shadow_result = sym_result;
						}
						else{
							sym_result = sym_v2._div(sym_v1);
							shadow_result = shadow_v2._div(shadow_v1);
						}
						
						if((op_v1 instanceof DiffExpression) || (op_v2 instanceof DiffExpression)){
							DiffExpression result = new DiffExpression(shadow_result,sym_result);
							sf.setOperandAttr(result);
						}
						else{
							IntegerExpression result = sym_result;
							sf.setOperandAttr(result);
						}
					}
				}
				return this.getNext(th);
			case 2: //"true" diff, new denominator is zero while old is not --> regression
				pc._addDet(Comparator.EQ, sym_v1, 0);
				pc._addDet(Comparator.NE, shadow_v1, 0);
				
				if(!pc.simplify()){
					th.getVM().getSystemState().setIgnored(true);
					return this.getNext(th);
				}
				else{
					pc.markAsDiffPC(this.getLineNumber(),Diff.divByZero); //technically not a diffpath, just to generate test case

					curCg.setCurrentPC(pc);
					return th.createAndThrowException("java.lang.ArithmeticException","div by 0");
				}
			case 3: //"false" diff, new is not zero but old is --> bug fix
				pc._addDet(Comparator.NE, sym_v1, 0);
				pc._addDet(Comparator.EQ, shadow_v1, 0);
				
				if(!pc.simplify()){
					th.getVM().getSystemState().setIgnored(true);
				}
				else{
					pc.markAsDiffPC(this.getLineNumber(),Diff.divByZero); //technically not a diffpath, just to generate test case
					curCg.setCurrentPC(pc);
					if(op_v2!=null){
						sym_result = sym_v2._div(sym_v1);
						shadow_result = new IntegerConstant(0); //old is div by zero
					}
					else{
						sym_v2 = new IntegerConstant(v2); 						
						if(v2==0){
							sym_result = sym_v2; // = 0
						}
						else{
							sym_result = sym_v2._div(sym_v1);
						}
						shadow_result = new IntegerConstant(0);
						
						if((op_v1 instanceof DiffExpression) || (op_v2 instanceof DiffExpression)){
							DiffExpression result = new DiffExpression(shadow_result,sym_result);
							sf.setOperandAttr(result);
						}
						else{
							IntegerExpression result = sym_result;
							sf.setOperandAttr(result);
						}
					}
				}
				return this.getNext(th);	
			default:
				assert(false);
				return this;
			}	
		}
	}
}