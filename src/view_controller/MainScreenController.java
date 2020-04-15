package view_controller;

import database.DatabaseConnection;
import static database.Query.appointmentInFifteen;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.User;


public class MainScreenController implements Initializable {

    //Define parts of the screen
      
    @FXML private RadioButton weeklyRadio;
    @FXML private RadioButton monthlyRadio;
    @FXML private RadioButton allRadio;
    @FXML private DatePicker datePicker;
    private ToggleGroup calendarRadio;
    private boolean isWeekly;
    
    @FXML private TableView<Appointment> mainCalendarTableView;
    @FXML private TableColumn<Appointment, String> apptIdCol;
    @FXML private TableColumn<Appointment, String> custCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, String> locationCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> dateCol;
    @FXML private TableColumn<Appointment, String> startTimeCol;
    @FXML private TableColumn<Appointment, String> endTimeCol;
    
    
    ObservableList<Appointment> appointmentSchedule = FXCollections.observableArrayList();
    
    
    
    
    
    
    //Define actions for pressed buttons
    
    //go back to Login Screen
    @FXML private void handleLogoutButton (ActionEvent event) throws IOException {
        User.setCurrentUserid(null);
        User.setCurrentUsername(null);
        
        System.out.println("LOGGED OUT!");
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
        
    }
    
    //Monthly Radio Button - changes the calendar label and calender data
    @FXML private void handleMonthlyRadio (ActionEvent event) throws IOException {
        isWeekly = false;
        
    }
    
    //View All Radio Button - changes the calendar label and calendar data
    @FXML private void handleAllRadio (ActionEvent event) throws IOException { 
        isWeekly = false;
        
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //When screen is loaded, the Weekly Calendar is automatically selected
        calendarRadio = new ToggleGroup();
        this.weeklyRadio.setToggleGroup(calendarRadio);
        this.monthlyRadio.setToggleGroup(calendarRadio);
        this.allRadio.setToggleGroup(calendarRadio);
        this.weeklyRadio.setSelected(true);
        datePicker.setValue(LocalDate.now());
        isWeekly = true;
        
        //Alerts the user if they logged in within 15 minutes of a scheduled appointment
        if (appointmentInFifteen()) {
            System.out.println("User alerted");
        }
        else {
            System.out.println("User not alerted of any appointment soon.");
        }
        
        //Populate Appointment Schedule table
        Connection con;
        try {
            con = DatabaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT appointmentId, customerName, title, description, location, type, DATE(start) date, start, end\n" +
                                                          "FROM customer c INNER JOIN appointment a ON c.customerId = a.customerId ORDER BY start;");
            while (rs.next()) {
                appointmentSchedule.add(new Appointment(rs.getString("appointmentId"), 
                                                        rs.getString("customerName"), 
                                                        rs.getString("title"), 
                                                        rs.getString("description"),
                                                        rs.getString("location"),   
                                                        rs.getString("type"),
                                                        rs.getString("date"),
                                                        rs.getString("start").substring(11,16),
                                                        rs.getString("end").substring(11,16)));
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModifyAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            custCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            startTimeCol.setCellValueFactory(new PropertyValueFactory<>("start"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<>("end"));
            
            mainCalendarTableView.setItems(appointmentSchedule);
    }    
    
}
