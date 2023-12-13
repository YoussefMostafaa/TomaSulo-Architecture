import java.util.ArrayList;
import java.util.Collections;

public class CPU {

	public ArrayList<Instruction> instructionmemory ;
	public int pc ;
	public MULReservationStation mulreservationstation ;
	public ADDReservationStation addreservationstation ;
	public LoadBuffers loadbuffers ;
	public StoreBuffers storebuffers ;
	public RegisterFile registerfile ;
	public int clkcycle = 1 ;
	public double[] memory ;
	public ArrayList<Bus> bus ;
	public Bus chosenbus = new Bus() ;
	public boolean done = false;
	//public boolean donebnez = false ;
	public boolean bnez = false;
	
	public CPU(ArrayList<Instruction> instmem,int mulsize,int addsize,int loadsize,int storesize ) {
		this.instructionmemory = instmem;
		pc = 0 ;
		mulreservationstation = new MULReservationStation(mulsize);
		addreservationstation = new ADDReservationStation(addsize);
		loadbuffers = new LoadBuffers(loadsize);
		storebuffers = new StoreBuffers(storesize);
		registerfile = new RegisterFile() ;
		memory = new double[1024];
		bus = new ArrayList<>();

	}
	
	public void start() {
		//testing values
		registerfile.floatingregisters[20].value = 1.5 ;
		registerfile.floatingregisters[0].value = 2 ;
		registerfile.floatingregisters[3].value = 4 ;
		registerfile.floatingregisters[5].value = 5 ;
		registerfile.floatingregisters[6].value = 6 ;
		registerfile.integerregisters[0].value = 2;
		memory[4] = 4 ;
		memory[5]=5;
		while(!done  ) {
			done = false;
			execute();
			write();
			if(!chosenbus.branch)
				System.out.println("CLK CYLCE : "+clkcycle+ " PC : "+pc);
			if(bnez) {
				System.out.println("CANNOT ISSUE MORE INSTRUCTIONS TILL BRANCH OUTCOME IN KNOWN !!");
				--pc;
			}
			else
				issue();
			if(pc < this.instructionmemory.size())
				pc++;
			clkcycle++;
			if(!chosenbus.branch) {
				System.out.println(registerfile);
				System.out.println(addreservationstation);
				System.out.println(mulreservationstation);
				System.out.println(loadbuffers);
				System.out.println(storebuffers);
				printmemory();
					
			}
			
			
			
		}
		
		
		
	}
	
	public void issue() {
		if(this.pc >= this.instructionmemory.size()) {
			System.out.println("No More Instructions to Issue");
			return ;
		}
		
		if (instructionmemory.get(pc).type.equals((InstructionType.ADD)) ||
				instructionmemory.get(pc).type.equals((InstructionType.SUB)) || 
				instructionmemory.get(pc).type.equals((InstructionType.ADDI)) ||
				instructionmemory.get(pc).type.equals((InstructionType.SUBI)) || 
				instructionmemory.get(pc).type.equals((InstructionType.BNEZ)) ||
				instructionmemory.get(pc).type.equals((InstructionType.DADD))) {
			if(addreservationstation.full) {
				System.out.println("Add Reservation Station FULL , Cannot Issue "+instructionmemory.get(pc) +" now");
				--pc;
				return ;
			}
			++addreservationstation.size ;
			insertintoaddrs(instructionmemory.get(pc));
		}else if (instructionmemory.get(pc).type.equals((InstructionType.MUL)) ||
				instructionmemory.get(pc).type.equals((InstructionType.DIV))   ) {
			///Continue here
			if(mulreservationstation.full) {
				System.out.println("Mul Reservation Station FULL , Cannot Issue "+instructionmemory.get(pc) +" now");
				--pc;
				return ;
			}
			++mulreservationstation.size ;
			insertintomulrs(instructionmemory.get(pc));
		}
		else if(instructionmemory.get(pc).type.equals(  (InstructionType.LOAD ) ) ) {
			if(loadbuffers.full) {
				System.out.println("Load Buffer FULL , Cannot Issue "+instructionmemory.get(pc) +" now");
				--pc;
				return ;
			}
			insertintoloadbuffer(instructionmemory.get(pc));
		}
		else if(instructionmemory.get(pc).type.equals(  (InstructionType.STORE ) ) ) {
			if(storebuffers.full) {
				System.out.println("Store Buffer FULL , Cannot Issue "+instructionmemory.get(pc) +" now");
				--pc;
				return ;
			}
			insertintostorebuffer(instructionmemory.get(pc));
		}
	}
	
