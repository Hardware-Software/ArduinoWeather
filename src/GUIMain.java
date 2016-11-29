import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class GUIMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("NewGUI2.fxml"));
        primaryStage.setTitle("Hard-Weather");

        primaryStage.setScene(new Scene(root, 1000, 750));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(GUIMain.class, (java.lang.String[])null);;
    }



}
