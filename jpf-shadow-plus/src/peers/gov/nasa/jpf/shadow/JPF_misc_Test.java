package gov.nasa.jpf.shadow;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.heap.Helper;
import gov.nasa.jpf.symbc.heap.SymbolicInputHeap;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.DiffExpression;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ReferenceFieldInfo;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class JPF_misc_Test extends NativePeer {
	
	@MJI
	public static int getSymbolicExpression(MJIEnv env, int objRef, int v) {
		Object [] attrs = env.getArgAttributes();
		IntegerExpression sym_v = null;
		if(attrs != null){
			if(attrs[0] != null){
				if(attrs[0] instanceof DiffExpression){
					sym_v = (IntegerExpression) ((DiffExpression)attrs[0]).getSymbc();
				}
				else{
					sym_v = (IntegerExpression)attrs[0];
				}
				return env.newString(sym_v.toString());
			}
		}
		return env.newString(Integer.toString(v));
	}
	
	
	
	@MJI
    public static int getShadowExpression(MJIEnv env, int objRef, int v) {
    	Object [] attrs = env.getArgAttributes();
		IntegerExpression shadow_v = null;
		if(attrs != null){
			if(attrs[0] != null){
				if(attrs[0] instanceof DiffExpression){
					shadow_v = (IntegerExpression) ((DiffExpression)attrs[0]).getShadow();
				}
				else{
					shadow_v = (IntegerExpression)attrs[0];
				}
				return env.newString(shadow_v.toString());
	
			}
		}
		return env.newString(Integer.toString(v));

    }
	
	@MJI
	public static int getSymbolicExpressionLong(MJIEnv env, int objRef, long v) {
		Object [] attrs = env.getArgAttributes();
		IntegerExpression sym_v = null;
		if(attrs != null){
			if(attrs[0] != null){
				if(attrs[0] instanceof DiffExpression){
					sym_v = (IntegerExpression) ((DiffExpression)attrs[0]).getSymbc();
				}
				else{
					sym_v = (IntegerExpression)attrs[0];
				}
				return env.newString(sym_v.toString());
			}
		}
		return env.newString(Long.toString(v));
	}
	
	
	
	@MJI
    public static int getShadowExpressionLong(MJIEnv env, int objRef, long v) {
    	Object [] attrs = env.getArgAttributes();
		IntegerExpression shadow_v = null;
		if(attrs != null){
			if(attrs[0] != null){
				if(attrs[0] instanceof DiffExpression){
					shadow_v = (IntegerExpression) ((DiffExpression)attrs[0]).getShadow();
				}
				else{
					shadow_v = (IntegerExpression)attrs[0];
				}
				return env.newString(shadow_v.toString());
	
			}
		}
		return env.newString(Long.toString(v));

    }
	
	@MJI
	public static int getSymbolicExpressionFloat(MJIEnv env, int objRef, float v) {
		Object [] attrs = env.getArgAttributes();
		RealExpression sym_v = null;
		if(attrs != null){
			if(attrs[0] != null){
				if(attrs[0] instanceof DiffExpression){
					sym_v = (RealExpression) ((DiffExpression)attrs[0]).getSymbc();
				}
				else{
					sym_v = (RealExpression)attrs[0];
				}
				return env.newString(sym_v.toString());
			}
		}
		return env.newString(Float.toString(v));
	}
	
	
	
	@MJI
    public static int getShadowExpressionFloat(MJIEnv env, int objRef, float v) {
    	Object [] attrs = env.getArgAttributes();
		RealExpression shadow_v = null;
		if(attrs != null){
			if(attrs[0] != null){
				if(attrs[0] instanceof DiffExpression){
					shadow_v = (RealExpression) ((DiffExpression)attrs[0]).getShadow();
				}
				else{
					shadow_v = (RealExpression)attrs[0];
				}
				return env.newString(shadow_v.toString());
	
			}
		}
		return env.newString(Float.toString(v));

    }
	
	@MJI
	public static int getSymbolicExpressionDouble(MJIEnv env, int objRef, double v) {
		Object [] attrs = env.getArgAttributes();
		RealExpression sym_v = null;
		if(attrs != null){
			if(attrs[0] != null){
				if(attrs[0] instanceof DiffExpression){
					sym_v = (RealExpression) ((DiffExpression)attrs[0]).getSymbc();
				}
				else{
					sym_v = (RealExpression)attrs[0];
				}
				return env.newString(sym_v.toString());
			}
		}
		return env.newString(Double.toString(v));
	}
	
	
	
	@MJI
    public static int getShadowExpressionDouble(MJIEnv env, int objRef, double v) {
    	Object [] attrs = env.getArgAttributes();
		RealExpression shadow_v = null;
		if(attrs != null){
			if(attrs[0] != null){
				if(attrs[0] instanceof DiffExpression){
					shadow_v = (RealExpression) ((DiffExpression)attrs[0]).getShadow();
				}
				else{
					shadow_v = (RealExpression)attrs[0];
				}
				return env.newString(shadow_v.toString());
	
			}
		}
		return env.newString(Double.toString(v));

    }
    
}
