package com.sem1project.tasktracker.controller;

import com.sem1project.tasktracker.Launcher;
import com.sem1project.tasktracker.controller.draw.drawWaterMarkPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.List;

public class WaterMarkPaneMainController {
    @FXML
    private ImageView ImgPreview;



    public void OnImgPreview(List<File> inputListViewItems ){


        for (File inputListViewItem : inputListViewItems) {
            String  filepath = inputListViewItem.getAbsolutePath();
            Image image = new Image("file:" + filepath);
            this.ImgPreview.setImage(image);
        }
    }


    public void OnCancelWaterMark(){
       Launcher.getStage().close();
    }


    public void OnApplyWaterMark(ActionEvent actionEvent) {

    }
}
