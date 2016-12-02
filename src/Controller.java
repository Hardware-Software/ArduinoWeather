
import java.awt.event.*;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;
import javafx.event.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import jssc.SerialPort;
import jssc.SerialPortException;

import javax.swing.*;

public class Controller implements Initializable, BufferReadyEvent {

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

    boolean connect = false;

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
    private boolean initialConnect;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        TempLine = new XYChart.Series();
        TempLine.setName("Temperature");
        TemperatureChart.setAnimated(false);
        setColor(TemperatureChart, TempLine, 0);

        HumidLine = new XYChart.Series();
        HumidLine.setName("Humidity");
        HumidChart.setAnimated(false);
        setColor(HumidChart, HumidLine, 1);

        PressLine = new XYChart.Series();
        PressLine.setName("Pressure");
        PressureChart.setAnimated(false);
        setColor(PressureChart, PressLine, 2);

        LightLine = new XYChart.Series();
        LightLine.setName("Light");
        LightChart.setAnimated(false);
        setColor(LightChart, LightLine, 3);


        graphItButton.setOnAction(e -> {
            if (draw) {
                draw = false;
            } else {
                draw = true;
            }
        });

        connectButton.setOnAction(e -> {
            if (connect) {
                connect = false;
                //cleanAndFlush();
                connectButton.setText("Connect");
            } else {
                connect = true;
                //openPort();
                connectButton.setText("Disconnect");
            }
        });

        lq = new LinkedBlockingQueue();
        port = new SerialComm("COM6", lq);
        port.addListener(this);
        sentRecently = true;
        initialConnect = true; // We use this to request a complete data dump the first time we connect.
        ActionListener timer = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (sentRecently) {
                    connectText.setFill(Color.DARKGREEN);
                    connectText.setText("Connected");
                    sentRecently = false;
                    if (initialConnect) {
                        initialConnect = false;
                        port.send('C');
                    } else {
                        port.send('A');
                    }
                } else {
                    connectText.setFill(Color.FIREBRICK);
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

    private void setColor(LineChart chart, XYChart.Series s, int color) {
        XYChart.Series temp1 = new XYChart.Series();
        XYChart.Series temp2 = new XYChart.Series();
        XYChart.Series temp3 = new XYChart.Series();

        switch (color) {
            case 0: {
                drawChart(chart, s);
                break;
            }
            case 1: {
                drawChart(chart, temp1);
                drawChart(chart, s);
                chart.getData().remove(temp1);
                break;
            }
            case 2: {
                drawChart(chart, temp1);
                drawChart(chart, temp2);
                drawChart(chart, s);
                chart.getData().remove(temp1);
                chart.getData().remove(temp2);
                break;
            }
            case 3: {
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

    private void cleanAndFlush() {
        try {
            port.port.purgePort(SerialPort.PURGE_RXCLEAR);
            port.port.purgePort(SerialPort.PURGE_TXCLEAR);
            port.port.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private void openPort() {
        port.connect("COM6");
    }

    @Override
    public void bufferReady(char request) {
        Packet data;
        if (!lq.isEmpty()) {
            data = (Packet) lq.peek();
            sentRecently = true;
            tempText.setText(data.temperature + "F");
            pressText.setText((data.pressure) + "hPa");
            lightText.setText(data.light * 4 + "somethings");
            humidText.setText(data.humidity + "RH");
            if (draw) {
                while (!(lq.isEmpty())) {
                    data = (Packet) lq.remove();
                    final short temperature = data.temperature;
                    final short pressure = data.pressure;
                    final byte humidity = data.humidity;
                    final short light = data.light;
                    final short timestamp = data.timestamp;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            getChartData(TempLine, timestamp, temperature);
                            getChartData(PressLine, timestamp, pressure);
                            getChartData(HumidLine, timestamp, humidity);
                            getChartData(LightLine, timestamp, light);
                        }
                    });
                    ++time;
                }
            }


        }
    }
}
