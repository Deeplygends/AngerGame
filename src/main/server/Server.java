package main.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import main.client.Client;

public class Server {

	public final static int PORT = 2345;
	public final static String HOST = "127.0.0.1";
	private ServerSocket serverSocket;
	private boolean isRunning = false;

	public Server() {
		try {
			serverSocket = new ServerSocket(PORT,100,InetAddress.getByName(HOST));
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//On lance notre serveur
	   public void open(){
	      
	      //Toujours dans un thread à part vu qu'il est dans une boucle infinie
	      Thread t = new Thread(new Runnable(){
	         public void run(){
	            while(isRunning == true){
	               
	               try {
	                  //On attend une connexion d'un client
	                  Socket client = serverSocket.accept();
	                  
	                  //Une fois reçue, on la traite dans un thread séparé
	                  System.out.println("Connexion cliente reçue.");                  
	                  Thread t = new Thread(new ClientProcessor(client));
	                  t.start();
	                  
	               } catch (IOException e) {
	                  e.printStackTrace();
	               }
	            }
	            
	            try {
	            	serverSocket.close();
	            } catch (IOException e) {
	               e.printStackTrace();
	               serverSocket = null;
	            }
	         }
	      });
	      
	      t.start();
	   }

	public static void main(String[] args) {

		Server s = new Server();
		s.open();
		
		System.out.println("Le serveur tourne !");
		
		for(int i = 0; i < 5; i++){
	         Thread t = new Thread(new Client(Server.HOST, Server.PORT));
	         t.start();
	      }

	}

}

