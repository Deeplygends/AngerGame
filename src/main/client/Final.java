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
		int i = (int)(this.timer.getSeconds() - f.timer.getSeconds());
				if(i != 0)
						return i;
				else
					return (int)(this.timer.getNano() - f.timer.getNano());
	}
	
	public boolean equals(Final f)
	{
		return f.name == name;
	}
}
