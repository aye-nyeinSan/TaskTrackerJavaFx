module com.sem1project.tasktracker {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.sem1project.tasktracker to javafx.fxml;
    opens com.sem1project.tasktracker.controller.draw to javafx.fxml;
    exports com.sem1project.tasktracker;
    exports com.sem1project.tasktracker.controller.draw;
}