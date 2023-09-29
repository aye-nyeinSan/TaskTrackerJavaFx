package com.sem1project.tasktracker.controller;

import com.sem1project.tasktracker.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public class HelloController{
    @FXML
    private Button resizebtn;
    @FXML
    private Button waterbtn;
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

                List<File> inputListViewItems=inputListView.getItems();
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

            if(db.hasFiles() && (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".zip"))){
                event.acceptTransferModes(TransferMode.COPY);
            }

            else{
                event.consume();
            }
        });


//DragDrop function
//        inputListView.setOnDragDropped(event -> {
//            Dragboard db=event.getDragboard();
//            boolean success=false;
//            if(db.hasFiles()){
//                List<File> files=db.getFiles();
//                for(File file:files){
//                    String filePath=file.getAbsolutePath();
//                    if(filePath.endsWith(".jpg") || filePath.endsWith(".png")){
//                        success=true;
//                        inputListView.getItems().add(filePath);
//                    }
//                    else if(filePath.endsWith(".zip")){
//                        success=true;
//                        zipArchive(file,inputListView);
//
//                    }
//                }
//            }
//            event.setDropCompleted(success);
//            event.consume();
//        });

        inputListView.setOnDragDropped(event -> {
            Dragboard db=event.getDragboard();
            List<File> files=db.getFiles();
            boolean success=false;
            for(int i=0;i<files.size();i++){
                File file=db.getFiles().get(i);
                if(db.hasFiles() && db.getFiles().get(i).getName().toLowerCase().endsWith(".jpg") || db.getFiles().get(i).getName().toLowerCase().endsWith(".png")){
                    success=true;

                    inputListView.getItems().add(file);
                }
                else if(db.getFiles().get(i).getName().toLowerCase().endsWith(".zip")){
                    success=true;
                    zipArchive(file,inputListView);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

    }

    public void zipArchive(File zipFile,ListView<File> inputListView) {
        try {
            try (ZipArchiveInputStream zipInput = new ZipArchiveInputStream(new FileInputStream(zipFile))) {
                ZipArchiveEntry entry;
                while ((entry = zipInput.getNextZipEntry()) != null) {
                    if (!entry.isDirectory()) {
                        String entryName = entry.getName();
                        if(entryName.endsWith(".jpg") || entryName.endsWith(".png")){
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            File extractedFile = new File("path_to_extracted_files", entryName);
                            extractedFile.getParentFile().mkdirs();
                            try (FileOutputStream outputStream = new FileOutputStream(extractedFile)) {
                                while ((bytesRead = zipInput.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                            }
                            inputListView.getItems().add(extractedFile);
                        }

                    }
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setSelectedFiles(List<File> selectedFiles) {
        if (inputListView != null && selectedFiles != null && !selectedFiles.isEmpty()) {
            inputListView.getItems().addAll(selectedFiles);
        }
    }






}