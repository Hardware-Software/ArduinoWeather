/**
 * Created by Andrew_2 on 11/18/2016.
 */
public class Packet {
    short temperature;
    byte humidity;
    short pressure;
    short light;
    short timestamp;
    Packet(short temp, byte hum, short press, short lit, short tsp){
        temperature = temp;
        humidity = hum;
        pressure = press;
        light = lit;
        timestamp = tsp;
    }
}
