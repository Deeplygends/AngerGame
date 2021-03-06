package main.client;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Random;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

import main.server.Listener;

import java.util.List;
import java.util.ArrayList;

public class Client extends Thread {
	static int port = 4800;
	static InetAddress hote = null;
	Socket sc = new Socket();
	BufferedReader in;
	PrintWriter out;
	String nom;
	List<String> tampon = new ArrayList<>();
	int compteur = 0;
	int gameplay = 0;
	boolean send = false;
	Listener listener;
	App game;
	String replay;
	boolean again = true;
	boolean allFinnish;
	WindowGame w;
	
	public Client(String nom) {
		this.nom = nom;
	}

	public void run() {
		try {
			replay = "wait";
			
			sc = new Socket(hote, port);
			in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			out = new PrintWriter(sc.getOutputStream(), true);
			w = new WindowGame("Labyrinthe");
			boolean victorious;
			while(again)
			{
				gameplay++;
				again = false;
				victorious = false;
				allFinnish = false;
				
				
			//Game Start
				System.out.println("Game Start " + gameplay);
				if(gameplay < 2)
				{
					
					listener = new Listener(w, in);

					game = new App(w);
					listener.start();
					System.out.println("start game");
					game.start();
				}
	
			


			String coordinates = "";
			
			// envoyer le pseudonyme au serveur
			System.out.println("Ceci est votre nom : " + nom);
			if(gameplay < 2)
			{
				out.println(nom);
				// recevoir le message d'accueil du serveur
				w.setName(nom);
			}


			System.out.println(send);
			while (true) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					coordinates = w.getCoordinates();
					//System.out.println( "coordonnées : " + coordinates);
					victorious = w.getVictorious();
					out.println(nom + ": " + coordinates);
					if (victorious && !send) {
						System.out.println("Sending score");
						out.println(nom + "- "+ "won" + "-" + w.getDuration().toString());
						send = true;
						victorious = false;
					}
					if(w.replay == "yes" && send)
					{
						again = true;
						send = false;
					}
					else if(w.replay == "no")
					{
						again = false;
						break;
					}
				}
			}
			
		} catch (IOException e) {
			System.err.println("Impossible cree socket du client : " + e);
		}  catch(Exception e){
			System.err.println("Erreur " + e);
		}finally {
		
			try {
				System.out.println("closing socket");
				sc.close();
				in.close();
				out.close();
				w.exit();
			} catch (IOException e) {
			}
		}
	}

	public static Point parsePosition(String message) {
		int startx = message.indexOf("(")+1;
		int endx = message.indexOf(";");
		Float x = Float.parseFloat(message.substring(startx,endx));
		int starty = message.indexOf(";")+1;
		int endy = message.indexOf(")");
		Float y = Float.parseFloat(message.substring(starty,endy));
		return new Point(x,y);
	}
	
	public static String parseName(String message) {
		int end = message.indexOf(":");
		return message.substring(0,end);
	}
	
	public static void main(String[] args)  {
		Random r = new Random();
		String pseudo = "Visitor " + r.nextInt(100000);
		try {
			if (args.length >= 2) {
				Client.hote = InetAddress.getByName(args[0]);
				Client.port = Integer.parseInt(args[1]);
				if(!args[2].equals(null) && !args[2].equals(""))
					pseudo = args[2];
				Client c = new Client(pseudo);
				c.run();
			} else {

			}
		} catch (UnknownHostException e) {
			System.err.println("Machine inconnue :" + e);
		} 


	}


}

