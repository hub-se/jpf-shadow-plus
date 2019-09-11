package gov.nasa.jpf.symbc.bytecode.util;

import gov.nasa.jpf.jvm.bytecode.IfInstruction;
import gov.nasa.jpf.symbc.bytecode.LCMP;
import gov.nasa.jpf.symbc.bytecode.FCMPG;
import gov.nasa.jpf.symbc.bytecode.FCMPL;
import gov.nasa.jpf.symbc.bytecode.DCMPG;
import gov.nasa.jpf.symbc.bytecode.DCMPL;



import gov.nasa.jpf.symbc.bytecode.IF_ICMPEQ;
import gov.nasa.jpf.symbc.bytecode.IF_ICMPGE;
import gov.nasa.jpf.symbc.bytecode.IF_ICMPGT;
import gov.nasa.jpf.symbc.bytecode.IF_ICMPLE;
import gov.nasa.jpf.symbc.bytecode.IF_ICMPLT;
import gov.nasa.jpf.symbc.bytecode.IF_ICMPNE;

import gov.nasa.jpf.symbc.bytecode.IFLE;
import gov.nasa.jpf.symbc.bytecode.IFLT;
import gov.nasa.jpf.symbc.bytecode.IFGE;
import gov.nasa.jpf.symbc.bytecode.IFGT;
import gov.nasa.jpf.symbc.bytecode.IFEQ;
import gov.nasa.jpf.symbc.bytecode.IFNE;




import gov.nasa.jpf.symbc.numeric.DiffExpression;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.Execute;
import gov.nasa.jpf.vm.Types;

public class IFInstrSymbHelper {

	public static Instruction getNextInstructionAndSetPCChoiceLong(ThreadInfo ti, 
			   LCMP instr, 
			   Object op_v1,
			   Object op_v2,
			   Comparator firstComparator,
			   Comparator secondComparator,
			   Comparator thirdComparator) {
	
			throw new UnsupportedOperationException();
	}
	
	public static Instruction getNextInstructionAndSetPCChoiceReal(ThreadInfo ti, 
			   Instruction instr, 
			   Object op_v1,
			   Object op_v2,
			   Comparator firstComparator,
			   Comparator secondComparator,
			   Comparator thirdComparator) {
			throw new UnsupportedOperationException();
	}

	//handles symbolic integer if-instructions with a single operand
	public static Instruction getNextInstructionAndSetPCChoice(ThreadInfo ti,
															   IfInstruction instr,
															   Object op_v,
															   Comparator trueComparator,
															   Comparator falseComparator){
		throw new UnsupportedOperationException();

	}

}
