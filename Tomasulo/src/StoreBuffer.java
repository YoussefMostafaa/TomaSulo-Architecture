
public class StoreBuffer {
	public String tag ;
	public int address ;
	public double V ;
	public String Q ;
	public boolean busy  ;
	public int latency ;
	public Instruction instruction ;
	public int startexec = -1 ;
	public int endexec = -1 ;
	//public boolean finished ;
//	public boolean written = false;
	
	public StoreBuffer(String tag){
		this.tag = tag ;
		busy = false ;
	}
	
	
	
	
	public String toString() {		
	return "Tag : "+this.tag+"   Address : "+this.address+"    V : "+this.V+"     Q : "+this.Q+"    Busy : "+this.busy ;
	}
}
