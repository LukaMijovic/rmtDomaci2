package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientHandler extends Thread{

	private BufferedReader klijentIn = null;
	private PrintStream klijentOut = null;
	private Socket soketZaKomunikaciju = null;
	
	public ClientHandler(Socket soketZaKomunikaciju) {
		this.soketZaKomunikaciju = soketZaKomunikaciju;
	}
	
	public void run() {
		try {
			klijentIn = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
			klijentOut = new PrintStream(soketZaKomunikaciju.getOutputStream());
			
			int odgovor = -1;
			
			while (odgovor == -1) {
				klijentOut.println(">>> 1. Registracija\n>>> 2. Prijava\n>>> 0. Izlaz");
//				klijentOut.println(">>> 2. Prijava");
//				klijentOut.println(">>> 0. Izlaz");
				
				odgovor = Integer.parseInt(klijentIn.readLine());
				//System.out.println(odgovor);
				
				switch (odgovor) {
				case 1:
					
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
