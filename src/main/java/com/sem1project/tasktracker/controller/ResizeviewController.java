package com.sem1project.tasktracker.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.awt.image.BufferedImage;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImageReadException;

import javax.imageio.ImageIO;

public class ResizeviewController {
    @FXML
    private ListView<File> listView;
    @FXML
    private ComboBox<String> comboFun, comboFile;
    @FXML
    private Button deleteAll;
    @FXML
    private Label label;
    @FXML
    private TextField widthValue;

    //    @FXML
//    private ImageView imgPreview;
    @FXML
    private AnchorPane anchorId;

    private File selectedFolder;


    public void setSelectedFiles(List<File> selectedFiles) {
        if (listView != null && selectedFiles != null && !selectedFiles.isEmpty()) {
            listView.getItems().addAll(selectedFiles);
        }

    }

    @FXML
    void select(ActionEvent event) {
        String s = comboFun.getSelectionModel().getSelectedItem().toString();
        label.setText(s);
    }


    public void initialize() {
        listView.setCellFactory(param -> new ListCell<>() { //Custom Cell in ListView
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

        //choosing resize functions
        ObservableList<String> functions = FXCollections.observableArrayList("Percentage", "Width", "Height");
        comboFun.setItems(functions);

        //choosing image file types
        ObservableList<String> fileTypes = FXCollections.observableArrayList("JPEG", "PNG");
        comboFile.setItems(fileTypes);

        deleteAll.setOnAction(e -> {
            listView.getItems().removeAll(listView.getItems());
        });

    }


    public void resize(ActionEvent event) {
        List<File> listViewItems = new ArrayList<>();
        for (File item : listView.getItems()) {
            listViewItems.add(item);
        }
        String selectedDir = comboFun.getValue();
        String widthStr = widthValue.getText();
        int sizeInt = Integer.parseInt(widthStr);
        if (!widthStr.isEmpty() && !listViewItems.isEmpty()) {
            // ArrayList for resized images
            List<BufferedImage> resizedImages = new ArrayList<>();

            // Create an ExecutorService with a fixed number of threads (e.g., 4)
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            CompletionService<BufferedImage> completionService = new ExecutorCompletionService<>(executorService);

            try {
                for (File selectedFile : listViewItems) {
                    completionService.submit(() -> {
                        File inputFile = new File(selectedFile.getAbsolutePath());
                        BufferedImage image = Imaging.getBufferedImage(inputFile);
                        BufferedImage resizedImage;

                        if (selectedDir.equals("Percentage")) {
                            // Resize by percentage
                            double scaleFactor = sizeInt / 100.0;
                            int newWidth = (int) (image.getWidth() * scaleFactor);
                            int newHeight = (int) (image.getHeight() * scaleFactor);
                            resizedImage = resizeImage(image, newWidth, newHeight);
                        } else if (selectedDir.equals("Width")) {
                            int newWidth = sizeInt;
                            int newHeight = (int) ((double) sizeInt / image.getWidth() * image.getHeight());
                            resizedImage = resizeImage(image, newWidth, newHeight);
                        } else if (selectedDir.equals("Height")) {
                            int newHeight = sizeInt;
                            int newWidth = (int) ((double) sizeInt / image.getHeight() * image.getWidth());
                            resizedImage = resizeImage(image, newWidth, newHeight);
                        } else {
                            return null;
                        }
                        return resizedImage;
                    });
                }

                for (int i = 0; i < listViewItems.size(); i++) {
                    try {
                        BufferedImage resizedImage = completionService.take().get();
                        if (resizedImage != null) {
                            resizedImages.add(resizedImage);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                saveToDirectory(resizedImages);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                executorService.shutdown();
            }
        }
    }
    private void saveToDirectory(List<BufferedImage> resizedImages) throws FileNotFoundException {
        String fileType = comboFile.getValue();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Resized Images");
        Stage stage = (Stage) anchorId.getScene().getWindow();
        File selectedDirectory = fileChooser.showSaveDialog(stage);

        if (selectedDirectory != null) {
            try {
                if (resizedImages.size() == 1) {
                    BufferedImage image = resizedImages.get(0);
                    String fileName = selectedDirectory.getName() + "." + fileType;
//               String fileName=selectedDirectory.getName();
                    File outputFile = new File(selectedDirectory.getParent(), fileName);
                    ImageIO.write(image, fileType, outputFile);
                } else if (resizedImages.size() > 1) {
                    String zipFileName = selectedDirectory.getAbsolutePath() + ".zip";
                    try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(zipFileName)))) {
                        int i = 1;
                        for (BufferedImage image : resizedImages) {
                            String entryName = selectedDirectory.getName() + i + "." + fileType;
                            ZipEntry entry = new ZipEntry(entryName);
                            zipOutputStream.putNextEntry(entry);
                            ImageIO.write(image, fileType, zipOutputStream);
                            zipOutputStream.closeEntry();
                            i++;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//        fileChooser.get

    private BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(originalImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return resizedImage;
    }

    public void browse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
        );
        Stage stage = (Stage) anchorId.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            listView.getItems().add(selectedFile);
        }

    }

    public void extension(ActionEvent event) {


    }
}