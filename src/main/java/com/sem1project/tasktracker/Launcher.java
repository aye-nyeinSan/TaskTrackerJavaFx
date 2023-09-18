package com.sem1project.tasktracker;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.image.Image;

import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Launcher extends Application {


    public static Stage stage;



    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage =stage;
        FXMLLoader fxmlLoader =new FXMLLoader(Launcher.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 540);
        this.stage.setTitle("JavaFx 1st Semester Project!");

        this.stage.getIcons().add(new Image(Launcher.class.getResource("assets/1stsemesterIcon.jpg").toString()));
        this.stage.setResizable(false);

        this.stage.setScene(scene);
        this.stage.show();

    }
    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Launcher.stage = stage;
    }
}



