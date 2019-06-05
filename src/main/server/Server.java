package main.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {
	int port = 4800;
	ServerSocket se = null;
	Socket ssv = null;
	public int cl = 0;
	public int wait = 0;
	public ArrayDeque<ThreadClientIRC> waitingList = new ArrayDeque<ThreadClientIRC>();
	public Vector<ThreadClientIRC> V;
	public boolean running = false;
	public ArrayList<ThreadClientIRC> disconnect = new ArrayList<>();
	public HashMap<String, Duration> board = new HashMap<>();
	public Server() {
		try {
			V = new Vector<>();
			se = new ServerSocket(port);
			System.out.println("open Socket");
			Watcher watch = new Watcher(this);
			System.out.println("Run watcher");
			watch.start();
			System.out.println("watcher running ...");
			while (true) {
				/*
				System.out.println("Waiting for connection ...");
				ssv = se.accept(); //bloquant
				System.out.println("Client connect");
				ThreadClientIRC th = new ThreadClientIRC(ssv, this);
				waitingList.add(th);
				wait++;
				*/
				/*
				 *fonctionnelle */
				System.out.println("Socket open ... Waiting ...");
				ssv = se.accept(); //bloquant
				System.out.println("New Client ...");
				synchronized(V)
				{
					System.out.println("creating thread");
					ThreadClientIRC th = new ThreadClientIRC(ssv, this);
					System.out.println("adding thread");
					ajouterClient(th);
					System.out.println("end synchronized");
				}
				
			}
		} catch (IOException e) {
			System.err.println("Erreur : " + e);
		}
		catch(Exception e)
		{
			
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
		cl++;
		c.start();
	}

	synchronized public void supprimerClient(ThreadClientIRC c) {
		V.remove(c);
		cl--;
	}

	synchronized public void EnvoyerATous(String s) {
       	if(s != null)
       		if(V.size() != 0)
       		{
       			
				for(ThreadClientIRC c : V)
				{
					if(s.split(":") != null && s.split(":").length == 2)
						if(!s.split(":")[0].equals(c.getNom()))
							c.Envoyer(s);
					if(s.split("-") != null && s.split("-").length == 3)
					{
						System.out.println(s);
						System.out.println("update launch");
						updateBoard(s);
						//System.out.println(board.get("Deeply"));
						c.Envoyer(s);
					}
				}
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
		new Server();
	}
	
	synchronized void updateBoard(String s)
	{
		String[] split = s.split("-");
		if(split.length == 3)
		{
			Duration tmp = Duration.parse(split[2]);
			if(board.keySet().contains(split[0]))
			{
				if(board.get(split[0]).compareTo(tmp) > 0)
				{
					System.out.println("Old : " + board.get(split[0]).getSeconds() + "New record : " + tmp.getSeconds());
					board.put(split[0], tmp);
				}
			}
			else
				board.put(split[0], tmp);
		}
			
	}
	
	synchronized void envoyerBoard(ThreadClientIRC th)
	{
		for(String t : board.keySet())
		{
			String mess = t;
			mess += "-won-" + board.get(t).toString();
			System.out.println(mess);
			th.Envoyer(mess);
		}
	}
}

