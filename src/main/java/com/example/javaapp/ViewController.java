package com.example.javaapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Record> Table;

    @FXML
    private TableColumn<Record, String> audienceColumn;

    @FXML
    private TableColumn<Record, String> teacherColumn;

    @FXML
    private TableColumn<Record, String> dateColumn;

    @FXML
    private TableColumn<Record, String> timeColumn;

    @FXML
    private TextField audienceField;

    @FXML
    private TextField teacherField;

    @FXML
    private TextField dateField;

    @FXML
    private TextField timeField;

    @FXML
    private Button submitButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button refreshButton;

    String query = null;
    Connection connection = null ;
    PreparedStatement preparedStatement = null ;
    ResultSet resultSet = null ;

    ObservableList<Record> RecordList = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        LoadDate();
        submitButton.setOnAction(actionEvent -> sendInput());
        deleteButton.setOnAction(actionEvent -> removeInput());
        searchButton.setOnAction(actionEvent -> searchInput());
        refreshButton.setOnAction(actionEvent -> refreshTable());
    }

    public static void getError() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("error.fxml"));
        Scene errorScene = new Scene(fxmlLoader.load(), 325, 240);
        Stage errorStage = new Stage();
        errorStage.setTitle("Error!");
        errorStage.getIcons().add(new Image(Objects.requireNonNull(ViewController.class.getResourceAsStream
                ("/com/example/javaapp/iconTrouble.png"))));
        errorStage.setResizable(false);
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.setScene(errorScene);
        errorStage.showAndWait();
    }

    public static void getConfirm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("confirm.fxml"));
        Scene confirmScene = new Scene(fxmlLoader.load(), 325, 240);
        Stage confirmStage = new Stage();
        confirmStage.setTitle("Confirm!");
        confirmStage.getIcons().add(new Image(Objects.requireNonNull(ViewController.class.getResourceAsStream
                ("/com/example/javaapp/iconConfirm.png"))));
        confirmStage.setResizable(false);
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.setScene(confirmScene);
        confirmStage.showAndWait();
    }

    public static void getWarning() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("warning.fxml"));
        Scene warningScene = new Scene(fxmlLoader.load(), 325, 240);
        Stage warningStage = new Stage();
        warningStage.setTitle("Warning!");
        warningStage.getIcons().add(new Image(Objects.requireNonNull(ViewController.class.getResourceAsStream
                ("/com/example/javaapp/iconTrouble.png"))));
        warningStage.setResizable(false);
        warningStage.initModality(Modality.APPLICATION_MODAL);
        warningStage.setScene(warningScene);
        warningStage.showAndWait();
    }

    private String dateSQL(String inputDate) {
        if (!(inputDate.equals(""))) {
            String[] dateDetail = inputDate.split("\\.");
            return dateDetail[2] + "-" + dateDetail[1] + "-" + dateDetail[0];
        }
        return null;
    }

    private String dateTable(String inputDate) {
        if (!(inputDate.equals(""))) {
            String[] dateDetail = inputDate.split("-");
            return dateDetail[2] + "." + dateDetail[1] + "." + dateDetail[0];
        }
        return null;
    }

    private String timeTable(String inputTime) {
        if (!(inputTime.equals(""))) {
            String[] timeDetail = inputTime.split(":");
            return timeDetail[0] + ":" + timeDetail[1];
        }
        return null;
    }

    private String timeSQL(String inputTime) {
        if (!(inputTime.equals(""))) {
            return inputTime + ":00";
        }
        return null;
    }

    private boolean checkDate(String inputDate) {
        if (!(inputDate.equals(""))) {
            String[] dateDetail = inputDate.split("\\.");

            if (dateDetail[0].startsWith("0"))
                dateDetail[0] = dateDetail[0].replace("0","");

            if (dateDetail[1].startsWith("0"))
                dateDetail[1] = dateDetail[1].replace("0","");

            if (dateDetail[2].startsWith("0"))
                dateDetail[2] = dateDetail[2].replace("0","");

            if (Integer.parseInt(dateDetail[0]) > 31 || Integer.parseInt(dateDetail[0]) < 1)
                return false;
            if (Integer.parseInt(dateDetail[1]) > 12 || Integer.parseInt(dateDetail[1]) < 1)
                return false;
            if (Integer.parseInt(dateDetail[2]) < 1000 || Integer.parseInt(dateDetail[2]) > 9999 )
                return false;
        }
        else return false;

        return true;
    }

    private boolean checkTime(String inputTime) {
        if (!(inputTime.equals(""))) {
            String[] timeDetail = inputTime.split(":");

            if (timeDetail[0].startsWith("0"))
                timeDetail[0] = timeDetail[0].replace("0","");

            if (timeDetail[1].startsWith("0"))
                timeDetail[1] = timeDetail[1].replace("0","");

            if (!(timeDetail[0].equals("")))
                if (Integer.parseInt(timeDetail[0]) > 23 || Integer.parseInt(timeDetail[0]) < 0)
                 return false;

            if (!(timeDetail[1].equals("")))
                if (Integer.parseInt(timeDetail[1]) > 60 || Integer.parseInt(timeDetail[1]) < 0)
                    return false;
        }
        else return false;

        return true;
    }

    private boolean checkDb(String audience, String date, String time) {
        query = "SELECT audience, teacher, date, time FROM audience WHERE " +
                "audience='" + audience + "' AND date ='" + dateSQL(date) + "' AND time ='" + timeSQL(time) + "'";
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                if (resultSet.getString("teacher") != null) {
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private void addTable(String audience, String teacher, String date, String time) {
        query = "INSERT INTO audience(audience, teacher, date, time)" +
                "VALUES(?,?,?,?)";
        try {
            preparedStatement = connection.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            preparedStatement.setString(1, audience);
            preparedStatement.setString(2, teacher);
            preparedStatement.setString(3, dateSQL(date));
            preparedStatement.setString(4, timeSQL(time));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteTable(String audience, String teacher, String date, String time) {
        query = "DELETE FROM audience WHERE audience=? AND teacher=? AND date=? AND time=?";

        try {
            preparedStatement = connection.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            preparedStatement.setString(1, audience);
            preparedStatement.setString(2, teacher);
            preparedStatement.setString(3, dateSQL(date));
            preparedStatement.setString(4, timeSQL(time));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filterTable(String audience, String teacher, String date, String time) {
        query = "SELECT audience, teacher, date, time FROM audience WHERE";

        if (!(audience.equals(""))) {
            query += " audience='" + audience + "'";
        }

        if (!(teacher.equals(""))) {
            if ((query.endsWith("audience='" + audience + "'")))
                query += " AND";

            query += " teacher='" + teacher + "'";
        }

        if (!(date.equals(""))) {
            if (checkDate(date)) {
                if ((query.endsWith("audience='" + audience + "'")))
                    query += " AND";

                if ((query.endsWith("teacher='" + teacher + "'")))
                    query += " AND";

                query += " date='" + dateSQL(date) + "'";
            }
            else {
                try {
                    getError();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!(time.equals(""))) {
            if (checkTime(time)) {
                if ((query.endsWith("audience='" + audience + "'")))
                    query += " AND";

                if ((query.endsWith("teacher='" + teacher + "'")))
                    query += " AND";

                if (!(date.equals("")))
                    if (query.endsWith("date='" + dateSQL(date) + "'"))
                        query += " AND";

                query += " time='" + timeSQL(time) + "'";
            }
            else try {
                getError();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            RecordList.clear();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RecordList.add(new Record(
                        resultSet.getString("audience"),
                        resultSet.getString("teacher"),
                        dateTable(String.valueOf(resultSet.getDate("date"))),
                        timeTable(String.valueOf(resultSet.getTime("time")))));
                Table.setItems(RecordList);
            }
        } catch (SQLException ex) {
        Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
    }
    }

    private void refreshTable() {
        try {
            RecordList.clear();
            query = "SELECT * FROM audience";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RecordList.add(new Record(
                        resultSet.getString("audience"),
                        resultSet.getString("teacher"),
                        dateTable(String.valueOf(resultSet.getDate("date"))),
                        timeTable(String.valueOf(resultSet.getTime("time")))));
                Table.setItems(RecordList);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void LoadDate() {
        connection = DbConnect.getConnect();
        refreshTable();

        audienceColumn.setCellValueFactory(new PropertyValueFactory<>("audience"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

    }

    private void sendInput() {
        String audienceText = audienceField.getText().trim();
        String teacherText = teacherField.getText();
        String dateText = dateField.getText();
        String timeText = timeField.getText();

        if (!(audienceText.equals("")) && !(teacherText.equals("")) &&
                !(dateText.equals("")) && !(timeText.equals(""))) {
            if (checkDate(dateText) && checkTime(timeText)) {
                if (checkDb(audienceText, dateText, timeText)) {
                    addTable(audienceText, teacherText, dateText, timeText);
                    try {
                        getConfirm();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        getWarning();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                try {
                    getError();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            try {
                getError();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeInput() {
        String audienceText = audienceField.getText().trim();
        String teacherText = teacherField.getText();
        String dateText = dateField.getText();
        String timeText = timeField.getText();

        if (!(audienceText.equals("")) && !(teacherText.equals("")) &&
                !(dateText.equals("")) && !(timeText.equals(""))) {
            if (checkDate(dateText) && checkTime(timeText)) {
                deleteTable(audienceText, teacherText, dateText, timeText);
                try {
                    getConfirm();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    getError();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            try {
                getError();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchInput() {
        String audienceText = audienceField.getText().trim();
        String teacherText = teacherField.getText();
        String dateText = dateField.getText();
        String timeText = timeField.getText();

        if (!((audienceText.equals("")) && (teacherText.equals("")) &&
                (dateText.equals("")) && (timeText.equals("")))) {
            filterTable(audienceText, teacherText, dateText, timeText);
        } else {
            try {
                getError();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

