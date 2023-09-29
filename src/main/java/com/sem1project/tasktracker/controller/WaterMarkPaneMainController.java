package com.sem1project.tasktracker.controller;

import com.sem1project.tasktracker.Launcher;


import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;


public class WaterMarkPaneMainController {
    @FXML
    private ImageView ImgPreview;
    @FXML private TextField watermarkText;

    @FXML private Slider visibilitySlider = new Slider(0,100,1);
    @FXML private Label visibility;
    @FXML private Label rotation;
    @FXML private Slider rotationSlider;
    @FXML private Label sizeLbl;
    @FXML private Slider sizeSlider;
    @FXML private ColorPicker colorPicker;
    @FXML private ComboBox<String> comboFileType;

    @FXML private ArrayList<Image> inputImages= new ArrayList<>();
    private ArrayList<Image> bufferedImages= new ArrayList<>();
    private int currentImageIndex = -1;
    private static int watermarkYPosition = 0;
    private static int watermarkXPosition = 0;

    public void initialize(){

        combotypeAddition();
        colorPicker.valueProperty().addListener(action->{
            this.updateWatermarkPreview();
        });

            //Bind Label and Slider
        visibility.setText("0%");
        rotation.setText("0°");
        sizeLbl.setText("0%");
        sizeSlider.setMax(1500);
        rotationSlider.setMin(-180);
        rotationSlider.setMax(180);

       DoubleBinding binding = visibilitySlider.valueProperty().divide(100).multiply(100);
        DoubleBinding sizebinding = sizeSlider.valueProperty().divide(1500).multiply(100);
        // Create a custom binding for rotation as a StringExpression
        StringExpression rotationBinding = Bindings.concat(
                Bindings.when(rotationSlider.valueProperty().lessThan(0))
                        .then(Bindings.format("-%.0f\u00B0", rotationSlider.valueProperty().negate()))
                        .otherwise(Bindings.format("%.0f\u00B0", rotationSlider.valueProperty()))
        );
        rotation.textProperty().bind(rotationBinding);
        bindValueToSlider(binding,visibilitySlider,visibility,"%%");
        bindValueToSlider(sizebinding,sizeSlider,sizeLbl,"%%");

        visibilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.updateWatermarkPreview();

        });
        rotationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            this.updateWatermarkPreview();
        });
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            this.updateWatermarkPreview();
        });

        //Update Preview
       watermarkText.textProperty().addListener((observable, oldValue, newValue) -> {

           this.updateWatermarkPreview();
        });

    }

    private void combotypeAddition() {
//        comboFileType.getItems().removeAll();
        comboFileType.getItems().addAll( "JPG","PNG","JPEG");
        comboFileType.getSelectionModel().select("JPG");
    }
    @FXML
    private void UpButtclicked(){
        System.out.println("up was clicked!");
        watermarkYPosition -= 30;
        updateWatermarkPreview();

    }
    @FXML
    private void downButtclicked(){
        System.out.println("Down was clicked!");
        watermarkYPosition += 30;
        updateWatermarkPreview();

    }
    @FXML
    private void rightButtclicked(){
        System.out.println("right was clicked!");
        watermarkXPosition += 30;
        updateWatermarkPreview();

    }
    @FXML
    private void leftButtclicked(){
        System.out.println("left was clicked!");
        watermarkXPosition -= 30;
        updateWatermarkPreview();

    }
    @FXML
    private void centerButtclicked(){
        System.out.println("center was clicked!");
        watermarkYPosition = 0;
        watermarkXPosition = 0;
        updateWatermarkPreview();

    }



    @FXML
    private void showNextImage() {
        if (!inputImages.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % inputImages.size();
            updateImageView();}
    }

    @FXML
    private void showPreviousImage() {
        if (!inputImages.isEmpty()) {
             currentImageIndex = (currentImageIndex - 1 + inputImages.size()) % inputImages.size();
            updateImageView();
        }
    }
    private void updateImageView() {
        if (currentImageIndex >= 0 && currentImageIndex < inputImages.size()) {
            Image img = inputImages.get(currentImageIndex);
            ImgPreview.setImage(img);
            updateWatermarkPreview();

        }
    }

    @FXML
   private void updateWatermarkPreview() {
        Color newColor = this.colorPicker.getValue();
        int newSize = (int) this.sizeSlider.getValue();
        String waterMarkText = this.watermarkText.getText();

        double visibility = this.visibilitySlider.getValue();
        double rotation = this.rotationSlider.getValue();
        System.out.println("Color:" + newColor + " Size:" + newSize + " waterMarkText:" + waterMarkText + " Visibility:" + visibility + " rotation:" + rotation);


        if (waterMarkText != null && !(this.inputImages.isEmpty())) {
            List<Image> watermarkedImages = new ArrayList();
            for (Image img:inputImages) {
                Image watermarkedImage = addWatermark(img, waterMarkText, newColor, newSize,visibility, rotation);
                watermarkedImages.add(watermarkedImage);
            }
            bufferedImages.clear();
            bufferedImages.addAll(watermarkedImages);
            this.ImgPreview.setImage(null);
            this.ImgPreview.setImage(watermarkedImages.get(currentImageIndex));

        }
    }

    private static <graphics> Image addWatermark(Image image, String watermarkText, Color newColor, int newSize, double visibility, double rotation) {
        String text = watermarkText;
        BufferedImage originalImage = SwingFXUtils.fromFXImage(image, null);

        BufferedImage watermarkedImage = new BufferedImage(
                originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = watermarkedImage.createGraphics();

        graphics.drawImage(originalImage, 0, 0, (ImageObserver)null);
        Font javafxFont = Font.font("Arial", FontWeight.BOLD, (double)newSize);
        java.awt.Font awtFont = new java.awt.Font(javafxFont.getFamily(), 0, newSize);

        double normalizedVisibility = (visibility - 0) / (100 - 0);
        java.awt.Color awtColor = new java.awt.Color((float)newColor.getRed(), (float)newColor.getGreen(), (float)newColor.getBlue(), (float) normalizedVisibility);
        graphics.setColor(awtColor);
        graphics.setFont(awtFont);



         int x = (watermarkedImage.getWidth() - graphics.getFontMetrics().stringWidth(text)) / 2 + watermarkXPosition;
        int  y = watermarkedImage.getHeight() / 2 + watermarkYPosition;
        System.out.println("X:"+x+" Y:"+y);

        if (rotation != 0.0) {
            double centerX = (double)x + (double)graphics.getFontMetrics().stringWidth(text) / 2.0;
            double centerY = (double)y;
            graphics.translate(centerX, centerY);
            graphics.rotate(Math.toRadians(rotation));
            graphics.translate(-centerX, -centerY);
        }

        graphics.drawString(text,x,y);

        graphics.dispose();
        return SwingFXUtils.toFXImage(watermarkedImage,null);
    }



    public void  bindValueToSlider(DoubleBinding binding,Slider slider,Label label,String unit){
         slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            label.textProperty().bind(binding.asString("%.0f"+unit));

        });
    }

    public void OnImgPreview(List<File> inputListViewItems ){
        showNextImage();
        for (File inputListViewItem : inputListViewItems) {
            String  filepath = inputListViewItem.getAbsolutePath();
            Image image = new Image("file:" + filepath);
            this.inputImages.add(image);

        }
        currentImageIndex = inputImages.size()-1;
        System.out.println("current size: "+ currentImageIndex);
        if(this.ImgPreview.getImage() == null){
        this.ImgPreview.setImage(inputImages.get(0));
        }
        System.out.println("Images from input listView are saved in image arraylist!");

    }


