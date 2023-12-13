
public class Bus implements Comparable<Bus> {
	
	String tag ;
	double value ;
	int startexec ;
	int memaddress ;
	boolean branch = false ;
	public Bus (String tag , double value,int startexec) {
		this.tag = tag ;
		this.value = value ;
		this.startexec = startexec ;
		this.memaddress = -1 ;
	}
	public Bus (String tag , double value,int startexec,int mem) {
		this.tag = tag ;
		this.value = value ;
		this.startexec = startexec ;
		this.memaddress = mem ;
	}
	public Bus() {
		this.memaddress = -1 ;
	}
    public int compareTo(Bus other) {
        return Integer.compare(this.startexec, other.startexec);
    }
}
