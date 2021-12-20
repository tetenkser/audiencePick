package com.example.javaapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button confirmButton;

    @FXML
    void initialize() {
        confirmButton.setOnAction(actionEvent -> {
            Stage confirmStage = (Stage) confirmButton.getScene().getWindow();
            confirmStage.close();
        });
    }
}
