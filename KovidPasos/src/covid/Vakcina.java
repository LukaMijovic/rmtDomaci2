package covid;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class Vakcina implements Serializable {
	private DostupneVakcine naziv;
	private GregorianCalendar datumDoze;
	
	

	public Vakcina(DostupneVakcine naziv, GregorianCalendar datumDoze) {
		super();
		this.naziv = naziv;
		this.datumDoze = datumDoze;
	}

	public GregorianCalendar getDatumiDoza() {
		return datumDoze;
	}

	public void setDatumiDoza(GregorianCalendar datumDoze) {
		this.datumDoze = datumDoze;
	}

	public DostupneVakcine getNaziv() {
		return naziv;
	}

	public void setNaziv(DostupneVakcine naziv) {
		this.naziv = naziv;
	}

	@Override
	public String toString() {
		return "Vakcina [naziv=" + naziv + ", datumDoze=" + datumDoze.getTime() + "]";
	}
}
