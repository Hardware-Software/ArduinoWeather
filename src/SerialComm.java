import jssc.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Andrew_2 on 10/17/2016.
 */

public class SerialComm implements SerialPortEventListener {
    ArrayList<BufferReadyEvent> listenerList;
    SerialPort port;
    LinkedBlockingQueue buffer;
    public boolean RTS = false;
    char mostRecentRequest;
    SerialComm(String comPort, LinkedBlockingQueue lq){
        listenerList = new ArrayList<>();
        connect(comPort);
        buffer = lq;
    }

    public void addListener(BufferReadyEvent ev){
        listenerList.add(ev);
    }
    public void connect(String comPort){
        // List the available COM ports.
        String[] list = SerialPortList.getPortNames();
        // Print them out
        for (String port : list) {
            System.out.println(port);
        }
        port = new SerialPort(comPort);
        try {
            port.openPort();
            port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            port.addEventListener(this, SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            System.out.println("Error: Serial port is not available.");
        }
    }
    public boolean send(char dataRequested){
        if(RTS){
            try {
                port.writeByte((byte) dataRequested);
            }catch(SerialPortException exc){
                exc.printStackTrace();
            }
            mostRecentRequest = dataRequested;
            return true;
        }else {
            return false;
        }
    }
    public void serialEvent(SerialPortEvent e) {
        if (e.isRXCHAR() && e.getEventValue() > 0) {
            try {
                System.out.println("Received Response");
                byte[] input = new byte[1];
                if(!RTS){
                    try{
                    input = port.readBytes(3,200);
                    }catch(SerialPortTimeoutException exc){
                        exc.printStackTrace();
                    }
                    StringBuilder s = new StringBuilder();
                    for(byte b : input){
                        s.append((char)b);
                    }
                    if(s.toString().equals("RTS")){
                        RTS = true;
                    }
                }else {
                    try {
                        input = port.readBytes(14, 200);
                    }catch(SerialPortTimeoutException spte){
                        spte.printStackTrace();
                    }
                    ByteBuffer temperature = ByteBuffer.wrap(input, 0, 4);
                    ByteBuffer humidity = ByteBuffer.wrap(input, 4, 4);
                    ByteBuffer pressure = ByteBuffer.wrap(input, 8, 4);
                    ByteBuffer light = ByteBuffer.wrap(input,12,2);
                    temperature.order(ByteOrder.LITTLE_ENDIAN);
                    pressure.order(ByteOrder.LITTLE_ENDIAN);
                    humidity.order(ByteOrder.LITTLE_ENDIAN);
                    light.order(ByteOrder.LITTLE_ENDIAN);
                    float tp = temperature.getFloat();
                    int pr = pressure.getInt();
                    float hm = humidity.getFloat();
                    short lg = light.getShort();
                    buffer.add(new Packet(tp,hm,pr,lg));
                    for(BufferReadyEvent evnt : listenerList){
                        evnt.bufferReady(mostRecentRequest);
                    }
                    System.out.println(tp + "C " + hm + "% RH " + (pr / 100.0F) + " hPa " + lg +" somethings");
                }

            } catch (SerialPortException ex) {
                System.out.println("Serial Port Error");
            }
        }

    }
}
interface BufferReadyEvent {
    void bufferReady(char type);
}
