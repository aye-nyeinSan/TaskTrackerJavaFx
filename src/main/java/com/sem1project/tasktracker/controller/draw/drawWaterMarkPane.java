package com.sem1project.tasktracker.controller.draw;

import com.sem1project.tasktracker.Launcher;
import com.sem1project.tasktracker.controller.WaterMarkPaneMainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class drawWaterMarkPane {






    FXMLLoader loader;

    @FXML
    private ListView<File> inputListView;



    public void initialize() {
        //For the list View Cell
        inputListView.setCellFactory(param -> new ListCell<>() { //Custom Cell in ListView
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                } else {
                    setText(file.getName());
                }
            }
        });
        //Dragging the item
        inputListView.setOnDragOver(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();

            if (db.hasFiles() ) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            } else {
                dragEvent.consume();
            }//  it prevents other event handlers (if any) from being notified of the button click event.

        });

        //checking the target items are acceptable or not
        inputListView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()) {
                success = true;
                int total_files = dragboard.getFiles().size();
                //  String  filepath;
                for (int i = 0; i < total_files; i++) {
                    File file = dragboard.getFiles().get(i);
                    //filepath = dragboard.getFiles().get(i).getAbsolutePath();
                    inputListView.getItems().add(file);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public  void OnDrawWaterMark() throws IOException {

            System.out.println("Clicked watermark!!");
            // Load the WaterMarkPane.fxml
                loader = new FXMLLoader(Launcher.class.getResource("WaterMarkPane.fxml"));
                Parent root;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return; // Handle the exception as needed
                }
                //get controller WaterMarkPane.fxml >> controller is WaterMarkPaneMainController
                WaterMarkPaneMainController watermarkpanecontroller= loader.getController();
               List<File> inputListViewItems = inputListView.getItems();
               watermarkpanecontroller.OnImgPreview(inputListViewItems);

                // Create a new stage for the WaterMarkPane
                Stage watermarkStage = new Stage();
                Launcher.setStage(watermarkStage);
                watermarkStage.getIcons().add(new Image(Launcher.class.getResource("assets/1stsemesterIcon.jpg").toString()));
                watermarkStage.setTitle("Watermark Pane");
                watermarkStage.setResizable(false);
                watermarkStage.setScene(new Scene(root));

                // Show the stage
                watermarkStage.show();


    }

    public ListView<File> getInputListView() {
        return inputListView;
    }


}
