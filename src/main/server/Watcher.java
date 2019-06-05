package main.server;

public class Watcher extends Thread{
	
	Server serv;
	int min = 2;
	boolean launch = false;
	
	public Watcher(Server s)
	{
		serv = s;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(serv.cl < 6 && !serv.running)
				{
					ThreadClientIRC next;
					synchronized(serv.waitingList)
					{
						next = serv.waitingList.poll();
						if(next != null)
						{
							break;
						}
					
					}
						serv.ajouterClient(next);
				}
				synchronized(serv.V)
				{
					for(ThreadClientIRC th : serv.V)
					{
						if(!th.isAlive() && th.getState() == State.TERMINATED)
						{
							System.out.println(th.nom + " " + th.getState().toString() + " " + th.isAlive());
							serv.disconnect.add(th);
						}
					}
					for(ThreadClientIRC th : serv.disconnect)
					{
						System.out.println(th.nom +" disconnected");
						serv.supprimerClient(th);
					}
					serv.disconnect.clear();
				}
				

				
		}
	}

}
