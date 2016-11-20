
import java.awt.event.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.event.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import javax.swing.*;

public class Controller implements  Initializable, BufferReadyEvent {

    @FXML
    private Button tempButton;
    @FXML
    private Button humidButton;
    @FXML
    private Button pressButton;
    @FXML
    private Button lightButton;
    @FXML
    private Text tempText;
    @FXML
    private Text humidText;
    @FXML
    private Text pressText;
    @FXML
    private Text lightText;
    private LinkedBlockingQueue lq;
    private SerialComm port;
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        lq = new LinkedBlockingQueue();
        port = new SerialComm("COM3",lq);
        port.addListener(this);
        ActionListener timer = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                port.send('A');
            }
        };
        Timer tm = new Timer(1000, timer);
        tm.setInitialDelay(3000);
        tm.start();
        tempButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                port.send('T');
            }
        });

        humidButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                port.send('H');
            }
        });

        pressButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                port.send('P');
            }
        });

        lightButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                port.send('L');
            }
        });
    }

    @Override
    public void bufferReady(char request){
        Packet data = new Packet(0,0,0);
        if(!lq.isEmpty()){
            data = (Packet)lq.remove();
            switch (request) {
                case 'T':{
                    tempText.setText(data.temperature + "C");
                    break;
                }
                case 'P':{
                    pressText.setText((data.pressure/100.0F)+ "hPa");
                    break;
                }
                case 'L':{
                    lightText.setText("Daytime/Nighttime");
                    break;
                }
                case 'H':{
                    humidText.setText(data.humidity + "RH");
                    break;
                }
                case 'A': {
                    tempText.setText(data.temperature + "C");
                    pressText.setText((data.pressure/100.0F)+ "hPa");
                    lightText.setText("Daytime/Nighttime");
                    humidText.setText(data.humidity + "RH");
                }
            }
        }
    }
}
