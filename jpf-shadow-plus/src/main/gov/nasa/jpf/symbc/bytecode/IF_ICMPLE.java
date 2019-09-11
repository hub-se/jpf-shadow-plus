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
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;



public class IF_ICMPLE extends gov.nasa.jpf.jvm.bytecode.IF_ICMPLE{
	public IF_ICMPLE(int targetPosition){
	    super(targetPosition);
	  }
	@Override
	public Instruction execute (ThreadInfo ti) {
		StackFrame sf = ti.getModifiableTopFrame();

		Object op_v1 = sf.getOperandAttr(1);
		Object op_v2 = sf.getOperandAttr(0);

		if ((op_v1 == null) && (op_v2 == null)) { // both conditions are concrete
			//System.out.println("Execute IF_ICMPEQ: The conditions are concrete");
			return super.execute(ti);
		}else{ // at least one condition is symbolic
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
					nextCg = new PCChoiceGenerator(4);
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
				//This actually returns the next instruction
				PCChoiceGenerator curCg = (PCChoiceGenerator) ti.getVM().getSystemState().getChoiceGenerator();
				int v2 = ti.getModifiableTopFrame().pop();
				int v1 = ti.getModifiableTopFrame().pop();
				
				PathCondition pc;
				PCChoiceGenerator prevCg = curCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
				
				if(prevCg == null){
					pc = new PathCondition();
				}
				else{
					pc = prevCg.getCurrentPC();
				}
				
				assert(pc != null);
				
				IntegerExpression sym_v1 = BytecodeUtils.getSymbcExpr(op_v1, v1);
				IntegerExpression shadow_v1 = BytecodeUtils.getShadowExpr(op_v1, v1);
				
				IntegerExpression sym_v2 = BytecodeUtils.getSymbcExpr(op_v2, v2);
				IntegerExpression shadow_v2 = BytecodeUtils.getShadowExpr(op_v2, v2);
				
				ti.setExecutionMode(curCg.getExecutionMode());
				int choice = curCg.getNextChoice();
				
				switch(choice){
				case 0: //True path
					if(ti.getExecutionMode() == Execute.NEW || ti.getExecutionMode() == Execute.BOTH){
						pc._addDet(Comparator.LE,sym_v1,sym_v2);
					}
					if(ti.getExecutionMode() == Execute.OLD || ti.getExecutionMode() == Execute.BOTH){
						if(!pc.isDiffPC()){
							pc._addDet(Comparator.LE,shadow_v1,shadow_v2);
						}
					}
					if(!pc.simplify()){
						//path not feasible
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						curCg.setCurrentPC(pc);
					}
					return this.getTarget();
					
				case 1: //False path
					if(ti.getExecutionMode() == Execute.NEW || ti.getExecutionMode() == Execute.BOTH){
						pc._addDet(Comparator.GT,sym_v1,sym_v2);
					}
					if(ti.getExecutionMode() == Execute.OLD || ti.getExecutionMode() == Execute.BOTH){
						if(!pc.isDiffPC()){
							pc._addDet(Comparator.GT,shadow_v1,shadow_v2);
						}
					}
					if(!pc.simplify()){
						//path not feasible
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						curCg.setCurrentPC(pc);
					}
					return this.getNext(ti);
					
				case 2: //Diff true path
					pc._addDet(Comparator.LE,sym_v1,sym_v2);
					pc._addDet(Comparator.GT,shadow_v1,shadow_v2);
					if(!pc.simplify()){
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						pc.markAsDiffPC(this.getLineNumber(),Diff.diffTrue);
						curCg.setCurrentPC(pc);
					}
					return this.getTarget();
					
				case 3: //Diff false path
					pc._addDet(Comparator.GT,sym_v1,sym_v2);
					pc._addDet(Comparator.LE,shadow_v1,shadow_v2);
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