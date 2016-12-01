
import java.awt.event.*;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.event.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javax.swing.*;

public class Controller implements  Initializable, BufferReadyEvent {

    @FXML
    private Button connectButton;
    @FXML
    private Button graphItButton;
    @FXML
    private Button RateButton;

    @FXML
    private TextField RateField;

    @FXML
    private LineChart TemperatureChart;
    @FXML
    private LineChart PressureChart;
    @FXML
    private LineChart LightChart;
    @FXML
    private LineChart HumidChart;


    private XYChart.Series TempLine;

    private XYChart.Series PressLine;

    private XYChart.Series HumidLine;

    private XYChart.Series LightLine;

    int time = 0;

    boolean draw = false;

    @FXML
    private Text tempText;
    @FXML
    private Text humidText;
    @FXML
    private Text pressText;
    @FXML
    private Text lightText;
    @FXML
    private Text connectText;
    private LinkedBlockingQueue lq;
    private SerialComm port;
    private boolean sentRecently;
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        TempLine = new XYChart.Series();
        TempLine.setName ("Temperature");
        setColor(TemperatureChart, TempLine, 0);

        HumidLine = new XYChart.Series();
        HumidLine.setName ("Humidity");
        setColor(HumidChart, HumidLine, 1);

        PressLine = new XYChart.Series();
        PressLine.setName ("Pressure");
        setColor(PressureChart, PressLine, 2);

        LightLine = new XYChart.Series();
        LightLine.setName ("Light");
        setColor(LightChart, LightLine, 3);


        graphItButton.setOnAction(e -> {
            if (draw)
            {
                draw = false;
            }
            else
            {
                draw = true;
            }
        });

        lq = new LinkedBlockingQueue();
        port = new SerialComm("COM3",lq);
        port.addListener(this);
        sentRecently = true;
        ActionListener timer = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                if(sentRecently) {
                    connectText.setText("Connected");
                    sentRecently = false;
                    port.send('A');
                }else {
                    connectText.setText("Not Connected");
                }
            }
        };
        Timer tm = new Timer(1000, timer);
        tm.setInitialDelay(3000);
        tm.start();
    }

    private void getChartData(XYChart.Series s, double time, double value) {
        s.getData().add(new XYChart.Data(time, value));
    }

    private void drawChart(LineChart chart, XYChart.Series s) {
        chart.getData().add(s);
    }

    private void setColor(LineChart chart, XYChart.Series s, int color)
    {
        XYChart.Series temp1 = new XYChart.Series();
        XYChart.Series temp2 = new XYChart.Series();
        XYChart.Series temp3 = new XYChart.Series();

        switch (color) {
            case 0 :{
                drawChart(chart, s);
                break;
            }
            case 1 :{
                drawChart(chart, temp1);
                drawChart(chart, s);
                chart.getData().remove(temp1);
                break;
            }
            case 2 :{
                drawChart(chart, temp1);
                drawChart(chart, temp2);
                drawChart(chart, s);
                chart.getData().remove(temp1);
                chart.getData().remove(temp2);
                break;
            }
            case 3 :{
                drawChart(chart, temp1);
                drawChart(chart, temp2);
                drawChart(chart, temp3);
                drawChart(chart, s);
                chart.getData().remove(temp1);
                chart.getData().remove(temp2);
                chart.getData().remove(temp3);
                break;
            }
    }

    }

    @Override
    public void bufferReady(char request){
        Packet data;
        if(!lq.isEmpty()){
            data = (Packet)lq.remove();
            switch (request) {
                case 'T':{
                    sentRecently = true;
                    tempText.setText(data.temperature + "C");
                    if (draw) {
                        getChartData(TempLine, time, data.temperature);
                    }
                    break;
                }
                case 'P':{
                    sentRecently = true;
                    pressText.setText((data.pressure/100.0F)+ "hPa");
                    if (draw) {
                        getChartData(TempLine, time, data.pressure);
                    }
                    break;
                }
                case 'L':{
                    sentRecently = true;
                    lightText.setText("Daytime/Nighttime");
                    if (draw) {
                        getChartData(TempLine, time, data.light);
                    }
                    break;
                }
                case 'H':{
                    sentRecently = true;
                    humidText.setText(data.humidity + "RH");
                    if (draw) {
                        getChartData(TempLine, time, data.humidity);
                    }
                    break;
                }
                case 'A': {
                    sentRecently = true;
                    tempText.setText(data.temperature + "F");
                    pressText.setText((data.pressure)+ "hPa");
                    lightText.setText(data.light*4 + "somethings");
                    humidText.setText(data.humidity + "RH");
                    if (draw) {
                        getChartData(TempLine, time, data.temperature);
                        getChartData(PressLine, time, data.pressure);
                        getChartData(HumidLine, time, data.humidity);
                        getChartData(LightLine, time, data.light*4);
                        ++time;
                    }
                }
            }
        }
    }
}