	public void insertintoaddrs(Instruction instruction) {
		int jindex; int kindex ;int resultindex ;
		if(addreservationstation.size ==addreservationstation.stations.size())
			addreservationstation.full = true ;
		for(int i=0 ; i<addreservationstation.stations.size();i++) {
			if(!addreservationstation.stations.get(i).busy) {
				if(instruction.type != InstructionType.BNEZ)
					System.out.println("Issued "+instruction.type +" "+instruction.rd + " "+instruction.rs+" "+instruction.rt);
				addreservationstation.stations.get(i).instruction = instruction ;
				addreservationstation.stations.get(i).busy = true ;
				switch(instruction.type) {
				case DADD : //ADD.D
					addreservationstation.stations.get(i).op = Operation.ADD;
					addreservationstation.stations.get(i).latency = instruction.latency ;
					 jindex = Integer.parseInt(instruction.rs.substring(1));
					kindex = Integer.parseInt(instruction.rt.substring(1));
					resultindex = Integer.parseInt(instruction.rd.substring(1));
					if(registerfile.integerregisters[jindex].Q == null ) {
						addreservationstation.stations.get(i).Vj = registerfile.integerregisters[jindex].value ;
					}
					else {
						addreservationstation.stations.get(i).Qj = registerfile.integerregisters[jindex].Q ;
					}
					if(registerfile.integerregisters[kindex].Q == null ) {
						addreservationstation.stations.get(i).Vk = registerfile.integerregisters[kindex].value ;
					}
					else {
						addreservationstation.stations.get(i).Qk = registerfile.integerregisters[kindex].Q ;
					}
					registerfile.integerregisters[resultindex].Q = addreservationstation.stations.get(i).tag ;
					return ;
				case ADD : //ADD.D
					addreservationstation.stations.get(i).op = Operation.ADD;
					addreservationstation.stations.get(i).latency = instruction.latency ;
					 jindex = Integer.parseInt(instruction.rs.substring(1));
					kindex = Integer.parseInt(instruction.rt.substring(1));
					resultindex = Integer.parseInt(instruction.rd.substring(1));
					if(registerfile.floatingregisters[jindex].Q == null ) {
						addreservationstation.stations.get(i).Vj = registerfile.floatingregisters[jindex].value ;
					}
					else {
						addreservationstation.stations.get(i).Qj = registerfile.floatingregisters[jindex].Q ;
					}
					if(registerfile.floatingregisters[kindex].Q == null ) {
						addreservationstation.stations.get(i).Vk = registerfile.floatingregisters[kindex].value ;
					}
					else {
						addreservationstation.stations.get(i).Qk = registerfile.floatingregisters[kindex].Q ;
					}
					registerfile.floatingregisters[resultindex].Q = addreservationstation.stations.get(i).tag ;
					return ;
				case SUB :
					addreservationstation.stations.get(i).op = Operation.SUB;
					addreservationstation.stations.get(i).latency = instruction.latency ;
					 jindex = Integer.parseInt(instruction.rs.substring(1));
					 kindex = Integer.parseInt(instruction.rt.substring(1));
					 resultindex = Integer.parseInt(instruction.rd.substring(1));
					if(registerfile.floatingregisters[jindex].Q == null ) {
						addreservationstation.stations.get(i).Vj = registerfile.floatingregisters[jindex].value ;
					}
					else {
						addreservationstation.stations.get(i).Qj = registerfile.floatingregisters[jindex].Q ;
					}
					if(registerfile.floatingregisters[kindex].Q == null ) {
						addreservationstation.stations.get(i).Vk = registerfile.floatingregisters[kindex].value ;
					}
					else {
						addreservationstation.stations.get(i).Qk = registerfile.floatingregisters[kindex].Q ;
					}
					registerfile.floatingregisters[resultindex].Q = addreservationstation.stations.get(i).tag ;
					return ;
				case ADDI :
					addreservationstation.stations.get(i).op = Operation.ADD;
					addreservationstation.stations.get(i).latency = instruction.latency ;
					 jindex = Integer.parseInt(instruction.rs.substring(1));
					 kindex = Integer.parseInt(instruction.rt);
					 resultindex = Integer.parseInt(instruction.rd.substring(1));
					if(registerfile.integerregisters[jindex].Q == null ) {
						addreservationstation.stations.get(i).Vj = registerfile.integerregisters[jindex].value ;
					}
					else {
						addreservationstation.stations.get(i).Qj = registerfile.integerregisters[jindex].Q ;
					}
						addreservationstation.stations.get(i).Vk = kindex ;
					
					registerfile.integerregisters[resultindex].Q = addreservationstation.stations.get(i).tag ;
					return ;
				case SUBI :
					addreservationstation.stations.get(i).op = Operation.SUB;
					addreservationstation.stations.get(i).latency = instruction.latency ;
					 jindex = Integer.parseInt(instruction.rs.substring(1));
					 kindex = Integer.parseInt(instruction.rt);
					 resultindex = Integer.parseInt(instruction.rd.substring(1));
					if(registerfile.integerregisters[jindex].Q == null ) {
						addreservationstation.stations.get(i).Vj = registerfile.integerregisters[jindex].value ;
					}
					else {
						addreservationstation.stations.get(i).Qj = registerfile.integerregisters[jindex].Q ;
					}
						addreservationstation.stations.get(i).Vk = kindex ;
					
					registerfile.integerregisters[resultindex].Q = addreservationstation.stations.get(i).tag ;
					return ;
				case BNEZ :
					System.out.println("Issued "+instruction.type +" "+instruction.rd + " 0");
					bnez = true ;
					addreservationstation.stations.get(i).op = Operation.BNEZ;
					addreservationstation.stations.get(i).latency = instruction.latency ;
					 jindex = Integer.parseInt(instruction.rd.substring(1));
					// kindex = Integer.parseInt(instruction.rt);
					// resultindex = Integer.parseInt(instruction.rd.substring(1)); malhash lazma , kda kda branch to 1st line
					Register register = (instruction.rd.charAt(0) == 'F' ? registerfile.floatingregisters[jindex] : registerfile.integerregisters[jindex]);
					 if(register.Q == null ) {
						addreservationstation.stations.get(i).Vj = register.value ;
					}
					else {
						addreservationstation.stations.get(i).Qj = register.Q ;
					}
						addreservationstation.stations.get(i).Vk = 0 ;
					return ;
				default : break ;
				}
			}
		}
	}
	
