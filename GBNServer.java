import java.io.*;
import java.net.*;
import java.util.*;
/**
*author gourav
**/

public class GBNServer {	
	public static final int WINDOW_SIZE = 4;
	public static byte[][] data = new byte[4][];
	public static int i=0,firstOutstand=0,nextSend=4;
	public static void main(String[] args) throws SocketException,IOException{
		DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[0]));				
        byte[] myData = new byte[530];
        byte[] req = new byte[50];
		String msg, cons, ack="";			
		DatagramPacket rp = new DatagramPacket(req,50);
		socket.receive(rp);
		InetAddress ip = rp.getAddress();	
		int port = rp.getPort();			
		msg = new String(req,0,rp.getLength()-2);
		System.out.println("Received request for " + msg.substring(7) + " from " + ip.getHostAddress() + " port " + port);
		MessageFactory mf = new MessageFactory(msg.substring(7));
		byte[] ak = new byte[50];
		while(true) {
			for(i=firstOutstand;i<nextSend;i++)
			{
				data[i-firstOutstand] = mf.doIt(i);
				if(args.length>1)
				if(i==Integer.parseInt(args[1])){
					System.out.println("Forgot CONSIGNMENT "+i);
					if(new String(data[i-firstOutstand]).trim().endsWith("END")){
					System.out.println("END\n");
					break;
					}
					continue;
				}
				if(args.length>2)
				if(i==Integer.parseInt(args[2])){
					System.out.println("Forgot CONSIGNMENT "+i);
					if(new String(data[i-firstOutstand]).trim().endsWith("END")){
					System.out.println("END\n");
					break;
					}
					continue;
				}
				if(args.length>3)
				if(i==Integer.parseInt(args[3])){
					System.out.println("Forgot CONSIGNMENT "+i);
					if(new String(data[i-firstOutstand]).trim().endsWith("END")){
					System.out.println("END\n");
					break;
					}
					continue;
				}
				cons = "Sent Consignment " + i;
				int bytes = data[i-firstOutstand].length;
				DatagramPacket sp = new DatagramPacket(data[i-firstOutstand],bytes,ip,port);
				socket.send(sp);
				System.out.println(cons);
				if (new String(data[i-firstOutstand]).contains("END")){
					System.out.println("END");
					break;
				}
			}
			socket.setSoTimeout(30);
			try
			{
				while(firstOutstand<nextSend){
				DatagramPacket r = new DatagramPacket(ak,50);
				ack = new String(ak,0,r.getLength());
				socket.receive(r);
				System.out.println("Received ACK " + ak[3] );
				if(ak[3]>firstOutstand&&ak[3]<=nextSend){
					firstOutstand = ak[3];
					}
				}
				nextSend = firstOutstand+WINDOW_SIZE;
				
			}
			catch(SocketTimeoutException e)
			{
				System.out.println(e.getMessage());
				onTimeot(socket,ip,port);
				
			}
		}		
	}
	public static void onTimeot(DatagramSocket socket, InetAddress ip,int port) throws SocketException,IOException
	{
		
		for(i=firstOutstand;i<nextSend;i++)
		{
			String cons = "Sent Consignment " + i;
			int bytes = data[i%WINDOW_SIZE].length;
			DatagramPacket sp = new DatagramPacket(data[i%WINDOW_SIZE],bytes,ip,port);
			socket.send(sp);
			System.out.println(cons);
			if (new String(data[i%WINDOW_SIZE]).contains("END")){
				System.out.println("END");
				System.exit(0);
			}
		}
		socket.setSoTimeout(30);
		try
		{
			//System.out.println("Hi" + nextSend);
			while(firstOutstand<nextSend){
			byte[] ak = new byte[50];
			DatagramPacket r = new DatagramPacket(ak,50);
			String ack = new String(ak,0,r.getLength());
			socket.receive(r);
			System.out.println("Received ACK " + ak[3] );
			if(ak[3]>firstOutstand&&ak[3]<=nextSend)
				firstOutstand = ak[3];
			}
			nextSend = firstOutstand+WINDOW_SIZE;
		}
		catch(SocketTimeoutException e)
		{
			System.out.println(e.getMessage());
			onTimeot(socket,ip,port);	
		}
	}
}
