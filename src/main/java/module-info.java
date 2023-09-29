module com.sem1project.tasktracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.commons.imaging;
    requires javafx.swing;
    requires org.apache.commons.compress;


    opens com.sem1project.tasktracker to javafx.fxml;
    opens com.sem1project.tasktracker.controller to javafx.fxml;
    exports com.sem1project.tasktracker;
    exports com.sem1project.tasktracker.controller;
}