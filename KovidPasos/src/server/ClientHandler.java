package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import covid.Korisnik;
import util.Pol;

public class ClientHandler extends Thread implements Serializable{

	private BufferedReader klijentIn = null;
	private PrintStream klijentOut = null;
	private Socket soketZaKomunikaciju = null;
	public static ArrayList<Korisnik> registrovaniKorisnici = new ArrayList<>();
	
	public ClientHandler(Socket soketZaKomunikaciju) {
		this.soketZaKomunikaciju = soketZaKomunikaciju;
	}
	
	public void run() {
		try (FileInputStream fi = new FileInputStream("registrovani_korisnici.out");
						BufferedInputStream bi = new BufferedInputStream(fi);
						ObjectInputStream oi = new ObjectInputStream(bi)) {
					registrovaniKorisnici = (ArrayList<Korisnik>) oi.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		try {
			klijentIn = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
			klijentOut = new PrintStream(soketZaKomunikaciju.getOutputStream());
			
			int odgovor = -1;
			
			while (odgovor == -1) {
				klijentOut.println(">>> 1. Registracija; 2. Prijava; 0. Izlaz");
//				klijentOut.println(">>> 2. Prijava");
//				klijentOut.println(">>> 0. Izlaz");
				
				odgovor = Integer.parseInt(klijentIn.readLine());
				//System.out.println(odgovor);
				
				switch (odgovor) {
				case 1:
					klijentOut.println(">>>	Ime:");
					String ime = klijentIn.readLine();
					System.out.println(ime);
					
					klijentOut.println(">>> Prezime:");
					String prezime = klijentIn.readLine();
					
					klijentOut.println(">>> Email:");
					String email = klijentIn.readLine();
					
					klijentOut.println(">>> JMBG:");
					String jmbg = klijentIn.readLine();
					
					klijentOut.println(">>> Pol: 1. Muski; 2. Zenski");
					boolean flag = false;
					Pol pol = Pol.Muski;
					while (!flag) {
						int polIndeks = Integer.parseInt(klijentIn.readLine());
						if (polIndeks == 1) {
							pol = Pol.Muski;
							flag = true;
						} else if (polIndeks == 2) {
							pol = Pol.Zenski;
							flag = true;
						} else { 
							klijentOut.println(">>> Neispravan pol! Unesite ponovo...");
						} 
					}
					
					klijentOut.println(">>> Sifra:");
					String sifra = klijentIn.readLine();
					
					Korisnik noviKorisnik = new Korisnik(ime, prezime, sifra, email, jmbg, pol);
					registrovaniKorisnici.add(noviKorisnik);
					
					try (FileOutputStream fo = new FileOutputStream("registrovani_korisnici.out");
							BufferedOutputStream bo = new BufferedOutputStream(fo);
							ObjectOutputStream oo = new ObjectOutputStream(bo)){
						oo.flush();
						oo.writeObject(registrovaniKorisnici);
					}
					klijentOut.println("Gotovo");
					odgovor = -1;
					break;
				case 2:
					
					odgovor = -1;
					break;
				case 0:
					klijentOut.println(">>> Prijatan ostatak dana!");
					soketZaKomunikaciju.close();
					System.out.println(">>> Ugasena konekcija!");
//					odgovor = -1;
					break;
				default:
					
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
