package com.sem1project.tasktracker.controller.exception;

import javafx.scene.control.Alert;

public class CustomExceptionHandler extends Exception {
    public void showAlert(String s, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(type.name());
        alert.setContentText(s);
        alert.showAndWait();
    }


}