	public void insertintomulrs(Instruction instruction) {
		int jindex; int kindex ;int resultindex ;
		if(mulreservationstation.size ==mulreservationstation.stations.size())
			mulreservationstation.full = true ;
		for(int i=0 ; i<mulreservationstation.stations.size();i++) {
			if(!mulreservationstation.stations.get(i).busy) {
				System.out.println("Issued "+instruction.type +" "+instruction.rd + " "+instruction.rs+" "+instruction.rt);

				mulreservationstation.stations.get(i).instruction = instruction ;
				mulreservationstation.stations.get(i).busy = true ;
				if(instruction.type.equals(InstructionType.MUL))
					mulreservationstation.stations.get(i).op = Operation.MUL;
				else
					mulreservationstation.stations.get(i).op = Operation.DIV;
				mulreservationstation.stations.get(i).latency = instruction.latency ;
				 jindex = Integer.parseInt(instruction.rs.substring(1));
				kindex = Integer.parseInt(instruction.rt.substring(1));
				resultindex = Integer.parseInt(instruction.rd.substring(1));
				if(registerfile.floatingregisters[jindex].Q == null ) {
					mulreservationstation.stations.get(i).Vj = registerfile.floatingregisters[jindex].value ;
				}
				else {
					mulreservationstation.stations.get(i).Qj = registerfile.floatingregisters[jindex].Q ;
				}
				if(registerfile.floatingregisters[kindex].Q == null ) {
					mulreservationstation.stations.get(i).Vk = registerfile.floatingregisters[kindex].value ;
				}
				else {
					mulreservationstation.stations.get(i).Qk = registerfile.floatingregisters[kindex].Q ;
				}
				registerfile.floatingregisters[resultindex].Q = mulreservationstation.stations.get(i).tag ;
				return ;
			}
		}
	}
	
