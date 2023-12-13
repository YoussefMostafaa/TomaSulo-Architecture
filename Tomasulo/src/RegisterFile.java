
public class RegisterFile {
	Register[] integerregisters ;
	Register[] floatingregisters ;
	
	
	public RegisterFile() {
		integerregisters = new Register[32] ;
		floatingregisters = new Register[32] ;
		for(int i = 0 ; i<32;i++) {
			floatingregisters[i] = new Register("F"+i);
			integerregisters[i] = new Register("R"+i);
		}
	}
	
	public String toString() {
		String res = "";
		System.out.println("Register File \n ----------------------------------- \n");
		for(int i = 0 ; i < integerregisters.length ; i++) {
			System.out.println(integerregisters[i] +"     |     "+floatingregisters[i]);
		}
		return res;
	}
	
}
