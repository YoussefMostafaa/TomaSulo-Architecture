import java.util.ArrayList;

public class LoadBuffers {
	ArrayList<LoadBuffer> buffers ;
	boolean full = false ;
	int size = 0 ;
	
	public LoadBuffers(int size) {
		buffers = new ArrayList<LoadBuffer>(size);
		for(int i = 1 ; i <= size ; i++) {
			buffers.add(new LoadBuffer("L"+i)) ;
		}
	}
	
	
	public String toString() {
		String res ="";
		for(LoadBuffer s : this.buffers) {
			res+=("\n--------------------------------------\n"+s);
		}
		return res ;
	}
	
	
	
	
}
