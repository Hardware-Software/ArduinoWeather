import jssc.*;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Andrew_2 on 10/17/2016.
 */

public class SerialComm implements SerialPortEventListener {
    SerialPort port;
    LinkedBlockingQueue buffer;
    public boolean RTS = false;
    static byte x = 'F';
    SerialComm(String comPort, LinkedBlockingQueue lq){
        connect(comPort);
        buffer = lq;
    }
    public void connect(String comPort){
        // List the available COM ports.
        String[] list = SerialPortList.getPortNames();
        // Print them out
        for (String port : list) {
            System.out.println(port);
        }
        port = new SerialPort("COM3");
        try {
            port.openPort();
            port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            port.addEventListener(this, SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            System.out.println("Error: Serial port is not available.");
        }
    }
    public boolean send(char Message){
        if(RTS){
            try {
                port.writeByte((byte) Message);
            }catch(SerialPortException exc){
                exc.printStackTrace();
            }
            return true;
        }else {
            return false;
        }
    }
    public void serialEvent(SerialPortEvent e) {
        if (e.isRXCHAR() && e.getEventValue() > 0) {
            try {
                System.out.println("Received Response");
                byte[] input = port.readBytes();
                if(!RTS){
                    StringBuilder s = new StringBuilder();
                    for(byte b : input){
                        s.append((char)b);
                    }
                    buffer.add(s.toString());
                    RTS = true;
                }else {
                    buffer.add(input);
                }
            } catch (SerialPortException ex) {
                System.out.println("Serial Port Error");
            }
        }

    }
}
