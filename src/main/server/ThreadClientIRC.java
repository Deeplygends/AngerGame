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
		try {
			in = new BufferedReader(new InputStreamReader(ssv.getInputStream()));
			out = new PrintWriter(ssv.getOutputStream(),true);
			String req = in.readLine();
			setNom(req);
			// envoi un premier message d'accueil …
			serv.EnvoyerATous("[Serveur] : " + getNom() +  " vient de se connecter...");
				
			Envoyer("####  Bonjour bienvenue sur le serveur IRC de Micky  ####");
			// envoi la liste des clients connectes …
			serv.EnvoyerListeClients(out);
			while (true) {
				
				// attendre un phrase de reponse …
				req = in.readLine();
				if (req.equals("/quit")) {
					serv.EnvoyerATous(nom + " vient de se deconnecter ...");
					serv.supprimerClient(this);
					System.exit(0);
				}
				else{
					serv.EnvoyerATous(req);
					System.out.println(req);
				}

			}

		} catch (IOException e) {
			System.err.println("Erreur : " + e);
		} finally {
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