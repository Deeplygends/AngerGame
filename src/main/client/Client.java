package main.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Client implements Runnable{

	private Socket connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	//Notre liste de commandes. Le serveur nous répondra différemment selon la commande utilisée.
	private String[] listCommands = {"FULL", "DATE", "HOUR", "NONE"};
	private static int count = 0;
	private String name = "Client-";   

	public Client(String host, int port){
		name += ++count;
		try {
			connexion = new Socket(host, port);
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run(){

		//nous n'allons faire que 10 demandes par thread...
		for(int i =0; i < 10; i++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {


				writer = new PrintWriter(connexion.getOutputStream(), true);
				reader = new BufferedInputStream(connexion.getInputStream());
				//On envoie la commande au serveur

				String commande = getCommand();
				writer.write(commande);
				//TOUJOURS UTILISER flush() POUR ENVOYER RÉELLEMENT DES INFOS AU SERVEUR
				writer.flush();  

				System.out.println("Commande " + commande + " envoyée au serveur");

				//On attend la réponse
				String response = read();
				System.out.println("\t * " + name + " : Réponse reçue " + response);

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		writer.write("CLOSE");
		writer.flush();
		writer.close();
	}

	//Méthode qui permet d'envoyer des commandeS de façon aléatoire
	private String getCommand(){
		Random rand = new Random();
		return listCommands[rand.nextInt(listCommands.length)];
	}

	//Méthode pour lire les réponses du serveur
	private String read() throws IOException{      
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);      
		return response;
	}

}

