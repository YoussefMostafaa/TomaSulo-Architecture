
public class Register {
	public double value ;
	public String Q ;
	public String tag;
	
	public Register(String tag) {
		this.tag = tag ;
	}
	
	public String toString() {
		return "Tag : "+tag+"     Q : "+Q+"        Value : "+value;
	}
}
