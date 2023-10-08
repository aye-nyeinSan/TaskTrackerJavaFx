package com.sem1project.tasktracker.controller;

import com.sem1project.tasktracker.Launcher;


import com.sem1project.tasktracker.controller.exception.CustomExceptionHandler;
import javafx.application.Platform;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
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


    private CustomExceptionHandler watermarkException = new CustomExceptionHandler();
    private Map<Image, Integer> imageWatermarkSizeMap = new HashMap<>();


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
        String waterMarkText = this.watermarkText.getText();
        double visibility = this.visibilitySlider.getValue();
        double rotation = this.rotationSlider.getValue();
        double newSize = sizeSlider.getValue();
            System.out.println(newSize);
        // Update the imageWatermarkSizeMap for all images with the new size
        for (Image img : inputImages) {
            imageWatermarkSizeMap.put(img, (int) newSize);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        ExecutorCompletionService<Image> completionService = new ExecutorCompletionService<>(executorService);

        try {
            if (waterMarkText != null && !(this.inputImages.isEmpty())) {
                List<Future<Image>> watermarkedImages = new ArrayList<>();
                for (Image img : inputImages) {
                    int watermarkSize = imageWatermarkSizeMap.get(img); // Get the watermark size from the map

                    System.out.println("Color:" + newColor + " Size:" + watermarkSize + " waterMarkText:" + waterMarkText + " Visibility:" + visibility + " rotation:" + rotation);

                    Future<Image> future = completionService.submit(() -> {
                        return addWatermark(img, waterMarkText, newColor, watermarkSize, visibility, rotation);
                    });

                    watermarkedImages.add(future);
                }

                bufferedImages.clear();
                for (Future<Image> future : watermarkedImages) {
                    try {
                        Image watermarkedImage = future.get();
                        bufferedImages.add(watermarkedImage);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                this.ImgPreview.setImage(null);
                this.ImgPreview.setImage(bufferedImages.get(currentImageIndex));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

//    @FXML
//   private void updateWatermarkPreview() {
//        Color newColor = this.colorPicker.getValue();
//       // watermarkSize = (int) sizeSlider.getValue();
//        String waterMarkText = this.watermarkText.getText();
//
//        double visibility = this.visibilitySlider.getValue();
//        double rotation = this.rotationSlider.getValue();
//
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
//        ExecutorCompletionService<Image> completionService = new ExecutorCompletionService<>(executorService);
//        try {
//            if (waterMarkText != null && !(this.inputImages.isEmpty())) {
//                 List<Future<Image>> watermarkedImages = new ArrayList();
//                for (Image img : inputImages) {
//                    // Get the watermark size from the map
//                    int watermarkSize = imageWatermarkSizeMap.getOrDefault(img, (int) sizeSlider.getValue());
//                    System.out.println("Color:" + newColor + " Size:" + watermarkSize + " waterMarkText:" + waterMarkText + " Visibility:" + visibility + " rotation:" + rotation);
//
//                    Future<Image> future = completionService.submit(() -> {
//                        return addWatermark(img, waterMarkText, newColor, watermarkSize, visibility, rotation);
//                    });
//
//                    watermarkedImages.add(future);
//                }
//
//                bufferedImages.clear();
//                for (Future<Image> future : watermarkedImages) {
//                    try {
//                        Image watermarkedImage = future.get();
//                        bufferedImages.add(watermarkedImage);
//                    } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                this.ImgPreview.setImage(null);
//                this.ImgPreview.setImage(bufferedImages.get(currentImageIndex));
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }finally{
//            executorService.shutdown();
//        }
//
//
//        }


    private static <graphics> Image addWatermark(Image image, String watermarkText, Color newColor,double newSize, double visibility, double rotation) {
        String text = watermarkText;
        BufferedImage originalImage = SwingFXUtils.fromFXImage(image, null);

        BufferedImage watermarkedImage = new BufferedImage(
                originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = watermarkedImage.createGraphics();

        graphics.drawImage(originalImage, 0, 0, (ImageObserver)null);
        Font javafxFont = Font.font("Arial", FontWeight.BOLD, (double)newSize);
        java.awt.Font awtFont = new java.awt.Font(javafxFont.getFamily(), 0, (int) newSize);

        double normalizedVisibility = (visibility - 0) / (100 - 0);
        java.awt.Color awtColor = new java.awt.Color((float)newColor.getRed(), (float)newColor.getGreen(), (float)newColor.getBlue(), (float) normalizedVisibility);
        graphics.setColor(awtColor);
        graphics.setFont(awtFont);
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
        //showNextImage();
        for (File inputListViewItem : inputListViewItems) {
            String  filepath = inputListViewItem.getAbsolutePath();
            Image image = new Image("file:" + filepath);
            this.inputImages.add(image);
            imageWatermarkSizeMap.put(image, (int) sizeSlider.getValue());
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
        if (watermarkText.getText().isEmpty()){
            this.watermarkException.showAlert("There is no text you want to watermark your photo", Alert.AlertType.ERROR);
        }else if(watermarkText.getText()!= null && comboFileType.getValue()!= null){
            FileChooser fileChooser = new FileChooser();
            String selectedFiletype = comboFileType.getValue().toLowerCase();

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(selectedFiletype.toUpperCase() + " files", "*." + selectedFiletype)
            );

            File selectedFile = fileChooser.showSaveDialog(new Stage());
            // Remove the double extension if it exists
            String fileName = selectedFile.getName();
            fileName = removeDoubleExtension(fileName, selectedFiletype);
            // Now, fileName should have only one extension
            File newFile = new File(selectedFile.getParent(), fileName);
            Thread applyWatermarkThread = new Thread(() -> {
                savefiles(bufferedImages, newFile, selectedFiletype);
            });

            applyWatermarkThread.setDaemon(true); // Set the thread as a daemon to exit when the application exits
            applyWatermarkThread.start();
        }

        }



    @FXML
    private void savefiles(ArrayList<Image> bufferedImages, File selectedFile,
                           String selectedFiletype) {

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try{

            if( bufferedImages.size() == 1) {
                save_as_individual(bufferedImages,selectedFile,selectedFiletype);

            } else if (bufferedImages.size()> 1) {
                save_as_zip(bufferedImages,selectedFile,selectedFiletype);
            }
            Platform.runLater(() -> {
                watermarkException.showAlert("You have saved your files successfully!", Alert.AlertType.INFORMATION);
                OnCancelWaterMark();
            });


        } catch (Exception  e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();

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
        this.sizeSlider.setValue(700);
        this.rotationSlider.setValue(0);
    }


}
