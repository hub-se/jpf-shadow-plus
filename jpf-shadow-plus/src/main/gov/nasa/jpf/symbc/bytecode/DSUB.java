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
 * Subtract double
 * ..., value1, value2 => ..., result
 */
public class DSUB extends gov.nasa.jpf.jvm.bytecode.DSUB {

  @Override
  public Instruction execute (ThreadInfo th) {
	  
	StackFrame sf = th.getModifiableTopFrame();
	Object op_v1 = sf.getOperandAttr(1); 
    Object op_v2 = sf.getOperandAttr(3);

    double v1 = Types.longToDouble(sf.popLong());
    double v2 = Types.longToDouble(sf.popLong());
    
    double r = v2 - v1;
    sf.pushLong(Types.doubleToLong(r)); 

    if(!(op_v1==null && op_v2==null && th.getExecutionMode() ==Execute.BOTH)){
	    
	    RealExpression shadow_v1 = null;
	    RealExpression sym_v1 = null;
	    
	    RealExpression shadow_v2 = null;
	    RealExpression sym_v2 = null;
	    
	    RealExpression shadow_result = null;
	    RealExpression sym_result = null;
	    
	    if(op_v1!=null){
	    	if(op_v1 instanceof DiffExpression){
	    		shadow_v1 = (RealExpression) ((DiffExpression)op_v1).getShadow();
	    		sym_v1 = (RealExpression) ((DiffExpression)op_v1).getSymbc();
	    	}
	    	else{
	    		sym_v1 = (RealExpression) op_v1;
	    		shadow_v1 = sym_v1;
	    	}
	    }
	    else{
	    	sym_v1 = new RealConstant(v1);
	    	shadow_v1 = sym_v1;
	    }
	    
	    if(op_v2!=null){
	    	if(op_v2 instanceof DiffExpression){
	    		shadow_v2 = (RealExpression) ((DiffExpression)op_v2).getShadow();
	    		sym_v2 = (RealExpression) ((DiffExpression)op_v2).getSymbc();
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
	    
	    if(th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.BOTH){
	    	
	    	if(th.getExecutionMode() ==Execute.OLD){
	    		sym_result = new RealConstant(0);
	    	}
		    
	    	if(shadow_v2!=null) {
				if (shadow_v1!=null){
					shadow_result = shadow_v2._minus(shadow_v1);
				}
				else{ // v1 is concrete
					shadow_result = shadow_v2._minus(v1);
				}
			}else if (shadow_v1!=null){
				shadow_result = shadow_v1._minus_reverse(v2);
			} 
	    }
	    
	    if(th.getExecutionMode() ==Execute.NEW || th.getExecutionMode() ==Execute.BOTH){
	    	
	    	if(th.getExecutionMode() ==Execute.NEW){
	    		shadow_result = new RealConstant(0);
	    	}
	    	
	    	if(sym_v2!=null) {
				if (sym_v1!=null){
					sym_result = sym_v2._minus(sym_v1);
				}
				else{ // v1 is concrete
					sym_result = sym_v2._minus(v1);
				}
			}else if (sym_v1!=null){
				sym_result = sym_v1._minus_reverse(v2);
			}
	    }
	    
	    if(op_v1 instanceof DiffExpression || op_v2 instanceof DiffExpression || th.getExecutionMode() ==Execute.OLD || th.getExecutionMode() ==Execute.NEW){
	    	DiffExpression result = new DiffExpression(shadow_result,sym_result);
	    	sf.setLongOperandAttr(result);
	    }
	    else{
	    	RealExpression result = (RealExpression) sym_result;
	    	sf.setLongOperandAttr(result);
	    }		
    }

    return getNext(th);
  }

}
