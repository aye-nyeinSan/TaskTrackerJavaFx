package com.sem1project.tasktracker;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;


public class Launcher extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =new FXMLLoader(Launcher.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 540);
        stage.setTitle("JavaFx 1st Semester Project!");
        stage.getIcons().add(new Image(Launcher.class.getResource("assets/1stsemesterIcon.jpg").toString()));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }
}



