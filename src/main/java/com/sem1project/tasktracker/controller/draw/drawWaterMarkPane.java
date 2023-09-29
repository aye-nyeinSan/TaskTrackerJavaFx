package com.sem1project.tasktracker.controller.draw;

import com.sem1project.tasktracker.Launcher;

import com.sem1project.tasktracker.controller.ResizeviewController;

import com.sem1project.tasktracker.controller.WaterMarkPaneMainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class drawWaterMarkPane {


    FXMLLoader loader;
    @FXML Button deleteBtn;

    @FXML
    private ListView<File> inputListView;
    @FXML Button resizebtn;




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

           boolean isAccepted = false;
            List<File> files = db.getFiles();

            for (File file : files) {
                String fileName = file.getName().toLowerCase();

                    if (db.hasFiles() && fileName.endsWith(".png")
                            ||fileName.toLowerCase().endsWith(".jpg")
                            || fileName.toLowerCase().endsWith(".zip")) {
                       isAccepted = true;
                       if(isAccepted){
                            dragEvent.acceptTransferModes(TransferMode.COPY);
                        }


                    }else {
                        dragEvent.consume();

                }
            }
        });
        resizebtn.setOnAction(e->{
            if(!inputListView.getItems().isEmpty()){
                try {
//load the resize screen
                    FXMLLoader resizeLoader = new FXMLLoader(Launcher.class.getResource("resizeview.fxml"));
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
   //checking the target items are acceptable or not
        inputListView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();

             List<File> files = dragboard.getFiles();
            boolean success = false;

                     for (int i = 0 ;i<files.size();i++)  {
                        if(dragboard.hasFiles()&&
                                ( dragboard.getFiles().get(i).getName().toLowerCase().endsWith(".png")
                                        ||dragboard.getFiles().get(i).getName().toLowerCase().endsWith(".jpg"))){
                            success = true;
                            File file = dragboard.getFiles().get(i);
                            inputListView.getItems().add(file);

                        } else if (dragboard.getFiles().get(i).getName().toLowerCase().endsWith(".zip")) {
                            success = true;
                            File file = dragboard.getFiles().get(i);

                            zipArchive(file,inputListView);}
                     }



            event.setDropCompleted(success);
            event.consume();
        });


    }
    public void zipArchive(File zipFile, ListView<File> inputListView) {
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
                            inputListView.getItems().add(new File(extractedFile.getAbsolutePath()));
                        }

                    }
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void OnDeleteBtn()
    {     System.out.println("Delete was clicked!!");

        if (inputListView != null) {
            if(inputListView.getSelectionModel().getSelectedItem()== null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("You didn't select anything!\n Select any file you want to delete.");
                alert.showAndWait();
            }
         inputListView.getItems().remove(inputListView.getSelectionModel().getSelectedItem());
        }
    }
    @FXML




    public  void OnDrawWaterMark() throws IOException {

            System.out.println("Clicked watermark!!");
            // Load the WaterMarkPane.fxml


               List<File> inputListViewItems = inputListView.getItems();
               if( inputListViewItems.isEmpty())
               {
                   Alert alert = new Alert(Alert.AlertType.ERROR);
                   alert.setHeaderText(null);
                   alert.setTitle("Error");
                   alert.setContentText("Humm!!! \n It seems like you forgot to upload files.");
                   alert.showAndWait();
               }else
               {
              loader = new FXMLLoader(Launcher.class.getResource("WaterMarkWorkPane.fxml"));
                Parent root;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return; // Handle the exception as needed
                }
                //get controller WaterMarkPane.fxml >> controller is WaterMarkPaneMainController
                WaterMarkPaneMainController watermarkpanecontroller= loader.getController();

               watermarkpanecontroller.OnImgPreview(inputListViewItems);
               watermarkpanecontroller.OnDefaultValue();

                // Create a new stage for the WaterMarkPane
                Stage watermarkStage = new Stage();
                Launcher.setStage(watermarkStage);

                Launcher.setOtherStagesOpen(true);
                watermarkStage.getIcons().add(new Image(Launcher.class.getResource("assets/1stsemesterIcon.jpg").toString()));
                watermarkStage.setTitle("Watermark Pane");
                watermarkStage.setResizable(false);
                watermarkStage.setScene(new Scene(root));

                watermarkStage.setOnCloseRequest(action->{
                    Launcher.setOtherStagesOpen(false);
                });

                // Show the stage
                watermarkStage.show();
               }


    }

    public ListView<File> getInputListView() {
        return inputListView;
    }


}
