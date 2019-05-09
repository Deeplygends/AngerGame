package main.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Serveur {
	int port = 4800;
	ServerSocket se = null;
	Socket ssv = null;
	int cl = 0;
	Vector<ThreadClientIRC> V;

	public Serveur() {
		try {
			V = new Vector<>();
			se = new ServerSocket(port);
			while (true) {

				System.out.println("Socket open ... Waiting ...");
				ssv = se.accept();
				System.out.println("New Client ...");
				cl++;
				ThreadClientIRC th = new ThreadClientIRC(ssv, this);
				ajouterClient(th);
				th.start();
				
			}
		} catch (IOException e) {
			System.err.println("Erreur : " + e);
		}
		finally {
			try {
				se.close();
			} catch (IOException e) {
			}
		}
	}

	public void ajouterClient(ThreadClientIRC c) {
		V.addElement(c);
	}

	synchronized public void supprimerClient(ThreadClientIRC c) {
		V.remove(c);
		cl--;
	}

	synchronized public void EnvoyerATous(String s) {
		Calendar cal = Calendar.getInstance();
       	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		for(ThreadClientIRC c : V)
		{
			if(s.split(":") != null)
				if(!s.split(":")[0].equals(c.getNom()))
					c.Envoyer("["+sdf.format(cal.getTime()) + "] " + s);
		}
	}

	synchronized public void EnvoyerListeClients(PrintWriter out) {
		String s = "";
		for(ThreadClientIRC c : V)
			s += c.nom+"; ";
		System.out.println(s);
		out.println(s);	
	}

	public static void main(String[] args) {
		new Serveur();
	}
}
