/**
 * Created by Andrew_2 on 11/18/2016.
 */
public class Packet {
    float temperature;
    float humidity;
    int pressure;
    short light;
    Packet(float temp, float hum, int press, short lit){
        temperature = temp;
        humidity = hum;
        pressure = press;
        light = lit;
    }
}
