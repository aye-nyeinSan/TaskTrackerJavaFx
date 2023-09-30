package com.sem1project.tasktracker.controller.exception;

import com.sem1project.tasktracker.Launcher;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static com.sem1project.tasktracker.Launcher.stage;

public class CustomExceptionHandler extends Exception {
    public void showAlert(String s, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        stage.getIcons().add(new Image(Launcher.class.getResource("assets/appIcon.png").toString()));
        alert.setTitle(type.name());
        alert.setContentText(s);
        alert.showAndWait();
    }


}
