
public class Station {
	public String tag = null;
	public Operation op = null;
	public double Vj ;
	public double Vk  ;
	public String Qj = null;
	public String Qk = null ;
	public boolean busy  ;
	public int startexec = -1;
	public int endexec = -1;
	public int latency = -1;
	public Instruction instruction ;
	//public boolean finished = false;
	//public boolean written = false ;
	
	public Station(String tag) {
		this.tag = tag ;
		this.busy = false ;
	}
	
	public String toString() {
		String s = "";
		s+= "TAG: " + tag + " ";
		s+= "busy: " + busy + " ";
		s+= "op: "+ op+ " ";
		s+= "Vj: " + Vj + " ";
		s+= "Vk: " + Vk + " ";
		s+= "Qj: " + Qj + " ";
		s+= "Qk: " + Qk + " ";
		return s;
	}
	
}
