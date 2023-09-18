package com.sem1project.tasktracker.controller;

import com.sem1project.tasktracker.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController{
    @FXML
    private Button resizebtn;
    @FXML
    private Button waterbtn;
    @FXML
    private ListView<String> inputListView;



    public void initialize() {

        waterbtn.setOnAction(e->{
            if(!inputListView.getItems().isEmpty()){
                System.out.println(System.getProperty("javafx.version"));
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty List");
                alert.setContentText("Please add files to the list before clicking this button.");
                alert.showAndWait();
            }
        });

        resizebtn.setOnAction(e->{
            if(!inputListView.getItems().isEmpty()){
            try {
//load the resize screen
                FXMLLoader resizeLoader = new FXMLLoader(HelloApplication.class.getResource("resizeview.fxml"));
                Scene scene1 = new Scene(resizeLoader.load(), 600, 400);
                ResizeviewController resizeController=resizeLoader.getController();

                List<String> inputListViewItems=inputListView.getItems();
                resizeController.setSelectedFiles(inputListViewItems);
                Stage currentStage=(Stage) resizebtn.getScene().getWindow();
                currentStage.setScene(scene1);
                currentStage.show();

            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
            else {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty");
                alert.setHeaderText("Empty List");
                alert.setContentText("Please add files to the list before clicking this button.");
                alert.showAndWait();
            }
            });

//Drag Over function
        inputListView.setOnDragOver(event -> {
            Dragboard db=event.getDragboard();
            String fileName=db.getFiles().get(0).getName().toLowerCase();
// List<String> extractedImageNames=new ArrayList<>();

            if(db.hasFiles() && (fileName.endsWith(".jpg") || fileName.endsWith(".png"))){
                event.acceptTransferModes(TransferMode.COPY);
            }

            else{
                event.consume();
            }
        });


//DragDrop function
        inputListView.setOnDragDropped(event -> {
            Dragboard db=event.getDragboard();
            boolean success=false;
            if(db.hasFiles()){
                success=true;
                String filePath,fileName;
                int total_files=db.getFiles().size();
                for(int i=0;i<total_files;i++){
                    File file=db.getFiles().get(i);
                    filePath=file.getAbsolutePath();
                    inputListView.getItems().add(filePath);
                }

            }
            event.setDropCompleted(success);
            event.consume();
        });

    }
    public void setSelectedFiles(List<String> selectedFiles) {
        if (inputListView != null && selectedFiles != null && !selectedFiles.isEmpty()) {
            inputListView.getItems().addAll(selectedFiles);
        }
    }






}