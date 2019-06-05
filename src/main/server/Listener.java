package main.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;

import org.newdawn.slick.geom.Point;

import main.client.Final;
import main.client.WindowGame;

public class Listener extends Thread{
	public boolean kill = false;
	WindowGame w;
	BufferedReader in;
	public Listener(WindowGame w, BufferedReader in)
	{
		this.w = w;
		this.in = in;
	}
	public void run(){
		while(!kill)
		{
			try{
				String s = in.readLine();
				if(s != null)
					if (s.indexOf("(")!=-1 && s.split(":").length == 2) {
						w.setPersonnage(parseName(s), parsePosition(s));  // POUR LAFFICHAGE
					}
					else if(s.split("-").length == 3)
					{
						Final f = new Final();
						f.name = s.split("-")[0];
						f.timer = Duration.parse(s.split("-")[2]);
						w.updateBoard(f);
						System.out.println(f.name + " - " + f.getTimer() + " - " +f.timer.getSeconds());
					}
					else if(s.split("-").length == 2)
					{
						w.removePlayer(s.split("-")[0]);
					}
				

			}catch(IOException e) { }
		}
	}
	
	public Point parsePosition(String message) {
		int startx = message.indexOf("(")+1;
		int endx = message.indexOf(";");
		Float x = Float.parseFloat(message.substring(startx,endx));
		int starty = message.indexOf(";")+1;
		int endy = message.indexOf(")");
		Float y = Float.parseFloat(message.substring(starty,endy));
		return new Point(x,y);
	}
	
	public String parseName(String message) {
		int end = message.indexOf(":");
		return message.substring(0,end);
	}
}
