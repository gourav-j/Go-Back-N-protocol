import java.io.*;
import java.net.*;
import java.util.*;
/**
*author gourav
**/
public class GBNClient
{
	static private final String CLRF="\r\n";
 	public static void main(String[] args) throws IOException,SocketException
 	{
  		DatagramSocket client = new DatagramSocket();
  		String filename = args[2];
  		MessageExtractor me = new MessageExtractor(filename);
  		String msg = "REQUEST"+filename+CLRF;
		byte req[] = new byte[50]; 
		req = msg.getBytes();
		InetAddress ip = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);
		DatagramPacket sp = new DatagramPacket(req,req.length,ip,port);
		System.out.println("Requesting "+filename+" from "+args[0]+" port "+args[1]+"\n");
		client.send(sp);

		boolean quit = false;
		int r=1,lseq;
		while (!quit) {
	  		
	  		byte b[] = new byte[530];
			DatagramPacket rp = new DatagramPacket(b,530);			
			client.receive(rp);
			int bytes = rp.getLength();
			byte message[] = new byte[bytes];
			for(int i=0;i<bytes;i++)			
				message[i] = b[i];				
			byte seqNo = me.seqno(message);	
			seqNo++;
			if(seqNo==r)
			{
				me.extract(message);
				if (new String(message).trim().endsWith("END")){
					break;
				}
				if(args.length>3)
				if(seqNo==Integer.parseInt(args[3])){
					System.out.println("Forgot ACK "+seqNo);
					r++;
					continue;
				}
				if(args.length>4)
				if(seqNo==Integer.parseInt(args[4])){
					System.out.println("Forgot ACK "+seqNo);
					r++;
					continue;
				}
				if(args.length>5)
				if(seqNo==Integer.parseInt(args[5])){
					System.out.println("Forgot ACK "+seqNo);
					r++;
					continue;
				}
				byte ack[] = {65,67,75,0,'\r','\n'};
				ack[3] = seqNo;
				DatagramPacket s = new DatagramPacket(ack,6,ip,port);
				client.send(s);
				System.out.println("Sent ACK "+seqNo+"\n");
				r++;
			}
			else
				System.out.println("Received CONSIGNMENT "+ (seqNo-1)+"incorrect consignment - discarding " +"\n");
		}
		client.close();
 	}
}
