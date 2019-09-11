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

import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.symbc.numeric.DiffExpression;


/**
 * Negate float
 * ..., value  => ..., result
 */
public class FNEG extends gov.nasa.jpf.jvm.bytecode.FNEG  {

  @Override
  public Instruction execute (ThreadInfo th) {
	  
	  StackFrame sf = th.getModifiableTopFrame();
	  Object op_v = sf.getOperandAttr(); 
	  
	  float v = Types.intToFloat(sf.pop());
	  sf.push(Types.floatToInt(-v),false);

	  if(!(op_v == null && th.getExecutionMode() ==Execute.BOTH)){
		  RealExpression shadow_v = null;
		  RealExpression sym_v = null;
		  RealExpression shadow_result = null;
		  RealExpression sym_result = null;
		  
		  if(op_v!=null){
			  if(op_v instanceof DiffExpression){
				  shadow_v = (RealExpression) ((DiffExpression)op_v).getShadow();
				  sym_v = (RealExpression) ((DiffExpression)op_v).getSymbc();
			  }
			  else{
				  shadow_v = (RealExpression) op_v;
				  sym_v = shadow_v;
			  }
		  }
		  else{
			  shadow_v = new RealConstant(v);
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
			  sf.setOperandAttr(result);
		  }
		  else{
			  RealExpression result = sym_result;
			  sf.setOperandAttr(result);
		  }
	  }
	  //	System.out.println("Execute FNEG: "+ result);
    return getNext(th);
  }

}
