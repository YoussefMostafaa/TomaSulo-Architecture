import java.util.ArrayList;

public class MULReservationStation {
	public ArrayList<Station> stations ;
	public boolean full = false ;
	public int size = 0 ;
	
	public MULReservationStation(int size) {
		stations = new ArrayList<>(size);
	//initialize MULReservationStation
	for (int i = 1 ; i <= size ; i++) {
		stations.add(new Station("M"+i));
	}
	}
	
	public String toString() {
		String res ="";
		for(Station s : this.stations) {
			res+=("\n--------------------------------------\n"+s);
		}
		return res ;
	}
	
	
	
	
}
