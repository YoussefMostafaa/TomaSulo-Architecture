import java.util.ArrayList;

public class ADDReservationStation {
	public ArrayList<Station> stations ;
	public boolean full = false ;
	public int size = 0;
	
	public ADDReservationStation(int size) {
		stations = new ArrayList<>(size);
	//initialize MULReservationStation
	for (int i = 1 ; i <= size ; i++) {
		stations.add(new Station("A"+i));
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