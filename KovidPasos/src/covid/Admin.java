package covid;

import java.io.Serializable;

public class Admin implements Serializable {

	private String username;
	private String sifra;
	
	public Admin(String username, String sifra) {
		super();
		this.username = username;
		this.sifra = sifra;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		if (username != null) {
			this.username = username;
		}
	}
	public String getSifra() {
		return sifra;
	}
	public void setSifra(String sifra) {
		if (sifra != null) {
			this.sifra = sifra;
		}
	}
	
	
	
}
