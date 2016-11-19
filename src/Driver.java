import sun.awt.image.ImageWatched;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Andrew_2 on 11/18/2016.
 */
public class Driver {
    static LinkedBlockingQueue buffer;
    static SerialComm serialHandler;


    public static void main(String[] args) {
        buffer = new LinkedBlockingQueue();
        serialHandler = new SerialComm("COM3",buffer);


    }
}
