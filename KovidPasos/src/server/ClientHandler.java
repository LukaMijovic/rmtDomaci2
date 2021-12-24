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
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import covid.DostupneVakcine;
import covid.Korisnik;
import covid.Vakcina;
import util.Pol;

public class ClientHandler extends Thread implements Serializable {

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
			System.err.println(">> Fajl ne postoji.");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println(">> Fajl je prazan.");
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
				// System.out.println(odgovor);

				switch (odgovor) {
				case 1:
					klijentOut.println(">>>	Ime:");
					String ime = klijentIn.readLine();
//					System.out.println(ime);

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

					klijentOut.println(">>> Da li ste vakcinisani prvom dozom?");
					klijentOut.println(">>> 1. Da; 2. Ne");
					int flagVax = Integer.parseInt(klijentIn.readLine());

					if (flagVax == 1) {

						klijentOut.println(">>> Koju vakcinu ste primili (prva doza)?");
						klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
						int indeksVakcine = Integer.parseInt(klijentIn.readLine());

						klijentOut.println(">>> Kada ste primili prvu dozu? (D.M.YYYY)");
						String pomocniNiz[] = klijentIn.readLine().split("\\.");
						int dan = Integer.parseInt(pomocniNiz[0]);
						int mesec = Integer.parseInt(pomocniNiz[1]);
						int godina = Integer.parseInt(pomocniNiz[2]);
						GregorianCalendar datumPrveDoze = new GregorianCalendar(godina, mesec - 1, dan);
						Vakcina prvaDoza;

						switch (indeksVakcine) {
						case 1:
							prvaDoza = new Vakcina(DostupneVakcine.Fajzer, datumPrveDoze);
							noviKorisnik.setVakcine(prvaDoza);
							break;
						case 2:
							prvaDoza = new Vakcina(DostupneVakcine.Sinofarm, datumPrveDoze);
							noviKorisnik.setVakcine(prvaDoza);
							break;
						case 3:
							prvaDoza = new Vakcina(DostupneVakcine.Sputnik, datumPrveDoze);
							noviKorisnik.setVakcine(prvaDoza);
							break;

						default:
							break;
						}

						System.out.println(noviKorisnik.getVakcine()[0].toString());

						klijentOut.println(
								">>> Da li ste vakcinisani drugom dozom? (Podrazumeva se da je isti proizvodjac kao i za prvu dozu)");
						klijentOut.println(">>> 1. Da; 2. Ne");
						flagVax = Integer.parseInt(klijentIn.readLine());

						if (flagVax == 1) {

//							klijentOut.println(">>> Koju vakcinu ste primili (druga doza)?");
//							klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
//							int indeksVakcine2 = Integer.parseInt(klijentIn.readLine());

							klijentOut.println(">>> Kada ste primili drugu dozu? (D.M.YYYY)");
							String pomocniNiz2[] = klijentIn.readLine().split("\\.");
							dan = Integer.parseInt(pomocniNiz2[0]);
							mesec = Integer.parseInt(pomocniNiz2[1]);
							godina = Integer.parseInt(pomocniNiz2[2]);

							GregorianCalendar datumDrugeDoze = new GregorianCalendar(godina, mesec - 1, dan);

							if ((datumDrugeDoze.getTimeInMillis() / 604800000
									- datumPrveDoze.getTimeInMillis() / 604800000 >= 3)) {
								Vakcina drugaDoza;

								switch (indeksVakcine) {
								case 1:
									drugaDoza = new Vakcina(DostupneVakcine.Fajzer, datumDrugeDoze);
									noviKorisnik.setVakcine(drugaDoza);
									break;
								case 2:
									drugaDoza = new Vakcina(DostupneVakcine.Sinofarm, datumDrugeDoze);
									noviKorisnik.setVakcine(drugaDoza);
									break;
								case 3:
									drugaDoza = new Vakcina(DostupneVakcine.Sputnik, datumDrugeDoze);
									noviKorisnik.setVakcine(drugaDoza);
									break;

								default:
									break;
								}

								// System.out.println(noviKorisnik.getVakcine()[1].toString());

								klijentOut.println(">>> Da li ste vakcinisani trecom buster dozom?");
								klijentOut.println(">>> 1. Da; 2. Ne");
								flagVax = Integer.parseInt(klijentIn.readLine());

								if (flagVax == 1) {

									klijentOut.println(">>> Koju vakcinu ste primili (prva doza)?");
									klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
									int indeksVakcine3 = Integer.parseInt(klijentIn.readLine());

									klijentOut.println(">>> Kada ste primili prvu dozu? (D.M.YYYY)");
									String pomocniNiz3[] = klijentIn.readLine().split("\\.");
									dan = Integer.parseInt(pomocniNiz3[0]);
									mesec = Integer.parseInt(pomocniNiz3[1]);
									godina = Integer.parseInt(pomocniNiz3[2]);
									GregorianCalendar datumTreceDoze = new GregorianCalendar(godina, mesec - 1, dan);

									if (!(datumDrugeDoze.get(2) - datumPrveDoze.get(2) >= 6)) {
										klijentOut.println(
												">>> Treca doza mora biti primljena minimalno 6 meseci nakon prve! Ponistava se radnja!");
										break;
									}

									Vakcina trecaDoza;

									switch (indeksVakcine3) {
									case 1:
										trecaDoza = new Vakcina(DostupneVakcine.Fajzer, datumPrveDoze);
										noviKorisnik.setVakcine(trecaDoza);
										break;
									case 2:
										trecaDoza = new Vakcina(DostupneVakcine.Sinofarm, datumPrveDoze);
										noviKorisnik.setVakcine(trecaDoza);
										break;
									case 3:
										trecaDoza = new Vakcina(DostupneVakcine.Sputnik, datumPrveDoze);
										noviKorisnik.setVakcine(trecaDoza);
										break;

									default:
										break;
									}

									System.out.println(noviKorisnik.getVakcine()[2].toString());
								}
							} else {
								klijentOut.println(
										">>> Druga doza mora biti primljena minimalno 3 nedelje nakon prve! Ponistava se radnja!");
							}
						}
					}

					registrovaniKorisnici.add(noviKorisnik);

					try (FileOutputStream fo = new FileOutputStream("registrovani_korisnici.out");
							BufferedOutputStream bo = new BufferedOutputStream(fo);
							ObjectOutputStream oo = new ObjectOutputStream(bo)) {
						oo.flush();
						oo.writeObject(registrovaniKorisnici);
					}
					klijentOut.println(">>> Gotovo.");
					odgovor = -1;
					break;
				case 2:
					try (FileInputStream fi = new FileInputStream("registrovani_korisnici.out");
							BufferedInputStream bi = new BufferedInputStream(fi);
							ObjectInputStream oi = new ObjectInputStream(bi)) {
						registrovaniKorisnici = (ArrayList<Korisnik>) oi.readObject();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.err.println(">> Fajl ne postoji.");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						System.err.println(">> Fajl je prazan.");
					}
					
					klijentOut.println(">>> Unesite vasu email adresu");
					String emailKorisnika = klijentIn.readLine();
					klijentOut.println(">>> Unesite vasu sifru");
					String sifraKorisnika = klijentIn.readLine();
					
					boolean flagLogIn = false;
					
					for (Korisnik korisnik : registrovaniKorisnici) {
						if (korisnik.getEmail().equals(emailKorisnika) && korisnik.getSifra().equals(sifraKorisnika)) {
							klijentOut.println(">>> Uspesno ste se prijavili na platformu!");
							flagLogIn = true;
						}
					}
					
					if (!flagLogIn) {
						klijentOut.println(">>> Neupesna prijava! Proverite da li ste dobro uneli adresu i sifru i da li ste se registrovali...");
					}
					
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
			System.err.println(">> Prekinuta konekcija.");
		}
	}
}
