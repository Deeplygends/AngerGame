package main.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Client extends Thread {
	static int port = 4800;
	static InetAddress hote = null;
	Socket sc = new Socket();
	BufferedReader in;
	PrintWriter out;
	String nom;
	List<String> tampon = new ArrayList<>();
	int compteur = 0;

	Thread listener;
	public Client(String nom) {
		this.nom = nom;
	}

	public void run() {
		try {
			sc = new Socket(hote, port);
			in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			out = new PrintWriter(sc.getOutputStream(), true);
			Scanner scan = new Scanner(System.in);
			listener = new Thread(new Runnable(){
				
				public void run(){
					while(true)
					{
						try{
							String s = in.readLine();
							System.out.println(s);
							
						}catch(IOException e) { }
					}
				}
			});
			listener.start();
			String rep = "";
			// envoyer le pseudonyme au serveur
			System.out.println("Ceci est votre nom : " + nom);
			out.println(nom);
			// recevoir le message d'accueil du serveur
		

			
			
			while (!rep.equals("/quit")) {
				rep = scan.nextLine();
				Calendar cal = Calendar.getInstance();
       			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				if(rep.equals("/list") || rep.equals("/oss117"))
				{
					out.println(rep);
				}
				else if(rep.equals("/quit"))
				{
					out.println("Bye");
					rep = "/quit";
				}
				else{
					// recevoir un message du serveur
					// incrementer le nb d'echanges
					// repondre au serveur;
					out.println(nom + ": " + rep);
					// faire une pause de 3sec
				}
				
			}
			// recevoir un message du serveur
		
			// faire une pause de 2sec
			// envoyer un message « Bye » au serveur
		} catch (IOException e) {
			System.err.println("Impossible cree socket du client : " + e);
		}  finally {
			try {
				sc.close();
				
				in.close();
				out.close();
			} catch (IOException e) {
			}
		}
	}

	public static void main(String[] args) {
		Random r = new Random();
		String pseudo = "Visitor " + r.nextInt(100000);
		try {
			if (args.length >= 2) {
				Client.hote = InetAddress.getByName(args[0]);
				Client.port = Integer.parseInt(args[1]);
				if(!args[2].equals(null) && !args[2].equals(""))
					pseudo = args[2];
				Client c = new Client(pseudo);
				//c.port = Integer.parseInt(args[1]);
				//c.hote = InetAddress.getByName(args[0]);
				c.run();
			} else {

			}
		} catch (UnknownHostException e) {
			System.err.println("Machine inconnue :" + e);
		} 
		
	}


}

