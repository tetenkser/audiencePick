package com.example.javaapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class ErrorController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button errorButton;

    @FXML
    void initialize() {
        errorButton.setOnAction(actionEvent -> {
            Stage errorStage = (Stage) errorButton.getScene().getWindow();
            errorStage.close();
        });
    }

}