	public void insertintoloadbuffer(Instruction instruction) {
	int resultindex ;
	
	for(int i=0 ; i<storebuffers.buffers.size();i++) {
		StoreBuffer sb = storebuffers.buffers.get(i) ;
		if(sb.address == instruction.offset) {
			System.out.println("CANNOT issue LOAD after STORE with same mem address !! RAW HAZARD");
			--pc;
			return ;
		}
	}
	
	++loadbuffers.size ;
		if(loadbuffers.size ==loadbuffers.buffers.size())
			loadbuffers.full = true ;
		for(int i=0 ; i<loadbuffers.buffers.size();i++) {
			if(!loadbuffers.buffers.get(i).busy) {
				System.out.println("Issued "+instruction.type +" "+instruction.rd + " , "+instruction.offset);
				loadbuffers.buffers.get(i).instruction = instruction ;
				loadbuffers.buffers.get(i).busy = true ;
				loadbuffers.buffers.get(i).latency = instruction.latency ;
				resultindex = Integer.parseInt(instruction.rd.substring(1));
				loadbuffers.buffers.get(i).address= instruction.offset ;
				registerfile.floatingregisters[resultindex].Q = loadbuffers.buffers.get(i).tag ;
				return ;
			}
		}
	}
	
	public void insertintostorebuffer(Instruction instruction) {
		int resultindex ;
		
		for(int i=0 ; i<storebuffers.buffers.size();i++) {
			StoreBuffer sb = storebuffers.buffers.get(i) ;
			if(sb.address == instruction.offset) {
				System.out.println("CANNOT issue STORE after STORE with same mem address !! WAW HAZARD");
				--pc;
				return ;
			}
		}
		for(int i=0 ; i<loadbuffers.buffers.size();i++) {
			LoadBuffer lb = loadbuffers.buffers.get(i) ;
			if(lb.address == instruction.offset) {
				System.out.println("CANNOT issue STORE after LOAD with same mem address !! WAR HAZARD");
				--pc;
				return ;
			}
		}
		
		++storebuffers.size ;
		if(storebuffers.size ==storebuffers.buffers.size())
			storebuffers.full = true ;
		for(int i=0 ; i<storebuffers.buffers.size();i++) {
			if(!storebuffers.buffers.get(i).busy) {
				System.out.println("Issued "+instruction.type +" "+instruction.rd + " , "+instruction.offset);

				storebuffers.buffers.get(i).instruction = instruction ;
				storebuffers.buffers.get(i).busy = true ;
				storebuffers.buffers.get(i).latency = instruction.latency ;
				resultindex = Integer.parseInt(instruction.rd.substring(1));
				storebuffers.buffers.get(i).address= instruction.offset ;
				if(registerfile.floatingregisters[resultindex].Q == null)
					storebuffers.buffers.get(i).V= registerfile.floatingregisters[resultindex].value ;
				else
					storebuffers.buffers.get(i).Q= registerfile.floatingregisters[resultindex].Q ;
				return ;
			}
		}
	}

	public void execute() {
		execadd();
		execmul();
		execload();
		execstore();
	}

