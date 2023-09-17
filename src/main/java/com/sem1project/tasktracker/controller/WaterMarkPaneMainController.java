package com.sem1project.tasktracker.controller;

import com.sem1project.tasktracker.Launcher;
import com.sem1project.tasktracker.controller.draw.drawWaterMarkPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.util.List;

public class WaterMarkPaneMainController {
    @FXML private ImageView ImgPreview;
    @FXML private TextField watermarkText;

    public void initialize(){
        watermarkText.textProperty().addListener((observable, oldValue, newValue) -> {
            // Update the image preview here
            Text textNode = new Text(this.watermarkText.getText());
            textNode.setStroke(Color.BLACK);
             textNode.setFont(Font.font("Arial", 300));

            // Create a StackPane to overlay the text on the image
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(
                    new ImageView(ImgPreview.getImage()),
                    textNode
            );

            // Create a SnapshotParameters object to render the Text object
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            // Render the Text object to an image
            WritableImage image = stackPane.snapshot(params, null);
            ImgPreview.setImage(image);

        });

    }
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
