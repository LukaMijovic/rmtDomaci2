package covid;

import util.Pol;

public class Korisnik implements KorisnikInterfejs{
	private String ime;
	private String prezime;
	private String sifra;
	private String email;
	private String jmbg;
	private Pol pol;
	private Vakcina[] vakcine = new Vakcina[3];
	
	public Korisnik(String ime, String prezime, String sifra, String email, String jmbg, Pol pol) {
		super();
		if (ime != null && ime != "") {
			this.ime = ime;
		}
		if (prezime != null && prezime != "") {
			this.prezime = prezime;
		}
		if (sifra != null && sifra != "") {
			this.sifra = sifra;
		}
		if (email != null && email != "" && email.contains("@")) {
			this.email = email;
		}
		if (jmbg != null && jmbg != "") {
			this.jmbg = jmbg;
		}
		this.pol = pol;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		if (ime != null && ime != "") {
			this.ime = ime;
		} else {
			System.err.println("Unesite validno ime!");
		}
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		if (prezime != null && prezime != "") {
			this.prezime = prezime;
		} else {
			System.err.println("Unesite validno prezime!");
		}
	}

	public String getSifra() {
		return sifra;
	}

	public void setSifra(String sifra) {
		if (sifra != null && sifra != "") {
			this.sifra = sifra;
		} else {
			System.err.println("Sifra ne moze da bude 0 karaktera!");
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email != null && email != "" && email.contains("@")) {
			this.email = email;
		} else {
			System.err.println("Ne ispravan format email adrese");
		}
	}

	public String getJmbg() {
		return jmbg;
	}

	public void setJmbg(String jmbg) {
		if (jmbg != null && jmbg != "") {
			this.jmbg = jmbg;
		} else {
			System.err.println("Unesite validan JMBG");
		}
	}

	public Pol getPol() {
		return pol;
	}

	public void setPol(Pol pol) {
		this.pol = pol;
	}

	public Vakcina[] getVakcine() {
		return vakcine;
	}

	public void setVakcine(Vakcina vakcina) {
		if (vakcina == null) {
			System.err.println("Neuspesan upis vakcine!");
			return;
		}
		for (int i=0; i<vakcine.length; i++) {
			if (vakcine[i] == null) {
				vakcine[i] = vakcina;
				return;
			}
		}
		System.err.println("Neuspesan upis vakcine!");
	}

	@Override
	public Korisnik registracija(String ime, String prezime, String sifra, String jmbg, Pol pol, String email) {
		// TODO Auto-generated method stub
		return new Korisnik(ime, prezime, sifra, jmbg, email, pol);
	}

//	@Override
//	public void prijava(String email, String sifra) {
//		// TODO Auto-generated method stub
//		
//	}
}
