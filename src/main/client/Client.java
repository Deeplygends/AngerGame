package main.client;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

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
	Thread game;
	public Client(String nom) {
		this.nom = nom;
	}

	public void run() {
		try {

			sc = new Socket(hote, port);
			in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			out = new PrintWriter(sc.getOutputStream(), true);
			WindowGame w = new WindowGame("Labyrinthe");
			listener = new Thread(new Runnable(){

				public void run(){
					while(true)
					{
						try{
							String s = in.readLine();
							if (s.indexOf("(")!=-1) {
								//w.setPersonnage(parseName(s), parsePosition(s));   POUR LAFFICHAGE
								System.out.println(parsePosition(s).getCenterX());
							}
							System.out.println(s);
							

						}catch(IOException e) { }
					}
				}
			});
			listener.start();


			game = new Thread(new Runnable() {
				public void run() {

					System.setProperty("org.lwjgl.librarypath", new File("lib/natives").getAbsolutePath());
					try {
						new AppGameContainer(w, 640, 480, false).start();
					} catch (SlickException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			game.start();


			String coordinates = "";
			// envoyer le pseudonyme au serveur
			System.out.println("Ceci est votre nom : " + nom);
			out.println(nom);
			// recevoir le message d'accueil du serveur




			while (true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				coordinates = w.getCoordinates();
				//Calendar cal = Calendar.getInstance();
				//SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

				out.println(nom + ": " + coordinates);


			}
			
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
		int end = message.indexOf(":")-1;
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

