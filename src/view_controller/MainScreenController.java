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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;


public class MainScreenController implements Initializable {

    //Define parts of the screen
    
    
    @FXML private Label mainCalendarLabel;
    @FXML private TableView mainCalendarTableView;    
    @FXML private Button logoutButton;     
    @FXML private Button addCustomerButton;
    @FXML private Button modifyCustomerButton;
    @FXML private Button addAppointmentButton;
    @FXML private Button modifyAppointmentButton;
    @FXML private RadioButton weeklyRadio;
    @FXML private RadioButton monthlyRadio;
    private ToggleGroup calendarRadio;
    private boolean isWeekly;
    
    
    
    
    //Define actions for pressed buttons
    
    //go back to Login Screen
    @FXML private void handleLogoutButton (ActionEvent event) throws IOException {
        System.out.println("Main Menu -> Login Screen");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/LoginScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //change screen to Add Customer
    @FXML private void handleAddCustomer (ActionEvent event) throws IOException {
        System.out.println("Main Menu -> Add Customer");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/AddCustomer.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //change screen to Modify Customer
    @FXML private void handleModifyCustomer (ActionEvent event) throws IOException {
        System.out.println("Main Menu -> Modify Customer");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyCustomer.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //change screen to Add Appointment
    @FXML private void handleAddAppointment (ActionEvent event) throws IOException {
        System.out.println("Main Menu -> Add Appointment");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/AddAppointment.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //change screen to Modify Appointment
    @FXML private void handleModifyAppointment (ActionEvent event) throws IOException {
        System.out.println("Main Menu -> Modify Appointment");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyAppointment.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //change screen to Reports
    @FXML private void handleReportsButton (ActionEvent event) throws IOException {
        System.out.println("Main Menu -> Reports");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/Reports.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //Weekly Radio Button - changes the calendar label and calendar data
    @FXML private void handleWeeklyRadio (ActionEvent event) throws IOException { 
        isWeekly = true;
        mainCalendarLabel.setText("Appointments - Weekly");
    }
    
    //Monthly Radio Button - changes the calendar label and calender data
    @FXML private void handleMonthlyRadio (ActionEvent event) throws IOException {
        isWeekly = false;
        mainCalendarLabel.setText("Appointments - Monthly");
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //when screen is loaded, the Weekly Calendar is automatically selected
        calendarRadio = new ToggleGroup();
        this.weeklyRadio.setToggleGroup(calendarRadio);
        this.monthlyRadio.setToggleGroup(calendarRadio);
        this.weeklyRadio.setSelected(true);
        isWeekly = true;
    }    
    
}
