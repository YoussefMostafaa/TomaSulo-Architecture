import java.util.ArrayList;

public class StoreBuffers {

	ArrayList<StoreBuffer> buffers ;
	boolean full = false ;
	public int size = 0;
	
	
	public StoreBuffers(int size) {
		buffers = new ArrayList<StoreBuffer>(size);
		for(int i = 1 ; i <= size ; i++) {
			buffers.add(new StoreBuffer("S"+i)) ;
		}
	}
	
	public String toString() {
		String res ="";
		for(StoreBuffer s : this.buffers) {
			res+=("\n--------------------------------------\n"+s);
		}
		return res ;
	}
	
}
