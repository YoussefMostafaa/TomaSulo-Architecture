import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;



public class Main {

	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Parser parser = new Parser();
		//add,sub,mul,div,load,store,subi
		int addlatency = 2 ;int sublatency = 2 ;int mullatency = 3 ;int divlatency = 2 ;int loadlatency = 1 ;int storelatency = 1 ;int subilatency = 2 ;
		ArrayList<Instruction> InstructionMemory = parser.readMIPS(new File("testcode.txt"),addlatency,sublatency,mullatency,divlatency,loadlatency,storelatency,subilatency);
		

		int mulstationsize = 2 ; int addstationsize = 2 ; int loadbuffersize = 2 ; int storebuffersize = 2 ;
		//PrintInstructionMemory(InstructionMemory);
		CPU cpu = new CPU(InstructionMemory, mulstationsize, addstationsize,loadbuffersize,storebuffersize);
		cpu.start();
	}
	
	
	
	public static  void PrintInstructionMemory(ArrayList<Instruction> InstructionMemory) {
		for(Instruction i : InstructionMemory) {
			System.out.println("----------------------------------------\n"+i);
		}
	}
}



