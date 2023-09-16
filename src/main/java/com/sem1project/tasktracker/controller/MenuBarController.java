package com.sem1project.tasktracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;

public class MenuBarController {
    @FXML
    private MenuItem aboutUs;

    public void MainController(){
        aboutUs.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("About Us");
            alert.setContentText("This project is created by Aye Nyein San (652115502) And Naw Gloria Win Nyunt (652115513).");
            alert.showAndWait();
        });
    }

}
