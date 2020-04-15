package model;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

public class Alerts {
 
    public static void blankFieldError(String s1, String s2, String s3) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("Error");
        alert.setHeaderText("Blank Field");
        alert.setContentText("All fields (except " + s1 + ") MUST be filled to " + s2 + " " + s3 + ".");  
        alert.showAndWait();
    }
    
    public static void blankFieldMessage(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("Error");
        alert.setHeaderText("Blank Field");
        alert.setContentText(s + " field was left blank.");  
        alert.showAndWait();
    }
    
    public static void fieldEntryError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("Error");
        alert.setHeaderText("Error adding data");
        alert.setContentText(message);  
        alert.showAndWait();
    }
    
    public static void showException(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("Error");
        alert.setHeaderText("ERROR");
        alert.setContentText(message);  
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
   
   public static void appointmentEdited(String s1, String s2) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Appointment Information Saved");
        alert.setHeaderText("Appointment Information Saved!");
        alert.setContentText("Changes made to appointment ID #" + s1 + " for customer " + s2 + " have been saved.");  
        alert.showAndWait();
    }
   
   public static void appointmentDeleted(String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Deletion Successful");
        alert.setHeaderText("Deletion Successful!");
        alert.setContentText("Appointment for customer " + s + " was successfully deleted from the database.");  
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
   
   public static void appointmentSoon(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Appointment Soon");
        alert.setHeaderText("Appointment Soon!");
        alert.setContentText("You have an appointment with " + s + " within the next 15 minutes!");  
        alert.showAndWait();
   }
   
   public static void appointmentTimeIssue(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("Appointment Time Error");
        alert.setHeaderText("Appointment Time Error!");
        alert.setContentText(message);  
        alert.showAndWait();
   }
   
   public static void appointmentOverlap() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("Appointment Overlap");
        alert.setHeaderText("Appointment Overlap Error!");
        alert.setContentText("CANNOT SAVE: Appointment start and/or end time(s) overlap with an existing appointment on your selected date.");  
        alert.showAndWait();
   }
   
   public static void noResults(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.NONE);
        alert.setTitle("No results");
        alert.setHeaderText("No results");
        alert.setContentText("There are no appointments for the " + s + " you selected.");  
        alert.showAndWait();
    }
   
   
   
}
