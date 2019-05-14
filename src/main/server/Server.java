package main.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import main.client.Client;

public class Server {

	public final static int PORT = 4800;
	public final static String HOST = "localhost";
	private ServerSocket serverSocket;
	private boolean isRunning = true;

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
	      
	            while(isRunning == true){
	               
	               try {
	                  //On attend une connexion d'un client
	            	   System.out.println("Waiting for client ...");
	            	   Socket client = serverSocket.accept();
	                  
	                  //Une fois re�ue, on la traite dans un thread s�par�
	                  System.out.println("Connexion cliente re�ue.");                  
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

	public static void main(String[] args) {

		Server s = new Server();
		s.open();
		
		System.out.println("Le serveur tourne !");
		

	}

}

