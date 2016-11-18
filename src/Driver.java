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
                ByteBuffer data1 = ByteBuffer.wrap(message,0,2);
                ByteBuffer data2 = ByteBuffer.wrap(message,2,2);
                ByteBuffer data3 = ByteBuffer.wrap(message,4,2);
                ByteBuffer data4 = ByteBuffer.wrap(message,6,2);
                data1.order(ByteOrder.LITTLE_ENDIAN);
                data2.order(ByteOrder.LITTLE_ENDIAN);
                data3.order(ByteOrder.LITTLE_ENDIAN);
                data4.order(ByteOrder.LITTLE_ENDIAN);
                short test1 = data1.getShort();
                short test2 = data2.getShort();
                short test3 = data3.getShort();
                short test4 = data4.getShort();
                System.out.println(test1 + " " + test2 + " " + test3 + " " + test4);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
