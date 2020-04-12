package model;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

public class Alerts {
 
    public static void blankFieldError(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("Error");
        alert.setHeaderText("Blank Field");
        alert.setContentText("The " + s + " field cannot be left blank");  
        alert.showAndWait();
    }
    
    public static void customerAdded(String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Customer Added");
        alert.setHeaderText("Customer Added!");
        alert.setContentText(s + " was added to Customers.");  
        alert.showAndWait();
    }
    
    public static void customerEdited(String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Customer Information Saved");
        alert.setHeaderText("Customer Information Saved!");
        alert.setContentText("Changes made to " + s + " have been saved.");  
        alert.showAndWait();
    }
    
 
    public static void customerDeleted(String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Deletion Successful");
        alert.setHeaderText("Deletion Successful!");
        alert.setContentText(s + " was successfully deleted from the database.");  
        alert.showAndWait();
    }
    
   public static void appointmentAdded(String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Appointment Added");
        alert.setHeaderText("Appointment Added!");
        alert.setContentText("Appointment was added for customer " + s);  
        alert.showAndWait();
    }
   
   public static void nothingSelectedtoEdit(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("ERROR");
        alert.setHeaderText("Nothing Selected!");
        alert.setContentText("You did not select " + s + " to edit. To edit, select " + s + " from the table, then click the Edit button.");  
        alert.showAndWait();
    }
   
   public static void nothingSelectedtoDelete(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("ERROR");
        alert.setHeaderText("Nothing Selected!");
        alert.setContentText("You did not select " + s + " to delete. To delete, select " + s + " from the table, then click the Delete button.");  
        alert.showAndWait();
    }
   
   public static void nothingSearched() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("ERROR");
        alert.setHeaderText("Nothing Searched!");
        alert.setContentText("You did not type anything into the search box to search.");  
        alert.showAndWait();
    }
   
   
}