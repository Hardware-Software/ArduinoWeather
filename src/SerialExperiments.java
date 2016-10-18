/**
 * Created by Andrew_2 on 10/17/2016.
 */
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialExperiments {
   public static void main(String[] args) {
       String portName = "COM3";
       try {
           CommPortIdentifier pi = CommPortIdentifier.getPortIdentifier(portName);

           if(pi.isCurrentlyOwned()){
               System.out.println("Port is being used by another application.");
           }
           else {
               CommPort cmp = pi.open("SerialExperiments",2000);
                if(cmp instanceof SerialPort){
                    SerialPort sp = (SerialPort) cmp;
                    sp.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

                    InputStream in = sp.getInputStream();
                    OutputStream out = sp.getOutputStream();

                    (new Thread(new SerialReader(in))).start();
                    (new Thread(new SerialWriter(out))).start();
                }
           }

       }catch(Exception e){e.printStackTrace();}
   }
    public static class SerialWriter implements Runnable
    {
        OutputStream out;

        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }

        public void run ()
        {
            try
            {
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }
    public static class SerialReader implements Runnable
    {
        InputStream in;

        public SerialReader ( InputStream in )
        {
            this.in = in;
        }

        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }
}
