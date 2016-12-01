import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.concurrent.LinkedBlockingQueue;

public class TestNewMain extends Application implements BufferReadyEvent {
    static LinkedBlockingQueue lq;
    static SerialComm serialPort;
    Text tempResults;
    Text humidResults;
    Text lightResults;
    Text pressResults;
    @Override
    public void start(Stage primaryStage) throws Exception{
        setup();
        //Create Grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        // set Title
        primaryStage.setTitle("Weather Station");
        Text title = new Text("Weather Station");
        grid.add(title, 0, 0, 2, 1);

        // Create buttons and add them to Grid
        Button temp = new Button("Temperature");
        grid.add(temp, 0, 1);


        Button humid = new Button("Humidity");
        grid.add(humid, 0, 2);

        Button light = new Button("Day/Night");
        grid.add(light, 0, 3);

        Button press = new Button("Air Pressure");
        grid.add(press, 0, 4);

        Button all = new Button("Get All");
        grid.add(all, 1, 5);

        //Create Text area for output
        tempResults = new Text();
        grid.add(tempResults, 1, 1);

        humidResults = new Text();
        grid.add(humidResults, 1, 2);

        lightResults = new Text();
        grid.add(lightResults, 1, 3);

        pressResults = new Text();
        grid.add(pressResults, 1, 4);

        temp.setOnAction(e -> getTemp(tempResults));
        press.setOnAction(e -> getPress(pressResults));
        light.setOnAction(e -> getLight(lightResults));
        humid.setOnAction(e -> getHumid(humidResults));
        all.setOnAction(e -> getAll());

        // Create Window and show it
        primaryStage.setScene(new Scene(grid, 270, 225));
        primaryStage.show();
    }

   private void getTemp(Text tempResults){
       serialPort.send('T');
 }

    private void getPress(Text pressResults){
        serialPort.send('P');
    }

    private void getLight(Text lightResults){
        serialPort.send('L');
    }

    private void getHumid(Text humidResults){
        serialPort.send('H');
    }

    private void getAll() {serialPort.send('A');}
    @Override
    public void bufferReady(char request){
        Packet data;
        if(!lq.isEmpty()){
            data = (Packet)lq.remove();
            switch (request) {
                case 'T':{
                    tempResults.setText("Temp " + data.temperature + "C");
                    break;
                }
                case 'P':{
                    pressResults.setText("Press " + data.pressure+ "Pa");
                    break;
                }
                case 'L':{
                    lightResults.setText("Daytime/Nighttime");
                    break;
                }
                case 'H':{
                    humidResults.setText("Humid " + data.humidity + "RH");
                    break;
                }
                case 'A': {
                    humidResults.setText("Humid " + data.humidity + "RH");
                    lightResults.setText("Daytime/Nighttime");
                    pressResults.setText("Press " + data.pressure+ "Pa");
                    tempResults.setText("Temp " + data.temperature + "C");
                }
            }
        }
    }
    private void setup(){
        lq = new LinkedBlockingQueue();
        serialPort = new SerialComm("COM3",lq);
        serialPort.addListener(this);
    }
    public static void main(String[] args) {
        launch(args);

    }
}
