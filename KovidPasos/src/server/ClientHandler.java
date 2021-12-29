package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import covid.Admin;
import covid.DostupneVakcine;
import covid.Korisnik;
import covid.Vakcina;
import util.Pol;

public class ClientHandler extends Thread implements Serializable {

	private BufferedReader klijentIn = null;
	private PrintStream klijentOut = null;
	private Socket soketZaKomunikaciju = null;
	public static ArrayList<Korisnik> registrovaniKorisnici = new ArrayList<>();
	public static ArrayList<Admin> admini = new ArrayList<>();

	public ClientHandler(Socket soketZaKomunikaciju) {
		this.soketZaKomunikaciju = soketZaKomunikaciju;
	}

	public void run() {
		for (int i = 1; i < 4; i++) {
			admini.add(new Admin("admin" + i, "sifra" + i));
		}
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
				klijentOut.println(">>> 1. Registracija; 2. Prijava; 3. Prijava za Admina; 0. Izlaz");
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

									klijentOut.println(">>> Koju vakcinu ste primili (treca doza)?");
									klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
									int indeksVakcine3 = Integer.parseInt(klijentIn.readLine());

									klijentOut.println(">>> Kada ste primili trecu dozu? (D.M.YYYY)");
									String pomocniNiz3[] = klijentIn.readLine().split("\\.");
									dan = Integer.parseInt(pomocniNiz3[0]);
									mesec = Integer.parseInt(pomocniNiz3[1]);
									godina = Integer.parseInt(pomocniNiz3[2]);
									GregorianCalendar datumTreceDoze = new GregorianCalendar(godina, mesec - 1, dan);

									if (!(datumTreceDoze.get(2) - datumDrugeDoze.get(2) >= 6)) {
										klijentOut.println(
												">>> Treca doza mora biti primljena minimalno 6 meseci nakon druge! Ponistava se radnja!");
										break;
									}

									Vakcina trecaDoza;

									switch (indeksVakcine3) {
									case 1:
										trecaDoza = new Vakcina(DostupneVakcine.Fajzer, datumTreceDoze);
										noviKorisnik.setVakcine(trecaDoza);
										break;
									case 2:
										trecaDoza = new Vakcina(DostupneVakcine.Sinofarm, datumTreceDoze);
										noviKorisnik.setVakcine(trecaDoza);
										break;
									case 3:
										trecaDoza = new Vakcina(DostupneVakcine.Sputnik, datumTreceDoze);
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

							klijentOut.println(">>> 1. Popunite vakcine; 2. Pristup kovid propusnici");
							int indeksOdgovor = Integer.parseInt(klijentIn.readLine());

							switch (indeksOdgovor) {
							case 1:
								if (korisnik.getVakcine()[0] == null) {
									klijentOut.println(">>> Da li ste vakcinisani prvom dozom?");
									klijentOut.println(">>> 1. Da; 2. Ne");
									flagVax = Integer.parseInt(klijentIn.readLine());
									int indeksVakcine = -1;

									if (flagVax == 1) {

										klijentOut.println(">>> Koju vakcinu ste primili (prva doza)?");
										klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
										indeksVakcine = Integer.parseInt(klijentIn.readLine());

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
											korisnik.setVakcine(prvaDoza);
											break;
										case 2:
											prvaDoza = new Vakcina(DostupneVakcine.Sinofarm, datumPrveDoze);
											korisnik.setVakcine(prvaDoza);
											break;
										case 3:
											prvaDoza = new Vakcina(DostupneVakcine.Sputnik, datumPrveDoze);
											korisnik.setVakcine(prvaDoza);
											break;

										default:
											break;
										}
									}
									klijentOut.println(
											">>> Da li ste vakcinisani drugom dozom? (Podrazumeva se da je isti proizvodjac kao i za prvu dozu)");
									klijentOut.println(">>> 1. Da; 2. Ne");
									flagVax = Integer.parseInt(klijentIn.readLine());

									if (flagVax == 1) {

//										klijentOut.println(">>> Koju vakcinu ste primili (druga doza)?");
//										klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
//										int indeksVakcine2 = Integer.parseInt(klijentIn.readLine());

										klijentOut.println(">>> Kada ste primili drugu dozu? (D.M.YYYY)");
										String pomocniNiz2[] = klijentIn.readLine().split("\\.");
										int dan = Integer.parseInt(pomocniNiz2[0]);
										int mesec = Integer.parseInt(pomocniNiz2[1]);
										int godina = Integer.parseInt(pomocniNiz2[2]);

										GregorianCalendar datumDrugeDoze = new GregorianCalendar(godina, mesec - 1,
												dan);

										if ((datumDrugeDoze.getTimeInMillis() / 604800000
												- korisnik.getVakcine()[0].getDatumiDoza().getTimeInMillis()
														/ 604800000 >= 3)) {
											Vakcina drugaDoza;

											if (indeksVakcine != -1) {
												switch (indeksVakcine) {
												case 1:
													drugaDoza = new Vakcina(DostupneVakcine.Fajzer, datumDrugeDoze);
													korisnik.setVakcine(drugaDoza);
													break;
												case 2:
													drugaDoza = new Vakcina(DostupneVakcine.Sinofarm, datumDrugeDoze);
													korisnik.setVakcine(drugaDoza);
													break;
												case 3:
													drugaDoza = new Vakcina(DostupneVakcine.Sputnik, datumDrugeDoze);
													korisnik.setVakcine(drugaDoza);
													break;

												default:
													break;
												}

												klijentOut.println(">>> Da li ste vakcinisani trecom buster dozom?");
												klijentOut.println(">>> 1. Da; 2. Ne");
												flagVax = Integer.parseInt(klijentIn.readLine());

												if (flagVax == 1) {

													klijentOut.println(">>> Koju vakcinu ste primili (treca doza)?");
													klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
													int indeksVakcine3 = Integer.parseInt(klijentIn.readLine());

													klijentOut.println(">>> Kada ste primili trecu dozu? (D.M.YYYY)");
													String pomocniNiz3[] = klijentIn.readLine().split("\\.");
													dan = Integer.parseInt(pomocniNiz3[0]);
													mesec = Integer.parseInt(pomocniNiz3[1]);
													godina = Integer.parseInt(pomocniNiz3[2]);
													GregorianCalendar datumTreceDoze = new GregorianCalendar(godina,
															mesec - 1, dan);

													if (!(datumTreceDoze.get(2) - korisnik.getVakcine()[1].getDatumiDoza().get(2) >= 6)) {
														klijentOut.println(
																">>> Treca doza mora biti primljena minimalno 6 meseci nakon druge! Ponistava se radnja! ");
														break;
													}

													Vakcina trecaDoza;

													switch (indeksVakcine3) {
													case 1:
														trecaDoza = new Vakcina(DostupneVakcine.Fajzer, datumTreceDoze);
														korisnik.setVakcine(trecaDoza);
														break;
													case 2:
														trecaDoza = new Vakcina(DostupneVakcine.Sinofarm,
																datumTreceDoze);
														korisnik.setVakcine(trecaDoza);
														break;
													case 3:
														trecaDoza = new Vakcina(DostupneVakcine.Sputnik,
																datumTreceDoze);
														korisnik.setVakcine(trecaDoza);
														break;

													default:
														break;
													}

													// System.out.println(korisnik.getVakcine()[2].toString());
												}

											}
										} else {
											klijentOut.println(
													">>> Druga doza mora biti primljena minimalno 3 nedelje nakon prve! Ponistava se radnja!");
										}
										try (FileOutputStream fo = new FileOutputStream("registrovani_korisnici.out");
												BufferedOutputStream bo = new BufferedOutputStream(fo);
												ObjectOutputStream oo = new ObjectOutputStream(bo)) {
											oo.flush();
											oo.writeObject(registrovaniKorisnici);
										}
										klijentOut.println(">>> Gotovo.");
									}
								} else if (korisnik.getVakcine()[1] == null) {
									int indeksVakcine = -1;
									if (korisnik.getVakcine()[0].getNaziv() == DostupneVakcine.Fajzer) {
										indeksVakcine = 1;
									} else if (korisnik.getVakcine()[0].getNaziv() == DostupneVakcine.Sinofarm) {
										indeksVakcine = 2;
									} else if (korisnik.getVakcine()[0].getNaziv() == DostupneVakcine.Sputnik) {
										indeksVakcine = 3;
									}
									klijentOut.println(
											">>> Da li ste vakcinisani drugom dozom? (Podrazumeva se da je isti proizvodjac kao i za prvu dozu)");
									klijentOut.println(">>> 1. Da; 2. Ne");
									flagVax = Integer.parseInt(klijentIn.readLine());

									if (flagVax == 1) {

//										klijentOut.println(">>> Koju vakcinu ste primili (druga doza)?");
//										klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
//										int indeksVakcine2 = Integer.parseInt(klijentIn.readLine());

										klijentOut.println(">>> Kada ste primili drugu dozu? (D.M.YYYY)");
										String pomocniNiz2[] = klijentIn.readLine().split("\\.");
										int dan = Integer.parseInt(pomocniNiz2[0]);
										int mesec = Integer.parseInt(pomocniNiz2[1]);
										int godina = Integer.parseInt(pomocniNiz2[2]);

										GregorianCalendar datumDrugeDoze = new GregorianCalendar(godina, mesec - 1,
												dan);

										if ((datumDrugeDoze.getTimeInMillis() / 604800000
												- korisnik.getVakcine()[0].getDatumiDoza().getTimeInMillis()
														/ 604800000 >= 3)) {
											Vakcina drugaDoza;

											if (indeksVakcine != -1) {
												switch (indeksVakcine) {
												case 1:
													drugaDoza = new Vakcina(DostupneVakcine.Fajzer, datumDrugeDoze);
													korisnik.setVakcine(drugaDoza);
													break;
												case 2:
													drugaDoza = new Vakcina(DostupneVakcine.Sinofarm, datumDrugeDoze);
													korisnik.setVakcine(drugaDoza);
													break;
												case 3:
													drugaDoza = new Vakcina(DostupneVakcine.Sputnik, datumDrugeDoze);
													korisnik.setVakcine(drugaDoza);
													break;

												default:
													break;
												}

												klijentOut.println(">>> Da li ste vakcinisani trecom buster dozom?");
												klijentOut.println(">>> 1. Da; 2. Ne");
												flagVax = Integer.parseInt(klijentIn.readLine());

												if (flagVax == 1) {

													klijentOut.println(">>> Koju vakcinu ste primili (treca doza)?");
													klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
													int indeksVakcine3 = Integer.parseInt(klijentIn.readLine());

													klijentOut.println(">>> Kada ste primili trecu dozu? (D.M.YYYY)");
													String pomocniNiz3[] = klijentIn.readLine().split("\\.");
													dan = Integer.parseInt(pomocniNiz3[0]);
													mesec = Integer.parseInt(pomocniNiz3[1]);
													godina = Integer.parseInt(pomocniNiz3[2]);
													GregorianCalendar datumTreceDoze = new GregorianCalendar(godina,
															mesec - 1, dan);

													if (!(datumTreceDoze.get(2) - korisnik.getVakcine()[1].getDatumiDoza().get(2) >= 6)) {
														klijentOut.println(
																">>> Treca doza mora biti primljena minimalno 6 meseci nakon druge! Ponistava se radnja!");
														break;
													}

													Vakcina trecaDoza;

													switch (indeksVakcine3) {
													case 1:
														trecaDoza = new Vakcina(DostupneVakcine.Fajzer, datumTreceDoze);
														korisnik.setVakcine(trecaDoza);
														break;
													case 2:
														trecaDoza = new Vakcina(DostupneVakcine.Sinofarm,
																datumTreceDoze);
														korisnik.setVakcine(trecaDoza);
														break;
													case 3:
														trecaDoza = new Vakcina(DostupneVakcine.Sputnik,
																datumTreceDoze);
														korisnik.setVakcine(trecaDoza);
														break;

													default:
														break;
													}

													// System.out.println(korisnik.getVakcine()[2].toString());
												}

											}
										} else {
											klijentOut.println(
													">>> Druga doza mora biti primljena minimalno 3 nedelje nakon prve! Ponistava se radnja!");
										}
										try (FileOutputStream fo = new FileOutputStream("registrovani_korisnici.out");
												BufferedOutputStream bo = new BufferedOutputStream(fo);
												ObjectOutputStream oo = new ObjectOutputStream(bo)) {
											oo.flush();
											oo.writeObject(registrovaniKorisnici);
										}
										klijentOut.println(">>> Gotovo.");
									}
								} else if (korisnik.getVakcine()[2] == null) {
									klijentOut.println(">>> Da li ste vakcinisani trecom buster dozom?");
									klijentOut.println(">>> 1. Da; 2. Ne");
									flagVax = Integer.parseInt(klijentIn.readLine());

									if (flagVax == 1) {

										klijentOut.println(">>> Koju vakcinu ste primili (treca doza)?");
										klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
										int indeksVakcine3 = Integer.parseInt(klijentIn.readLine());

										klijentOut.println(">>> Kada ste primili trecu dozu? (D.M.YYYY)");
										String pomocniNiz3[] = klijentIn.readLine().split("\\.");
										int dan = Integer.parseInt(pomocniNiz3[0]);
										int mesec = Integer.parseInt(pomocniNiz3[1]);
										int godina = Integer.parseInt(pomocniNiz3[2]);
										GregorianCalendar datumTreceDoze = new GregorianCalendar(godina,
												mesec - 1, dan);

										if (!(datumTreceDoze.get(2) - korisnik.getVakcine()[1].getDatumiDoza().get(2) >= 6)) {
											klijentOut.println(
													">>> Treca doza mora biti primljena minimalno 6 meseci nakon druge! Ponistava se radnja!");
											break;
										}

										Vakcina trecaDoza;

										switch (indeksVakcine3) {
										case 1:
											trecaDoza = new Vakcina(DostupneVakcine.Fajzer, datumTreceDoze);
											korisnik.setVakcine(trecaDoza);
											break;
										case 2:
											trecaDoza = new Vakcina(DostupneVakcine.Sinofarm,
													datumTreceDoze);
											korisnik.setVakcine(trecaDoza);
											break;
										case 3:
											trecaDoza = new Vakcina(DostupneVakcine.Sputnik,
													datumTreceDoze);
											korisnik.setVakcine(trecaDoza);
											break;

										default:
											break;
										}
								
							} 
							try (FileOutputStream fo = new FileOutputStream("registrovani_korisnici.out");
									BufferedOutputStream bo = new BufferedOutputStream(fo);
									ObjectOutputStream oo = new ObjectOutputStream(bo)) {
								oo.flush();
								oo.writeObject(registrovaniKorisnici);
							}
							klijentOut.println(">>> Gotovo.");
							}
								break;

							case 2:
								if (korisnik.getVakcine()[1] != null || korisnik.getVakcine()[0] != null) {
									try (FileWriter fw = new FileWriter(
											"kovidPropusnica_" + korisnik.getIme() + "_" + korisnik.getPrezime());
											BufferedWriter bw = new BufferedWriter(fw);
											PrintWriter pw = new PrintWriter(bw)) {
										pw.write("KOVID PROPUSNICA \n-Ime: " + korisnik.getIme() + "\n-Prezime: "
												+ korisnik.getPrezime() + "\n-JMBG: " + korisnik.getJmbg() + "\n-Pol: "
												+ korisnik.getPol());
										pw.write("\n\n-Prva doza vakcine: " + korisnik.getVakcine()[0].getNaziv() + " "
												+ new SimpleDateFormat("dd.MM.yyyy")
														.format(korisnik.getVakcine()[0].getDatumiDoza().getTime()));
										pw.write("\n-Druga doza vakcine: " + korisnik.getVakcine()[1].getNaziv() + " "
												+ new SimpleDateFormat("dd.MM.yyyy")
														.format(korisnik.getVakcine()[1].getDatumiDoza().getTime()));

										if (korisnik.getVakcine()[2] != null) {
											pw.write("\n-Treca doza vakcine: " + korisnik.getVakcine()[2].getNaziv()
													+ " " + new SimpleDateFormat("dd.MM.yyyy").format(
															korisnik.getVakcine()[2].getDatumiDoza().getTime()));
										}
										klijentOut.println(">>> Kovid propusnica generisana!");
									}
								} else {
									klijentOut.println(">>> Nemate bar 2 doze vakcine!");
								}
								break;

							default:
								break;
							}

						}
					}

					if (!flagLogIn) {
						klijentOut.println(
								">>> Neupesna prijava! Proverite da li ste dobro uneli adresu i sifru i da li ste se registrovali...");
					}

					odgovor = -1;
					break;
				case 3:
					klijentOut.println(">>> Unesite username admina:");
					String adminUser = klijentIn.readLine();
					klijentOut.println(">>> Unesite sifru admina:");
					String adminSifra = klijentIn.readLine();

					for (Admin admin : admini) {
						if (admin.getUsername().equals(adminUser) && admin.getSifra().equals(adminSifra)) {
							klijentOut.println(
									">>> 1. Pretrazite korisnike sa JMBG-om; 2. Pogledajte sve korisnike; 3. Pogledajte broj vakcinisanih; 4. Pogledajte broj vakcinisanih pojedinacne vakcine");
							int indeksOdgovora = Integer.parseInt(klijentIn.readLine());

							switch (indeksOdgovora) {
							case 1:
								klijentOut.println(">>> Unesite JMBG korisnika:");
								String jmbgKorisnika = klijentIn.readLine();

								for (Korisnik korisnik : registrovaniKorisnici) {
									if (korisnik.getJmbg().equals(jmbgKorisnika)) {
										klijentOut.println(">>> Podaci korisnika:\n");
										klijentOut.println("	Ime: " + korisnik.getIme());
										klijentOut.println("	Prezime: " + korisnik.getPrezime());
										klijentOut.println("	Pol: " + korisnik.getPol());
										klijentOut.println("	Email: " + korisnik.getEmail());
										int i = 0;
										for (Vakcina vakcina : korisnik.getVakcine()) {
											if (vakcina != null) {
												klijentOut.println("	Doza " + i + " : " + vakcina.getNaziv());
												i++;
											}
										}
										if (i >= 2) {
											klijentOut.println("	Kovid propusnica: ima\n");
										} else {
											klijentOut.println("	Kovid propusnica: nema\n");
										}
										break;
									}
								}
								break;
							case 2:
								int i = 0;
								int j = 0;
								for (Korisnik korisnik : registrovaniKorisnici) {
									j++;
									for (Vakcina vakcina : korisnik.getVakcine()) {
										if (vakcina != null) {
											i++;
										}
									}
									klijentOut.println(j + ". " + korisnik.getIme() + " " + korisnik.getPrezime()
											+ " je primio " + i + " doze");
									i = 0;
								}
								break;
							case 3:
								int saPrvom = 0, saDrugom = 0, saTrecom = 0;
								for (Korisnik korisnik : registrovaniKorisnici) {
									if (korisnik.getVakcine()[0] != null) {
										if (korisnik.getVakcine()[1] != null) {
											if (korisnik.getVakcine()[2] != null) {
												saTrecom++;
											} else {
												saDrugom++;
											}
										} else {
											saPrvom++;
										}
									}
								}
								klijentOut.println(">>> Broj korisnika sa prvom dozom: " + saPrvom
										+ "\n	Broj korisnika sa drugom dozom: " + saDrugom
										+ "\n	Broj korisnika sa trecom dozom: " + saTrecom);

								break;
							case 4:
								klijentOut.println(">>> 1. Fajzer; 2. Sinofarm; 3. Sputnik");
								int indeksVakcine = Integer.parseInt(klijentIn.readLine());
								switch (indeksVakcine) {
								case 1:
									int brojFajzer = 0;
									for (Korisnik korisnik : registrovaniKorisnici) {
										if (korisnik.getVakcine()[0] != null) {
											if (korisnik.getVakcine()[0].getNaziv() == DostupneVakcine.Fajzer) {
												brojFajzer++;
											}
										}
									}
									klijentOut.println(
											">>> " + brojFajzer + " korisnika je vakcinisano sa Fajzer vakcinom");
									break;
								case 2:
									int brojSinofarm = 0;
									for (Korisnik korisnik : registrovaniKorisnici) {
										if (korisnik.getVakcine()[0] != null) {
											if (korisnik.getVakcine()[0].getNaziv() == DostupneVakcine.Sinofarm) {
												brojSinofarm++;
											}
										}
									}
									klijentOut.println(
											">>> " + brojSinofarm + " korisnika je vakcinisano sa Sinofarm vakcinom");
									break;
								case 3:
									int brojSputnik = 0;
									for (Korisnik korisnik : registrovaniKorisnici) {
										if (korisnik.getVakcine()[0] != null) {
											if (korisnik.getVakcine()[0].getNaziv() == DostupneVakcine.Sputnik) {
												brojSputnik++;
											}
										}
									}
									klijentOut.println(
											">>> " + brojSputnik + " korisnika je vakcinisano sa Sputnik vakcinom");
									break;

								default:
									break;
								}
								break;
							default:
								break;
							}
							break;
						}
					}
					odgovor = -1;
					break;
				case 0:
					klijentOut.println(">>> Prijatan ostatak dana!");
					soketZaKomunikaciju.close();
					System.out.println(">> Ugasena konekcija!");
//					odgovor = -1;
					break;
				default:
					klijentOut.println(">>> Neispravan odgovor!");
					break;
				}
			}}catch(IOException e)
	{
			// TODO Auto-generated catch block
			System.err.println(">> Prekinuta konekcija.");
		}
}}
