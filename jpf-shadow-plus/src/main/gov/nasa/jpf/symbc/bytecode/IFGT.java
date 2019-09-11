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


//Copyright (C) 2006 United States Government as represented by the
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


import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition.Diff;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;

public class IFGT extends gov.nasa.jpf.jvm.bytecode.IFGT {
	public IFGT(int targetPosition){
	    super(targetPosition);
	  }
	@Override
	public Instruction execute (ThreadInfo ti) {

		StackFrame sf = ti.getModifiableTopFrame();
		Object op_v = sf.getOperandAttr();

		if(op_v == null) { // the condition is concrete
			return super.execute(ti);
		}
		else { //The condition is symbolic
			if(!ti.isFirstStepInsn()){
				PCChoiceGenerator nextCg;
				PCChoiceGenerator curPcCg;
				ChoiceGenerator<?> curCg = ti.getVM().getSystemState().getChoiceGenerator();
				if(curCg instanceof PCChoiceGenerator){
					curPcCg = (PCChoiceGenerator) curCg;
				}
				else{
					curPcCg = curCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
				}
				PathCondition pc;
				if(curPcCg != null){
					pc = curPcCg.getCurrentPC();
				}
				else{
					pc = new PathCondition();
				}
				
				if(ti.getExecutionMode() == Execute.BOTH && !pc.isDiffPC() && !BytecodeUtils.isChangeBoolean(this, ti)){
					nextCg = new PCChoiceGenerator(4); //Additionally try to explore diff paths
				}
				else{
					nextCg = new PCChoiceGenerator(2);
				}
				nextCg.setOffset(this.position);
				nextCg.setMethodName(this.getMethodInfo().getFullName());
				nextCg.setExecutionMode(ti.getExecutionMode());
				ti.getVM().getSystemState().setNextChoiceGenerator(nextCg);
				return this;
			}
			else{
				PCChoiceGenerator curCg = (PCChoiceGenerator) ti.getVM().getSystemState().getChoiceGenerator();
				int v = ti.getModifiableTopFrame().pop();
				
				//Get current pc from previous cg
				PathCondition pc;
				PCChoiceGenerator prevCg = curCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
				
				if(prevCg == null){
					pc = new PathCondition();
				}
				else{
					pc = prevCg.getCurrentPC();
				}
				
				assert(pc != null);
				
				
				//Get symbolic and shadow expressions
				IntegerExpression sym_v = BytecodeUtils.getSymbcExpr(op_v, v);
				IntegerExpression shadow_v = BytecodeUtils.getShadowExpr(op_v, v);			
				
				ti.setExecutionMode(curCg.getExecutionMode());
				int choice = curCg.getNextChoice();
				/*
				 * Process choices -> Explore different execution paths
				 * Choice 0: True path
				 * Choice 1: False path
				 * Choice 2: Diff true path (new follows true and old follows false path)
				 * Choice 3: Diff false path (new follows false and old follows true path)
				 */
				switch(choice){
				case 0: //True path
					if(ti.getExecutionMode() == Execute.NEW || ti.getExecutionMode() == Execute.BOTH){
						pc._addDet(Comparator.GT, sym_v, 0);
					}
					if(ti.getExecutionMode() == Execute.OLD || ti.getExecutionMode() == Execute.BOTH){
						if(!pc.isDiffPC()){
							pc._addDet(Comparator.GT, shadow_v, 0);
						}
					}
					if(!pc.simplify()){
						//Path is not feasible
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						curCg.setCurrentPC(pc);
					}
					return this.getTarget();
				
				case 1: //False path
					if(ti.getExecutionMode() == Execute.NEW || ti.getExecutionMode() == Execute.BOTH){
						pc._addDet(Comparator.LE, sym_v, 0);
					}
					if(ti.getExecutionMode() == Execute.OLD || ti.getExecutionMode() == Execute.BOTH){
						if(!pc.isDiffPC()){
							pc._addDet(Comparator.LE, shadow_v, 0);
						}
					}
					if(!pc.simplify()){
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						curCg.setCurrentPC(pc);
					}
					return this.getNext(ti);
					
				case 2: //Diff true path
					pc._addDet(Comparator.GT, sym_v, 0);
					pc._addDet(Comparator.LE, shadow_v, 0);
					if(!pc.simplify()){
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						pc.markAsDiffPC(this.getLineNumber(),Diff.diffTrue);
						curCg.setCurrentPC(pc);
					}
					return this.getTarget();
					
				case 3: //Diff false path
					pc._addDet(Comparator.LE, sym_v, 0);
					pc._addDet(Comparator.GT, shadow_v, 0);
					if(!pc.simplify()){
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						pc.markAsDiffPC(this.getLineNumber(), Diff.diffFalse);
						curCg.setCurrentPC(pc);
					}
					return this.getNext(ti);
				default:
					assert(false);
					return this;
				}	
			}
		}
	}
}
