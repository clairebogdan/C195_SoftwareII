package view_controller;

import database.DatabaseConnection;
import static database.Query.appointmentInFifteen;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import static model.Alerts.noResults;
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
    private boolean isMonthly;
    
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
    
    
    //A new date was picked
    @FXML private void handleDatePicked (ActionEvent event) throws IOException {
        if (isWeekly) {  
            viewByWeek(); //View by Week is previously selected
        }
        else if (isMonthly) { 
            viewByMonth(); //View by Month is previously selected
        }
        else { 
            viewAll(); //View All is previously selected
        }
    }
    
    
    
    public void viewByMonth() {
        isWeekly = false;
        isMonthly = true;
        
        LocalDate datePicked = datePicker.getValue();
        String monthPicked = datePicked.toString().substring(5,7);
        String yearPicked = datePicked.toString().substring(0,4);
        
        System.out.println("month number: " + monthPicked + " year: " + yearPicked);
        
        Connection con;
        try {
            appointmentSchedule.clear();
            con = DatabaseConnection.getConnection();
            ResultSet getApptsByMonth = con.createStatement().executeQuery(String.format("SELECT appointmentId, customerName, title, description, location, type, DATE(start) date, start, end " +
                                                                          "FROM customer c INNER JOIN appointment a ON c.customerId = a.customerId " +
                                                                          "WHERE MONTH(start) = '%s' AND YEAR(start) = '%s' ORDER BY start", monthPicked, yearPicked));
            while (getApptsByMonth.next()) {
                appointmentSchedule.add(new Appointment(getApptsByMonth.getString("appointmentId"), 
                                                        getApptsByMonth.getString("customerName"), 
                                                        getApptsByMonth.getString("title"), 
                                                        getApptsByMonth.getString("description"),
                                                        getApptsByMonth.getString("location"),   
                                                        getApptsByMonth.getString("type"),
                                                        getApptsByMonth.getString("date"),
                                                        getApptsByMonth.getString("start").substring(11,16),
                                                        getApptsByMonth.getString("end").substring(11,16)));
            }
            mainCalendarTableView.setItems(appointmentSchedule);
            
            if (appointmentSchedule.isEmpty()) noResults("month and year");
            
        } catch (SQLException e) {
            System.out.println("Something wrong with SQL: " + e.getMessage());
        }
    }
    
    
    
    public void viewByWeek() {
        isWeekly = true;
        isMonthly = false;
        
        LocalDate datePicked = datePicker.getValue();
        String yearPicked = datePicker.getValue().toString().substring(0,4);
        WeekFields weekFields = WeekFields.of(Locale.US);
        int weekNumber = datePicked.get(weekFields.weekOfWeekBasedYear());
        String weekString = Integer.toString(weekNumber);
        
        System.out.println("week number: " + weekString + " year: " + yearPicked);
        
        Connection con;
        try {
            appointmentSchedule.clear();
            con = DatabaseConnection.getConnection();
            ResultSet getApptsByMonth = con.createStatement().executeQuery(String.format("SELECT appointmentId, customerName, title, description, location, type, DATE(start) date, start, end " +
                                                                          "FROM customer c INNER JOIN appointment a ON c.customerId = a.customerId " +
                                                                          "WHERE WEEK(DATE(start))+1 = '%s' AND YEAR(start) = '%s' ORDER BY start", weekString, yearPicked));
            while (getApptsByMonth.next()) {
                appointmentSchedule.add(new Appointment(getApptsByMonth.getString("appointmentId"), 
                                                        getApptsByMonth.getString("customerName"), 
                                                        getApptsByMonth.getString("title"), 
                                                        getApptsByMonth.getString("description"),
                                                        getApptsByMonth.getString("location"),   
                                                        getApptsByMonth.getString("type"),
                                                        getApptsByMonth.getString("date"),
                                                        getApptsByMonth.getString("start").substring(11,16),
                                                        getApptsByMonth.getString("end").substring(11,16)));
            }
            mainCalendarTableView.setItems(appointmentSchedule);
            
            if (appointmentSchedule.isEmpty()) noResults("week and year");
            
        } catch (SQLException e) {
            System.out.println("Something wrong with SQL: " + e.getMessage());
        }
    }
    
    
    
    public void viewAll() {
        isWeekly = false;
        isMonthly = false;
        
        Connection con;
        try {
            appointmentSchedule.clear();
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
            mainCalendarTableView.setItems(appointmentSchedule);
            
        } catch (SQLException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    //Weekly Radio Button - changes the calendar label and calendar data
    @FXML private void handleWeeklyRadio (ActionEvent event) throws IOException { viewByWeek(); }
    
    //Monthly Radio Button - changes the calendar label and calender data
    @FXML private void handleMonthlyRadio (ActionEvent event) throws IOException { viewByMonth(); }
    
    //View All Radio Button - changes the calendar label and calendar data
    @FXML private void handleAllRadio (ActionEvent event) throws IOException { viewAll(); }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //When screen is loaded, the View All Calendar is automatically selected
        calendarRadio = new ToggleGroup();
        this.weeklyRadio.setToggleGroup(calendarRadio);
        this.monthlyRadio.setToggleGroup(calendarRadio);
        this.allRadio.setToggleGroup(calendarRadio);
        this.allRadio.setSelected(true);
        datePicker.setValue(LocalDate.now());
        isWeekly = false;
        isMonthly = false;
        viewAll();
        
        //Alerts the user if they logged in within 15 minutes of a scheduled appointment
        if (appointmentInFifteen()) {
            System.out.println("User alerted");
        }
        else {
            System.out.println("User not alerted of any appointment soon.");
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
    }    
}
