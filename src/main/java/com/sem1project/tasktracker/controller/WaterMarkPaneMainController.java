package com.sem1project.tasktracker.controller;

import com.sem1project.tasktracker.Launcher;
import com.sem1project.tasktracker.controller.draw.drawWaterMarkPane;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.awt.*;
import java.awt.Button;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class WaterMarkPaneMainController {
    @FXML private ImageView ImgPreview;
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
    private int currentImageIndex = -1;
    private static int watermarkYPosition = 0;
    private static int watermarkXPosition = 0;





    public void initialize(){

        combotypeAddition();

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
        comboFileType.getItems().removeAll();
        comboFileType.getItems().addAll( "JPG","PNG", "JPEG");
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
    public void leftupCornerClicked() {
        System.out.println("leftUp was clicked!");
        watermarkYPosition -=200;
        watermarkXPosition -= 300;
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



    public void OnCancelWaterMark(){
        inputImages.clear();
       Launcher.getStage().close();

    }




// ...

    @FXML
    private void OnApplyWaterMark() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        fileChooser.setTitle("Save Image");

        // Show the file save dialog and get the selected file
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            // Ensure the selected file has the appropriate extension
            String selectedExtension = comboFileType.getSelectionModel().getSelectedItem();
            if (!file.getName().toLowerCase().endsWith("." + selectedExtension.toLowerCase())) {
                file = new File(file.getName(),  "." + selectedExtension.toLowerCase());
            }

            // Now you can save your image to the selected file using FileOutputStream
            try {
                Image imageToSave = ImgPreview.getImage(); // Get the image from ImgPreview
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageToSave, null);
                ImageIO.write(bufferedImage, selectedExtension.toLowerCase(), file);

                // Optionally, you can show a success message to the user
                System.out.println("File saved successfully: " + file.getAbsolutePath());
            } catch (IOException e) {
                // Handle any IOException that may occur
                e.printStackTrace();
                // Optionally, show an error message to the user
                System.err.println("Error saving the file.");
            }
        }
    }



    public void OnDefaultValue() {
        this.visibilitySlider.setValue(65);
        this.sizeSlider.setValue(100);
        this.rotationSlider.setValue(0);
    }


//    private void saveToDirectory(List<BufferedImage> resizedImages) throws FileNotFoundException {
//        String fileType = comboFile.getValue();
//        FileChooser fileChooser=new FileChooser();
//        fileChooser.setTitle("Save Resized Images");
//        Stage stage=(Stage) anchorId.getScene().getWindow();
//        File selectedDirectory=fileChooser.showSaveDialog(stage);
//
//        if(selectedDirectory!=null) {
//            try {
//                if (resizedImages.size() == 1) {
//                    BufferedImage image = resizedImages.get(0);
//                    String fileName = selectedDirectory.getName()+"."+fileType;
//                    File outputFile = new File(selectedDirectory.getParent(),fileName);
//                    ImageIO.write(image, fileType, outputFile);
//                } else if (resizedImages.size() > 1) {
//                    String zipFileName = selectedDirectory.getAbsolutePath()+".zip";
//                    try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(zipFileName)))) {
//                        int i = 1;
//                        for (BufferedImage image : resizedImages) {
//                            String entryName = selectedDirectory.getName() + i + "." + fileType;
//                            ZipEntry entry = new ZipEntry(entryName);
//                            zipOutputStream.putNextEntry(entry);
//                            ImageIO.write(image, fileType, zipOutputStream);
//                            zipOutputStream.closeEntry();
//                            i++;
//                        }
//                    }
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    }


}
