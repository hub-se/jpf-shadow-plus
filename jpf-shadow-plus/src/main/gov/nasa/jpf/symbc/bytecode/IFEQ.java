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


import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.ShadowPCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition.Diff;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;
import gov.nasa.jpf.symbc.numeric.ExecExpression;
import gov.nasa.jpf.symbc.numeric.DiffExpression;

public class IFEQ extends gov.nasa.jpf.jvm.bytecode.IFEQ {
	public IFEQ(int targetPosition){
	    super(targetPosition);
	  }
	@Override
	public Instruction execute (ThreadInfo ti) {
		StackFrame sf = ti.getModifiableTopFrame();
		Object op_v = sf.getOperandAttr();

		if(op_v == null) { // the condition is concrete
			//System.out.println("Execute IFEQ: The condition is concrete");
			return super.execute(ti);
		}
		else { //The condition is symbolic
			if(op_v instanceof ExecExpression){
				if(((ExecExpression) op_v).getExecutionMode() == 1){ //if(execute(OLD))
					if(!ti.isFirstStepInsn()){
						//First we have to check whether we are currently exploring a diffpath
						//in this case, we will skip the whole execute(OLD) block
						PCChoiceGenerator curPcCg;
						ChoiceGenerator<?> curCg = ti.getVM().getSystemState().getChoiceGenerator();
						if(curCg instanceof PCChoiceGenerator){
							curPcCg = (PCChoiceGenerator) curCg;
						}
						else{
							curPcCg = curCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
						}
						if(curPcCg != null){
							PathCondition pc = curPcCg.getCurrentPC();
							if(pc.isDiffPC()){
								ti.getModifiableTopFrame().pop();
								return this.getTarget();
							}
						}
												
						ShadowPCChoiceGenerator shadowCg =  new ShadowPCChoiceGenerator(1,this.getTarget(),this.getMethodInfo());
						//System.out.println("Target instruciton is: "+this.getTarget().getMnemonic());
						shadowCg.setOffset(this.getPosition());
						shadowCg.setMethodName(this.getMethodInfo().getFullName());
						shadowCg.setExecutionMode(Execute.OLD);
						ti.getVM().getSystemState().setNextChoiceGenerator(shadowCg);
						return this;
					}
					else{
						ti.getModifiableTopFrame().pop();
						PathCondition pc;
						ShadowPCChoiceGenerator shadowCg = ((ShadowPCChoiceGenerator)ti.getVM().getSystemState().getChoiceGenerator());
						PCChoiceGenerator prevCg = shadowCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
						if(prevCg == null){
							pc = new PathCondition();
						}
						else{
							pc = prevCg.getCurrentPC();
						}
						assert(pc != null);
						ti.setExecutionMode(shadowCg.getExecutionMode());
						assert(ti.getExecutionMode() == Execute.OLD);
						shadowCg.setCurrentPC(pc);
						
						Instruction next = this.getNext();
						//System.out.println("Executing old version only, next instruction: "+next.getMnemonic());
						return next; //start executing the block of old version
					}
				}
				else{
					//execute(NEW)
					if(!ti.isFirstStepInsn()){
						ShadowPCChoiceGenerator shadowCg =  new ShadowPCChoiceGenerator(1,this.getTarget(),this.getMethodInfo());
						shadowCg.setOffset(this.getPosition());
						shadowCg.setMethodName(this.getMethodInfo().getFullName());
						shadowCg.setExecutionMode(Execute.NEW);
						ti.getVM().getSystemState().setNextChoiceGenerator(shadowCg);
						return this;
					}
					else{
						ti.getModifiableTopFrame().pop();
						PathCondition pc;
						ShadowPCChoiceGenerator shadowCg = ((ShadowPCChoiceGenerator)ti.getVM().getSystemState().getChoiceGenerator());
						PCChoiceGenerator prevCg = shadowCg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
						if(prevCg == null){
							pc = new PathCondition();
						}
						else{
							pc = prevCg.getCurrentPC();
						}
						assert(pc != null);
						ti.setExecutionMode(shadowCg.getExecutionMode());
						assert(ti.getExecutionMode() == Execute.NEW);
						shadowCg.setCurrentPC(pc);
						
						Instruction next = this.getNext();
						return next; //start executing the block of new version
					}
				}
			}
			
			//no if(execute(old)) or if(execute(new)) branch, proceed as usual
			
			if(!ti.isFirstStepInsn()){
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
				
				PCChoiceGenerator nextCg;
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
				//"Lower part" of cg method, process choice now
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
				//System.out.println("Processing Choice "+choice+" for "+ti.getExecutionMode());
				switch(choice){
				case 0: //True path
					if(ti.getExecutionMode() == Execute.NEW || ti.getExecutionMode() == Execute.BOTH){
						pc._addDet(Comparator.EQ, sym_v, 0);
					}
					if(ti.getExecutionMode() == Execute.OLD || ti.getExecutionMode() == Execute.BOTH){
						if(!pc.isDiffPC()){
							pc._addDet(Comparator.EQ, shadow_v, 0);
						}
					}
					if(!pc.simplify()){ //unsat
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						curCg.setCurrentPC(pc);
					}
					return this.getTarget();
					
				case 1: //False path
					if(ti.getExecutionMode() == Execute.NEW || ti.getExecutionMode() == Execute.BOTH){
						pc._addDet(Comparator.NE, sym_v, 0);
					}
					if(ti.getExecutionMode() == Execute.OLD || ti.getExecutionMode() == Execute.BOTH){
						if(!pc.isDiffPC()){
							pc._addDet(Comparator.NE, shadow_v, 0);
						}
					}
					if(!pc.simplify()){ //unsat
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						curCg.setCurrentPC(pc);
					}
					return this.getNext(ti);
					
				case 2: //Diff true path
					pc._addDet(Comparator.EQ, sym_v, 0);
					pc._addDet(Comparator.NE, shadow_v, 0);
					if(!pc.simplify()){ //unsat
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						pc.markAsDiffPC(this.getLineNumber(),Diff.diffTrue);
						curCg.setCurrentPC(pc);
					}
					return this.getTarget();
				
				case 3: //Diff false path
					pc._addDet(Comparator.NE, sym_v, 0);
					pc._addDet(Comparator.EQ, shadow_v, 0);
					if(!pc.simplify()){ //unsat
						ti.getVM().getSystemState().setIgnored(true);
					}
					else{
						pc.markAsDiffPC(this.getLineNumber(),Diff.diffFalse);
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
