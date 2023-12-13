import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;



public class Parser {
	
	public Parser () {
	}
	
	public  ArrayList<Instruction> readMIPS(File code,int addlatency,int sublatency,int mullatency,int divlatency,int loadlatency,int storelatency,int subilatency) throws FileNotFoundException{
		try (//read all instruction
		Scanner Input = new Scanner(code)) {
			ArrayList<String> prog = new ArrayList<String>();
			while (Input.hasNextLine()) {

				String data = Input.nextLine();
				prog.add(data);
			}
			//put instructions in instruction memory
			ArrayList<Instruction> instmem = new ArrayList<>(prog.size());
			for(String instruction : prog) {
				
				
				String[] parameters = instruction.split(",");

				String operationcode = parameters[0].split(" ")[0];
				InstructionType instructionType = null;
				String rd = parameters[0].split(" ")[1];
				String rs = null;
				String rt = null;
				int latency = -1 ;
				int offset = -1;
				if (operationcode.equals("DIV.D")) {
					instructionType = InstructionType.DIV;
					rs = parameters[1];
					rt = parameters[2];
					latency = divlatency ;
				}
				else if (operationcode.equals("DADD")) {
					instructionType = InstructionType.DADD;
					rs = parameters[1];
					rt = parameters[2];
					latency = addlatency ;

				}
				else if (operationcode.equals("ADD.D")) {
					instructionType = InstructionType.ADD;
					rs = parameters[1];
					rt = parameters[2];
					latency = addlatency ;

				} else if (operationcode.equals("SUB.D")) {
					instructionType = InstructionType.SUB;
					rs = parameters[1];
					rt = parameters[2];
					latency = subilatency ;
				} else if (operationcode.equals("MUL.D")) {
					instructionType = InstructionType.MUL;
					rs = parameters[1];
					rt = parameters[2];
					latency = mullatency;
					
				} 
				else if (operationcode.equals("ADDI")) {
					instructionType = InstructionType.ADDI;
					rs = parameters[1];
					rt = parameters[2];
					latency = 1;
					
				} else if (operationcode.equals("SUBI")) {
					instructionType = InstructionType.SUBI;
					rs = parameters[1];
					rt = parameters[2];
					latency = subilatency;
				} 
				
				
				else if (operationcode.equals("L.D")) {
					instructionType = InstructionType.LOAD;
					offset = Integer.parseInt(parameters[1]);
					latency = loadlatency;
				} else if (operationcode.equals("S.D")) {
					instructionType = InstructionType.STORE;
					offset = Integer.parseInt(parameters[1]);
					latency = storelatency ;
				} else if (operationcode.equals("BNEZ")) {
					instructionType = InstructionType.BNEZ ;
					latency = 1 ;
					offset = Integer.parseInt(parameters[1]);
				}
				else {
					System.out.println("INVALID INSTRUCION");
				}
				
				instmem.add( new Instruction(instructionType, rs, rt, rd, offset,latency));
				
				
			}
			return instmem ;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
