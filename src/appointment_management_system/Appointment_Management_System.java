package appointment_management_system;

import database.DatabaseConnection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


//Claire Bogdan
//WGU C195 (Software II) Performance Assessment


public class Appointment_Management_System extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view_controller/LoginScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        launch(args);
        DatabaseConnection.closeConnection();
    }
    
}
