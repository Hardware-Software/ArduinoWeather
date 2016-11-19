package sample;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Controller implements  Initializable {

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

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        tempButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tempText.setText("Text");
            }
        });

        humidButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               humidText.setText("Text");
            }
        });

        pressButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pressText.setText("Text");
            }
        });

        lightButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lightText.setText("Text");
            }
        });
    }
}
