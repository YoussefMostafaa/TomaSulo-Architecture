
public class Instruction {

	InstructionType type ;
	String rs; //1st operand
	String rt; //2nd operand
	String rd; //result
	int offset;
	int startExec;
	int endExec;
	int latency ;
	
	public Instruction(InstructionType Type, String rs, String rt, String rd, int offset,int latency) {
		this.type = Type;
		this.rs = rs;
		this.rt = rt;
		this.rd = rd;
		this.offset = offset;
		startExec = -1;
		this.endExec = -1 ;
		this.latency = latency ;
	}
	


	public String toString() {
		return "type: " + type +
				" rs: " + rs + 
				" rt: " + rt + 
				" rd: " + rd + 
				" offset: " + offset + 
				" startExec: " + startExec+ 
				" endExec: " + endExec;
	}
	
	
}