@FXML
    public void OnCancelWaterMark(){
        inputImages.clear();

       Launcher.getStage().close();
       Launcher.setOtherStagesOpen(false);

    }


    private String removeDoubleExtension(String filename, String selectedFiletype) {
        int lastIndex = filename.lastIndexOf("." + selectedFiletype);
        if (lastIndex != -1) {
            return filename.substring(0, lastIndex);
        }
        return filename;
    }

    @FXML
    private void OnApplyWaterMark() {
        FileChooser fileChooser = new FileChooser();
        String selectedFiletype = comboFileType.getValue().toLowerCase();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(selectedFiletype.toUpperCase() + " files", "*." + selectedFiletype)
        );

        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            // Remove the double extension if it exists
            String fileName = selectedFile.getName();
            fileName = removeDoubleExtension(fileName, selectedFiletype);

            // Now, fileName should have only one extension
            File newFile = new File(selectedFile.getParent(), fileName);
            savefiles(bufferedImages, newFile, selectedFiletype);

            System.out.println("Selected file:" + newFile);
        }
    }


    @FXML
    private void savefiles(ArrayList<Image> bufferedImages, File selectedFile,
                           String selectedFiletype) {
         if( bufferedImages.size() == 1) {
            save_as_individual(bufferedImages,selectedFile,selectedFiletype);
         } else if (bufferedImages.size()> 1) {
            save_as_zip(bufferedImages,selectedFile,selectedFiletype);
         }

    }
    @FXML
        public void save_as_individual(ArrayList<Image> bufferedImages, File selectedFile,  String selectedFiletype){
            Image imgToSave = bufferedImages.get(0);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imgToSave, null);
            BufferedImage bufferedImage1 = bufferAgain(bufferedImage);
            String fileName = selectedFile.getName()+"."+selectedFiletype;
            File outputFile = new File(selectedFile.getParent(), fileName);

            try {
                ImageIO.write(bufferedImage1, selectedFiletype, outputFile);
                System.out.println("Image saved successfully!" + outputFile);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error saving image: " + e.getMessage());
            }
        }
        @FXML
    public void save_as_zip(ArrayList<Image> bufferedImages, File selectedFile,  String selectedFiletype){
            String zipFileName = selectedFile.getAbsolutePath()+".zip";
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(zipFileName)))) {
                for (int i = 0; i < bufferedImages.size(); i++) {
                    Image imgToSave = bufferedImages.get(i);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imgToSave, null);
                    BufferedImage bufferedImage1 = bufferAgain(bufferedImage);
                    String entryName = selectedFile.getName() + i+"."+selectedFiletype ;

                    ZipEntry entry = new ZipEntry(entryName);
                    zipOutputStream.putNextEntry(entry);

                    ImageIO.write(bufferedImage1, selectedFiletype, zipOutputStream);
                    zipOutputStream.closeEntry();

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public BufferedImage bufferAgain(BufferedImage bufferedImage){
        BufferedImage bufferedImage1 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage1.createGraphics();

        graphics2D.drawImage(bufferedImage, 0, 0, null);

        return bufferedImage1;
    }


@FXML
    public void OnDefaultValue() {
        this.visibilitySlider.setValue(65);
        this.sizeSlider.setValue(100);
        this.rotationSlider.setValue(0);
    }


}
