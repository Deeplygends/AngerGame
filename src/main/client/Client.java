package main.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import main.server.Server;

public class Client implements Runnable{

	private Socket connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	//Notre liste de commandes. Le serveur nous r�pondra diff�remment selon la commande utilis�e.
	private String[] listCommands = {"FULL", "DATE", "HOUR", "NONE"};
	private static int count = 0;
	private String name = "Client-";   

	public Client(String host, int port){
		name += ++count;
		try {
			System.out.println();
			connexion = new Socket(InetAddress.getByName(host), port);
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
				//TOUJOURS UTILISER flush() POUR ENVOYER R�ELLEMENT DES INFOS AU SERVEUR
				writer.flush();  

				System.out.println("Commande " + commande + " envoy�e au serveur");

				//On attend la r�ponse
				String response = read();
				System.out.println("\t * " + name + " : R�ponse re�ue " + response);

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

	//M�thode qui permet d'envoyer des commandeS de fa�on al�atoire
	private String getCommand(){
		Random rand = new Random();
		return listCommands[rand.nextInt(listCommands.length)];
	}

	//M�thode pour lire les r�ponses du serveur
	private String read() throws IOException{      
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);      
		return response;
	}
	
	public static void main(String[] args) {


		new Client(Server.HOST, Server.PORT);

	}

}

