package com.sem1project.tasktracker;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.scene.image.Image;

import javafx.stage.Stage;



public class Launcher extends Application {


    public static Stage stage;
    private static boolean otherStageOpen = false;




    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage =stage;
        FXMLLoader fxmlLoader =new FXMLLoader(Launcher.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        this.stage.setTitle("JavaFx 1st Semester Project!");
        this.stage.getIcons().add(new Image(Launcher.class.getResource("assets/1stsemesterIcon.jpg").toString()));
        this.stage.setResizable(false);
        this.stage.setScene(scene);
        this.stage.show();

        this.stage.setOnCloseRequest(action->{

            if(otherStageOpen){
                action.consume();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("You have to cancel or close your current work to close our app.");
                alert.showAndWait();
                this.stage.toBack();
            }
        });


    }
    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Launcher.stage = stage;
    }

    public static void setOtherStagesOpen(boolean isOpen) {
        otherStageOpen = isOpen;
    }

}



