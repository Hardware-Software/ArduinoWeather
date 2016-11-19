import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Andrew_2 on 11/18/2016.
 */
public class Driver {
    static boolean ReadyToSend;
    static LinkedBlockingQueue buffer = new LinkedBlockingQueue();
    static String RTSMessage;
    static byte[] message;
    static final String RTSCode = "RTS";

    public static void main(String[] args) {
        SerialComm cmp = new SerialComm("COM3", buffer);
        do {
            if (!buffer.isEmpty()) {
                RTSMessage = (String) buffer.remove();
                if (RTSMessage.equals(RTSCode)) {
                    ReadyToSend = true;
                    System.out.println("Ready To Send.");
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (!ReadyToSend);
        cmp.send('F');
        for (; ; ) {
            if (!buffer.isEmpty()) {
                message = (byte[]) buffer.remove();
                ByteBuffer temperature = ByteBuffer.wrap(message,0,4);
                ByteBuffer pressure = ByteBuffer.wrap(message,4,4);
                ByteBuffer humidity = ByteBuffer.wrap(message,8,4);
                temperature.order(ByteOrder.LITTLE_ENDIAN);
                pressure.order(ByteOrder.LITTLE_ENDIAN);
                humidity.order(ByteOrder.LITTLE_ENDIAN);
                float tp = temperature.getFloat();
                float hm = pressure.getFloat();
                int pr = humidity.getInt();
                System.out.println(tp + "C " + hm + "% RH " + (pr/100.0F) + " hPa");
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
