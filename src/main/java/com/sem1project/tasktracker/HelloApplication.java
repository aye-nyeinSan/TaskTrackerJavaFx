package com.sem1project.tasktracker;

import com.sem1project.tasktracker.controller.HelloController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
<<<<<<< HEAD
        //Load the home.
        FXMLLoader mainLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(mainLoader.load(), 600, 400);
        stage.setTitle("Hello!");
=======
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello gloria!");
>>>>>>> nawgloriawinnyunt
        stage.setScene(scene);
        stage.show();



    }


    public static void main(String[] args) {
        launch();
    }
}