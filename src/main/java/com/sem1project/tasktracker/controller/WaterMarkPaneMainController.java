package com.sem1project.tasktracker.controller;

import com.sem1project.tasktracker.Launcher;
import com.sem1project.tasktracker.controller.draw.drawWaterMarkPane;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
    @FXML private Slider visibilitySlider;
    @FXML private Label visibility;


    public void initialize(){
            //Bind Label and Slider
            visibility.setText("0%");
            DoubleBinding visibilityBinding = visibilitySlider.valueProperty().divide(100).multiply(100);
            visibility.textProperty().bind(visibilityBinding.asString("%.0f%%"));

            
            visibilitySlider.valueProperty()
            .addListener((observable, oldValue, newValue) -> {
                // Update the label's text.
                visibility.setText(String.format("%.0f%%", newValue));
              });



        watermarkText.textProperty().addListener((observable, oldValue, newValue) -> {
            // Create a Text object with the updated text
            Text textNode = new Text(newValue);
            textNode.setStroke(Color.BLACK);
            textNode.setFont(Font.font("Arial", 300));

            // Create an ImageView with the current image in ImgPreview
            ImageView imageView = new ImageView(ImgPreview.getImage());

            // Create a StackPane to combine the image and text
            StackPane stackPane = new StackPane(imageView, textNode);

            // Create a SnapshotParameters object to render the combined StackPane
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            // Clear the previous content in ImgPreview
            ImgPreview.setImage(null);
           // stackPane.getChildren().addAll(imageView);

            // Take a snapshot of the combined StackPane and set it as the new image in ImgPreview
            ImgPreview.setImage(stackPane.snapshot(params, null));
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