	public void execadd() {
		for (int i = 0 ; i < addreservationstation.stations.size() ; i++) {
			Station current = addreservationstation.stations.get(i) ;
			if(chosenbus.tag != null && current.tag == chosenbus.tag) { //reset station
				addreservationstation.size -- ;
				addreservationstation.full = false ;
				addreservationstation.stations.get(i).busy = false ;
				addreservationstation.stations.get(i).startexec = -1 ;
				addreservationstation.stations.get(i).endexec = -1 ;
			//	addreservationstation.stations.get(i).finished = false ;
				addreservationstation.stations.get(i).Qj = null ;
				addreservationstation.stations.get(i).Qk = null ;
				addreservationstation.stations.get(i).Vj = 0 ;
				addreservationstation.stations.get(i).Vk = 0 ;
				addreservationstation.stations.get(i).latency = -1 ;
				addreservationstation.stations.get(i).op = null ;
				chosenbus.tag = null ;
			}
			if(current.busy && current.startexec == -1 && current.Qj == null && current.Qk == null) {
				addreservationstation.stations.get(i).startexec = this.clkcycle;
				addreservationstation.stations.get(i).endexec = this.clkcycle + addreservationstation.stations.get(i).latency -1 ;
				System.out.println("Started Executing : "+ current.instruction.type +" "+current.instruction.rd + " "+current.instruction.rs+" "+current.instruction.rt);
			}
			else  if(current.busy && current.startexec > -1 && current.endexec > this.clkcycle) 
					System.out.println("Currently Executing : "+current.instruction.type +" "+current.instruction.rd + " "+current.instruction.rs+" "+current.instruction.rt);
				
			 if(current.busy && current.startexec > -1 && current.endexec == this.clkcycle) {
				 System.out.println("Finished Execution of : "+current.instruction.type +" "+current.instruction.rd + " "+current.instruction.rs+" "+current.instruction.rt);	 
			//	 addreservationstation.stations.get(i).finished = true ;
			 }
	}
}

	public void execmul() {
		for (int i = 0 ; i < mulreservationstation.stations.size() ; i++) {
			Station current = mulreservationstation.stations.get(i) ;
			if(chosenbus.tag != null && current.tag == chosenbus.tag) { //reset station
				mulreservationstation.size -- ;
				mulreservationstation.full = false ;
				mulreservationstation.stations.get(i).busy = false ;
				mulreservationstation.stations.get(i).startexec = -1 ;
				mulreservationstation.stations.get(i).endexec = -1 ;
				//mulreservationstation.stations.get(i).finished = false ;
				mulreservationstation.stations.get(i).Qj = null ;
				mulreservationstation.stations.get(i).Qk = null ;
				mulreservationstation.stations.get(i).Vj = 0 ;
				mulreservationstation.stations.get(i).Vk = 0 ;
				mulreservationstation.stations.get(i).latency = -1 ;
				mulreservationstation.stations.get(i).op = null ;
				chosenbus.tag = null ;
			}
			if(current.busy && current.startexec == -1 && current.Qj == null && current.Qk == null) {
				mulreservationstation.stations.get(i).startexec = this.clkcycle;
				mulreservationstation.stations.get(i).endexec = this.clkcycle + mulreservationstation.stations.get(i).latency -1 ;
				System.out.println("Started Executing : "+ current.instruction.type +" "+current.instruction.rd + " "+current.instruction.rs+" "+current.instruction.rt);
			}
			else  if(current.busy && current.startexec > -1 && current.endexec > this.clkcycle) 
					System.out.println("Currently Executing : "+current.instruction.type +" "+current.instruction.rd + " "+current.instruction.rs+" "+current.instruction.rt);
				
			 if(current.busy && current.startexec > -1 && current.endexec == this.clkcycle) {
				 System.out.println("Finished Execution of : "+current.instruction.type +" "+current.instruction.rd + " "+current.instruction.rs+" "+current.instruction.rt);	 
			//	 mulreservationstation.stations.get(i).finished = true ;
			 }
	}
}

	public void execload() {
		for (int i = 0 ; i < loadbuffers.buffers.size() ; i++) {
			LoadBuffer current = loadbuffers.buffers.get(i) ;
			if(chosenbus.tag != null && current.tag == chosenbus.tag) { //reset station
				loadbuffers.size -- ;
				loadbuffers.full = false ;
				loadbuffers.buffers.get(i).busy = false ;
				loadbuffers.buffers.get(i).startexec = -1 ;
				loadbuffers.buffers.get(i).endexec = -1 ;
				loadbuffers.buffers.get(i).address = 0;
				loadbuffers.buffers.get(i).latency = -1 ;
				chosenbus.tag = null ;
			}
			if(current.busy && current.startexec == -1 ) {
				loadbuffers.buffers.get(i).startexec = this.clkcycle;
				loadbuffers.buffers.get(i).endexec = this.clkcycle + loadbuffers.buffers.get(i).latency -1 ;
				System.out.println("Started Executing : "+ current.instruction.type +" "+current.instruction.rd + " , "+current.address);
			}
			else  if(current.busy && current.startexec > -1 && current.endexec > this.clkcycle) 
				System.out.println("Currently Executing : "+ current.instruction.type +" "+current.instruction.rd + " , "+current.address);
				
			 if(current.busy && current.startexec > -1 && current.endexec == this.clkcycle) {
				 System.out.println("Finished Execution of : "+ current.instruction.type +" "+current.instruction.rd + " , "+current.address);
				// loadbuffers.buffers.get(i).finished = true ;
			 }
	}
	}

