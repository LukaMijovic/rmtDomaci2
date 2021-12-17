package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable{
	
	private static Socket soketZaKomunikaciju = null;
	private static BufferedReader serverIn = null;
	private static PrintStream serverOut = null;
	private static BufferedReader unosKorisnika = null;
	
	public static void main(String[] args) {
		try {
			soketZaKomunikaciju = new Socket("localhost", 3006);
			serverIn = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
			serverOut = new PrintStream(soketZaKomunikaciju.getOutputStream());
			unosKorisnika = new BufferedReader(new InputStreamReader(System.in));
			
			new Thread(new Client()).start();
			String serverMsg;
			
			while(true) {
				serverMsg = serverIn.readLine();
				System.out.println(serverMsg);
				
				if (serverMsg.startsWith(">>> Prijatan ostatak dana!")) {
					break;
				}
			}
			soketZaKomunikaciju.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int broj;
		
		while(true) {
			try {
				broj = Integer.parseInt(unosKorisnika.readLine());
				serverOut.println(broj);
				if (broj == 0) {
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
