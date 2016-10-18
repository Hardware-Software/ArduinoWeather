import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
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
        Text tempResult = new Text();
        grid.add(tempResult, 1, 1);

        Text humidResult = new Text();
        grid.add(humidResult, 1, 2);

        Text lightResult = new Text();
        grid.add(lightResult, 1, 3);

        Text pressResult = new Text();
        grid.add(pressResult, 1, 4);

        temp.setOnAction(e -> getTemp(tempResult));
        press.setOnAction(e -> getPress(pressResult));
        light.setOnAction(e -> getLight(lightResult));
        humid.setOnAction(e -> getHumid(humidResult));
        all.setOnAction(e -> { getTemp(tempResult);
            getPress(pressResult);
            getLight(lightResult);
            getHumid(humidResult); });

        // Create Window and show it
        primaryStage.setScene(new Scene(grid, 270, 225));
        primaryStage.show();
    }

   private void getTemp(Text tempResults){ tempResults.setText("Temp " + "\u00b0" + "C"); }

    private void getPress(Text pressResults){
        pressResults.setText("Press " + "Pa");
    }

    private void getLight(Text lightResults){
        lightResults.setText("Daytime/Nighttime");
    }

    private void getHumid(Text humidResults){
        humidResults.setText("Humid " + "RH");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
