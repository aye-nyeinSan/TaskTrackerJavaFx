package com.sem1project.tasktracker.controller.draw;

import com.sem1project.tasktracker.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class drawWaterMarkPane {
    @FXML private Button waterMarkButt;

    FXMLLoader loader;

    public  void OnDrawWaterMark() {
        waterMarkButt.setOnAction(actionEvent -> {
            // Load the WaterMarkPane.fxml
                loader = new FXMLLoader(Launcher.class.getResource("WaterMarkPane.fxml"));
                Parent root;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return; // Handle the exception as needed
                }

                // Create a new stage for the WaterMarkPane
                Stage watermarkStage = new Stage();
                watermarkStage.setTitle("Watermark Pane");
                watermarkStage.setResizable(false);
                watermarkStage.setScene(new Scene(root));

                // Show the stage
                watermarkStage.show();


        });


    }
}