	public void execstore() {
		for (int i = 0 ; i < storebuffers.buffers.size() ; i++) {
			StoreBuffer current = storebuffers.buffers.get(i) ;
			if(chosenbus.tag != null && current.tag == chosenbus.tag) { //reset station
				storebuffers.size -- ;
				storebuffers.full = false ;
				storebuffers.buffers.get(i).busy = false ;
				storebuffers.buffers.get(i).startexec = -1 ;
				storebuffers.buffers.get(i).endexec = -1 ;
				storebuffers.buffers.get(i).address = 0;
				storebuffers.buffers.get(i).latency = -1 ;
				storebuffers.buffers.get(i).Q = null;
				storebuffers.buffers.get(i).V = 0 ;
				chosenbus.tag = null ;
				chosenbus.memaddress = -1 ;
			}
			if(current.busy && current.startexec == -1 && current.Q == null  ) {
				storebuffers.buffers.get(i).startexec = this.clkcycle;
				storebuffers.buffers.get(i).endexec = this.clkcycle + storebuffers.buffers.get(i).latency -1 ;
				System.out.println("Started Executing : "+ current.instruction.type +" "+current.instruction.rd + " , "+current.address);
			}
			else  if(current.busy && current.startexec > -1 && current.endexec > this.clkcycle) 
				System.out.println("Currently Executing : "+ current.instruction.type +" "+current.instruction.rd + " , "+current.address);
				
			 if(current.busy && current.startexec > -1 && current.endexec == this.clkcycle) {
				 System.out.println("Finished Execution of : "+ current.instruction.type +" "+current.instruction.rd + " , "+current.address);
				// storebuffers.buffers.get(i).finished = true ;

			 }
	}
	}
	
	public void write() {
		writeadd();
		writemul();
		writeload();
		writestore();
		gobus();
		if((this.pc >= this.instructionmemory.size() && 
				(addreservationstation.size+mulreservationstation.size
						+storebuffers.size+loadbuffers.size == 1) &&
				bus.size() == 1 )  
				)
			done = true ;
		 bus = new ArrayList<>(); //reset bus
	}
	
	public void writeadd() {
		for (int i = 0 ; i < addreservationstation.stations.size() ; i++) {
			Station current = addreservationstation.stations.get(i) ;
			if(current.busy && current.startexec > -1 && current.endexec < clkcycle) {
				if(current.op.equals(Operation.ADD) ) {
					bus.add(new Bus(current.tag, current.Vj+current.Vk,current.startexec));
				}
				else if(current.op.equals(Operation.SUB) ) {
					bus.add(new Bus(current.tag, current.Vj-current.Vk,current.startexec));
				}
				else if(current.op.equals(Operation.BNEZ) ) {
					if( current.Vj != 0) 
						{
						pc = 0 ;
						}
					else if(pc == instructionmemory.size()  )
					{
						chosenbus.branch = true ;
						done = true ;
					}
					bnez = false ;
					addreservationstation.size -- ;
					addreservationstation.full = false ;
					addreservationstation.stations.get(i).busy = false ;
					addreservationstation.stations.get(i).startexec = -1 ;
					addreservationstation.stations.get(i).endexec = -1 ;
				//	addreservationstation.stations.get(i).finished = false ;
					addreservationstation.stations.get(i).Qj = null ;
					addreservationstation.stations.get(i).Qk = null ;
					addreservationstation.stations.get(i).Vj = 0 ;
					addreservationstation.stations.get(i).Vk = 0 ;
					addreservationstation.stations.get(i).latency = -1 ;
					addreservationstation.stations.get(i).op = null ;
					//chosenbus.branch = true ;
					//chosenbus.tag = null ;
				}		
				
	}
	}
	}

