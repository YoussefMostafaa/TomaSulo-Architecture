
public class LoadBuffer {

	public String tag ;
	public int address  ;
	public boolean busy  ;
	public int latency ;
	public Instruction instruction ;
	public int startexec = -1 ;
	public int endexec = -1 ;
	//public boolean finished = false ;
	//public boolean written = false;

	
	public LoadBuffer(String tag) {
		this.busy = false ;
		this.tag = tag ;
	}
	
	
	public String toString() {		
	return "Tag : "+this.tag+"   Address : "+this.address+"    Busy : "+this.busy ;
	}
	
	
}
