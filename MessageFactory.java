
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * This class reads bytes from a stream, creates consignments and 
 * puts each consignment into a message
 * 
 * Message Format (without in-between space)
 * RDT sequence_number payload CRLF, or
 * RDT sequence_number payload END_CRLF, or
 * 
 * @author eekian
 */
public class MessageFactory {
    
    public static int CONSIGNMENT = 512;
    public static int HEX_PER_LINE = 512;
    public static byte[] RDT = new byte[] { 0x52, 0x44, 0x54 };
    public static byte[] SEQ_0 = new byte[] { 0x0 };
    public static byte[] END = new byte[] { 0x45, 0x4e, 0x44 };
    public static byte[] CRLF = new byte[] { 0x0a, 0x0d };
    byte count = 0;
    
	public String fileName;
    MessageFactory(String s) {
		fileName = s;
	}
	
    public byte[] doIt(int skipBytes) {
        
        FileInputStream myFIS = null;
        byte[] myData = new byte[CONSIGNMENT];
        byte[] myLastData;
        byte[] myMsg = null;
        int bytesRead = 0;		
        int i; // counter for copying bytes in array
        
        try {
            myFIS = new FileInputStream(fileName);
            myFIS.skip(skipBytes*512);
			bytesRead = myFIS.read(myData);
			//System.out.println("bytesRead : " + bytesRead);            
			if (bytesRead > -1) {
				//printBytesAsHex(myData);
				//System.out.println("data consignment has " + bytesRead + " bytes");  
		   
				if (bytesRead < CONSIGNMENT) {
					// last consignment
		// make a special byte array that exactly fits the number of bytes read 
		// otherwise, the consignment may be padded with junk data
					myLastData = new byte[bytesRead];
					for (i=0; i<bytesRead; i++) {
						myLastData[i] = myData[i];
					}

					myMsg = concatenateByteArrays(RDT, SEQ_0, myLastData, END, CRLF);

				} else {

					myMsg = concatenateByteArrays(RDT, SEQ_0, myData, CRLF);
				}
				SEQ_0[0] += 0x1;
			
				// prepare to send, concat bytes
				//printBytesAsHex(myMsg);
				//System.out.println("message has " + myMsg.length + " bytes");
                 
                }            
                      
        } /*catch (FileNotFoundException ex1) {
            System.out.println(ex1.getMessage());
            
        } */catch (IOException ex) {
            System.out.println(ex.getMessage());
            
        }finally {			
		/*try {
			myFIS.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}	*/
			return myMsg;
        }    
    }
    
    public static byte[] concatenateByteArrays(byte[] a, byte[] b, byte[] c, byte[] d) {
        byte[] result = new byte[a.length + b.length + c.length + d.length]; 
        System.arraycopy(a, 0, result, 0, a.length); 
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length+b.length, c.length);
        System.arraycopy(d, 0, result, a.length+b.length+c.length, d.length);
        return result;
    }
    
    public static byte[] concatenateByteArrays(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e) {
        byte[] result = new byte[a.length + b.length + c.length + d.length + e.length]; 
        System.arraycopy(a, 0, result, 0, a.length); 
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length+b.length, c.length);
        System.arraycopy(d, 0, result, a.length+b.length+c.length, d.length);
        System.arraycopy(e, 0, result, a.length+b.length+c.length+d.length, e.length);
        return result;
    }
}
    

