package view_controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class ReportsController implements Initializable {

    
    //Define parts of the screen
   
    @FXML private ChoiceBox reportChoiceBox;
    @FXML private TextArea textAreaForReports;
    @FXML private Label reportTextAreaLabel;
    @FXML private Button returnButton;
    @FXML private Button resetButton;
    @FXML private Button generateReportButton;

    


    //Define actions for pressed buttons
    
    //go back to Main Screen
    @FXML private void handleReturnButton (ActionEvent event) throws IOException {
        System.out.println("Reports -> Main Menu");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //generate report in the text area
    @FXML private void handleGenerateReportButton (ActionEvent event) throws IOException {
        
    }
    
    //reset everything on Reports screen
    @FXML private void handleResetButton (ActionEvent event) throws IOException {
        
    }
    
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}