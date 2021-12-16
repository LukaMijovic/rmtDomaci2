package covid;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class Vakcina {
	private String naziv;
	private GregorianCalendar datumDoze;
	
	public Vakcina(String naziv) {
		super();
		if (naziv != null && naziv != "") {
			this.naziv = naziv;
		}
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		if (naziv != "" && naziv != null) {
			this.naziv = naziv;
		}
	}

	public GregorianCalendar getDatumiDoza() {
		return datumDoze;
	}

	public void setDatumiDoza(GregorianCalendar datumDoze) {
		this.datumDoze = datumDoze;
	}
}