	public void writemul() {
		for (int i = 0 ; i < mulreservationstation.stations.size() ; i++) {
			Station current = mulreservationstation.stations.get(i) ;
			if(current.busy && current.startexec > -1 &&  current.endexec < clkcycle) {
				//mulreservationstation.stations.get(i).written = true ;
				if(current.op.equals(Operation.MUL) ) {
					bus.add(new Bus(current.tag, current.Vj*current.Vk,current.startexec));
				}
				else if(current.op.equals(Operation.DIV) ) {
					bus.add(new Bus(current.tag, current.Vj/current.Vk,current.startexec));
				}
				
	}
	}
	}
	
	public void writeload() {
		for (int i = 0 ; i < loadbuffers.buffers.size() ; i++) {
			LoadBuffer current = loadbuffers.buffers.get(i) ;
			if(current.busy && current.startexec > -1 && current.endexec < clkcycle) {
					bus.add(new Bus(current.tag, this.memory[current.address],current.startexec));
	}
	}
	}

	public void writestore() {
		for (int i = 0 ; i < storebuffers.buffers.size() ; i++) {
			StoreBuffer current = storebuffers.buffers.get(i) ;
			if(current.busy && current.startexec > -1 && current.endexec < clkcycle) {
					bus.add(new Bus(current.tag, current.V,current.startexec,current.address));
	}
	}
	}
	
	public void gobus() {
        if(bus.size() > 0) {
        	Collections.sort(bus);
        	 chosenbus = bus.get(0);
        	 System.out.println("Writing Value of : "+chosenbus.tag);
        	 //check in mem
        	 if(chosenbus.memaddress > -1 ) {
        		 memory[chosenbus.memaddress] = chosenbus.value;
        	 }
        	 //check in addres
        	for(int i = 0 ; i < addreservationstation.stations.size() ; i ++) {
        		if(chosenbus.tag != null && addreservationstation.stations.get(i).Qj == chosenbus.tag) {
        			addreservationstation.stations.get(i).Qj = null ;
        			addreservationstation.stations.get(i).Vj = chosenbus.value ;
        		}
        		if(chosenbus.tag != null &&addreservationstation.stations.get(i).Qk == chosenbus.tag) {
        			addreservationstation.stations.get(i).Qk = null ;
        			addreservationstation.stations.get(i).Vk = chosenbus.value ;
        		}
        	}
        	//check in mulres
        	for(int i = 0 ; i < mulreservationstation.stations.size() ; i ++) {
        		if(chosenbus.tag != null &&mulreservationstation.stations.get(i).Qj == chosenbus.tag) {
        			mulreservationstation.stations.get(i).Qj = null ;
        			mulreservationstation.stations.get(i).Vj = chosenbus.value ;
        		}
        		if(chosenbus.tag != null &&mulreservationstation.stations.get(i).Qk == chosenbus.tag) {
        			mulreservationstation.stations.get(i).Qk = null ;
        			mulreservationstation.stations.get(i).Vk = chosenbus.value ;
        		}
        	}
        	//check in storebuffer
        	for(int i = 0 ; i < storebuffers.buffers.size() ; i ++) {
        		if(chosenbus.tag != null &&storebuffers.buffers.get(i).Q == chosenbus.tag) {
        			storebuffers.buffers.get(i).Q = null ;
        			storebuffers.buffers.get(i).V = chosenbus.value ;
        		}
        	}
        	
           	for(int i = 0 ; i < registerfile.floatingregisters.length ; i ++) {
        		if(chosenbus.tag != null &&registerfile.floatingregisters[i].Q == chosenbus.tag) {
        			registerfile.floatingregisters[i].Q = null ;
        			registerfile.floatingregisters[i].value = chosenbus.value ;
        		}
        		if(chosenbus.tag != null &&registerfile.integerregisters[i].Q == chosenbus.tag) {
        			registerfile.integerregisters[i].Q = null ;
        			registerfile.integerregisters[i].value = chosenbus.value ;
        		}
        	}
        	
        
        }
        
		
	}
	
	public void printmemory() {
		System.out.println(" Memory : \n ----------------------------------------------");
		for (int i = 0 ; i < this.memory.length ; i ++) {
			if(memory[i] != 0) {
				System.out.println("Memory[ "+i+" ] "+" = "+memory[i]);
			}
		}
	}

}
