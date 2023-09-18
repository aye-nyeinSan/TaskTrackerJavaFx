package com.sem1project.tasktracker.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.awt.image.BufferedImage;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;

import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImageReadException;

import javax.imageio.ImageIO;

public class ResizeviewController {
    @FXML
    private ListView<String> listView;
    @FXML
    private ComboBox<String> comboFun;
    @FXML
    private Button deleteAll;
    @FXML
    private Label label;
    @FXML
    private TextField widthValue;
    @FXML
    private ImageView imgPreview;

    public void setSelectedFiles(List<String> selectedFiles) {
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
        ObservableList<String> functions = FXCollections.observableArrayList("Percentage", "Width", "Height");
        comboFun.setItems(functions);

        deleteAll.setOnAction(e -> {
            listView.getItems().removeAll(listView.getItems());
        });

    }


    public void resize(ActionEvent event) {
        List<String> listViewItems = new ArrayList<>();
        for (String item : listView.getItems()) {
            listViewItems.add(item);
        }
        String selectedDir = comboFun.getValue();
        String widthStr = widthValue.getText();
        int sizeInt = Integer.parseInt(widthStr);
        if (!widthStr.isEmpty() && !listViewItems.isEmpty()) {
             try {
             for(String selectedFile:listViewItems){
             File inputFile=new File(selectedFile);
             BufferedImage image=Imaging.getBufferedImage(inputFile);
                 System.out.println(image.getWidth());
                 System.out.println(image.getHeight());

             BufferedImage resizedImage;

             if(selectedDir=="Percentage"){
             //resize by percentage
             double scaleFactor=sizeInt/100.0;
             int newWidth=(int)(image.getWidth()*scaleFactor);
             int newHeight=(int)(image.getHeight()*scaleFactor);
             resizedImage=resizeImage(image,newWidth,newHeight);


             } else if (selectedDir=="Width") {
             int newWidth=sizeInt;
             int newHeight=(int)image.getHeight();
             resizedImage=resizeImage(image,newWidth,newHeight);


             } else if (selectedDir=="Height") {
             int newHeight=sizeInt;
             int newWidth=(int)image.getWidth();
             resizedImage=resizeImage(image,newWidth,newHeight);
             }
             else{
             continue;
             }
                 System.out.println(resizedImage.getWidth());
                 System.out.println(resizedImage.getHeight());
             Image fxImage=SwingFXUtils.toFXImage(resizedImage,null);
             imgPreview.setImage(fxImage);

             }
             } catch (IOException e) {
             e.printStackTrace();
             } catch (ImageReadException e) {
             throw new RuntimeException(e);
             }

        }
    }

        private BufferedImage resizeImage(BufferedImage originalImage,int newWidth,int newHeight){
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            resizedImage.createGraphics().drawImage(originalImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            return resizedImage;
        }

}