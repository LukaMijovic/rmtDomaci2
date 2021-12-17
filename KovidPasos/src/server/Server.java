package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Server {

//	public static List<ClientHandler> onlineKorisnici = new LinkedList<>();
	
	public static void main(String[] args) {
		int port = 3006;
		ServerSocket serverSoket = null;
		Socket soketZaKomunikaciju = null;
		
		try {
			serverSoket = new ServerSocket(port);
			
			while(true) {
				System.out.println(">> Server ceka na povezivanje...");
				soketZaKomunikaciju = serverSoket.accept();
				System.out.println(">> Klijent povezan!");
				
				ClientHandler klijent = new ClientHandler(soketZaKomunikaciju);
//				onlineKorisnici.add(klijent);
				klijent.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
