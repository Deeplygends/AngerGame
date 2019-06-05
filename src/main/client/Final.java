package main.client;

import java.time.Duration;

public class Final implements Comparable<Final>{
	public String name;
	public Duration timer;
	
	public String getTimer()
	{	
		long milli = timer.getNano()/10000000;
		long sec = timer.getSeconds() % 60;
		long min = timer.getSeconds() / 60;
		return min+":"+sec+":"+milli;
	}

	@Override
	public int compareTo(Final f) {
		// TODO Auto-generated method stub
		return (int)(this.timer.getSeconds() - f.timer.getSeconds());
	}
}
