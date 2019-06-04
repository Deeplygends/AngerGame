package main.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.InputStreamReader;


public class ThreadClientIRC extends Thread {
	Server serv;
	Socket ssv;
	String nom;
	BufferedReader in;
	PrintWriter out;

	public ThreadClientIRC(Socket ssv, Server serv) {
		this.serv = serv;
		this.ssv = ssv;
	}

	@Override
	public void run() {
		String req = "";
		try {
			System.out.println("Initialisation of Client thread");
			in = new BufferedReader(new InputStreamReader(ssv.getInputStream()));
			out = new PrintWriter(ssv.getOutputStream(),true);
			System.out.println("Waiting req in");
			req = in.readLine();
			System.out.println("Set Name :" + req);
			setNom(req);
			System.out.println("send every player connected to all");
			serv.EnvoyerListeClients(out);
			while (true) {
				
				// attendre un phrase de reponse �
				req = in.readLine();
				serv.EnvoyerATous(req);
				System.out.println(req);
				System.out.println("pause");

			}

		} catch (IOException e) {
			System.err.println("Erreur IO : " + e);
		}
		finally {
			try {
				ssv.close();
				in.close();
				out.close();
			} catch (IOException e) {
			}
		}
	}

	public void Envoyer(String s) {
		out.println(s);
	}

	public void setNom(String s) {
		this.nom = s;
	}

	public String getNom() {
		return nom;
	}
}