package com.example.javaapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class WarningController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button warningButton;

    @FXML
    void initialize() {
        warningButton.setOnAction(actionEvent -> {
            Stage warningStage = (Stage) warningButton.getScene().getWindow();
            warningStage.close();
        });
    }

}
