
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author eekian
 */
public class MessageExtractor {
    
    public static byte[] MESSAGE_START = { 0x52, 0x44, 0x54 }; // "RDT "
    public static byte[] MESSAGE_END = { 0x45, 0x4e, 0x44, 0xa, 0xd }; //" END CRLF"
    
    public static int MESSAGE_FRONT_OFFSET = 4; //"RDT#"
    public static int MESSAGE_BACK_OFFSET = 2; //"CRLF"
    public static int MESSAGE_LAST_BACK_OFFSET = 5; //"ENDCRLF"
    public FileOutputStream myFOS;
   
   /*
    public static byte[] CONSIGNMENT_0 = { 0x52, 0x44, 0x54, 0x30, 0x31, 0x9, 0x6f, 0x6e,  
                                           0x65, 0xa, 0x32, 0x9, 0x74, 0x77, 0xa, 0xd };
    public static byte[] CONSIGNMENT_1 = { 0x52, 0x44, 0x54, 0x31, 0x6f, 0xa, 0x33, 0x9,
                                           0x74, 0x68, 0x72, 0x65, 0x65, 0xa, 0xa, 0xd };
    public static byte[] CONSIGNMENT_2 = { 0x52, 0x44, 0x54, 0x32, 0x34, 0x9, 0x66, 0x6f,
                                           0x75, 0x72, 0xa, 0x45, 0x4e, 0x44, 0xa, 0xd };
    */
    
    
    public String MYFILENAME ;
    MessageExtractor(String s)
    {
        MYFILENAME = "my"+s;
		try {
			myFOS = new FileOutputStream(MYFILENAME);
		}
		catch(FileNotFoundException ex) {
            System.out.println(ex.getMessage());
		}
    }

    public  void extract(byte[] msg) {
        
        // given 3 sample messages
        /*byte[][] messages = new byte[3][];
        messages[0] = CONSIGNMENT_0;
        messages[1] = CONSIGNMENT_1;
        messages[2] = CONSIGNMENT_2;*/
        
        byte seqString = -1;        
        
        //byte[] data = new byte[512]; // each consignment has data length 512 bytes


        try {
            myFOS = new FileOutputStream(MYFILENAME,true);

                // get sequence number
                seqString = msg[MESSAGE_START.length];
                //System.out.println("Sequence Number = " + seqString);

                // get last message
                if (!matchByteSequence(msg, msg.length-MESSAGE_END.length , MESSAGE_END.length, MESSAGE_END)) {

                    myFOS.write(msg, MESSAGE_FRONT_OFFSET, msg.length-MESSAGE_FRONT_OFFSET-MESSAGE_BACK_OFFSET);

                    System.out.println("Received CONSIGNMENT " + seqString);
             
                } else {
                    myFOS.write(msg, MESSAGE_FRONT_OFFSET, msg.length-MESSAGE_FRONT_OFFSET-MESSAGE_LAST_BACK_OFFSET);
					System.out.println("Received CONSIGNMENT " + seqString);
                    System.out.println("END");
                }
            
            myFOS.close();
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        
        // extract data
        // write to file
        
    }
           
 public byte seqno(byte[] msg)
    {
                
        return msg[MESSAGE_START.length];
    }
    static public boolean matchByteSequence(byte[] input, int offset, int length, byte[] ref) {
        
        boolean result = true;
        
        if (length == ref.length) {
            for (int i=0; i<ref.length; i++) {
                if (input[offset+i] != ref[i]) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    
}
