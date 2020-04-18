package view_controller;

import database.DatabaseConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.LocalDateTime_Interface;
import model.User;


public class ReportsController implements Initializable {

    
    //Define parts of the screen
   
    @FXML private ChoiceBox reportChoiceBox;
    @FXML private TextArea textAreaForReports;
    @FXML private Button resetButton;
    @FXML private Button generateReportButton;
    
    
    //Define OB Lists for reports
    static ObservableList<String> reports = FXCollections.observableArrayList();

    
    LocalDateTime_Interface convert = (String dateTime) -> { //Lambda used to convert the UTC datetime from the database to the user's localdatetime
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime ldt =  LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            return ldt;
    };
    

    
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
    @FXML private void handleGenerateReportButton (ActionEvent event) throws IOException, SQLException {
        textAreaForReports.clear();
        reportChoiceBox.setDisable(true);
        generateReportButton.setDisable(true);
        resetButton.setDisable(false);
        
        String chosenReport = reportChoiceBox.getValue().toString();
        
        switch (chosenReport) {
            case "Appointment Types by Month":
                textAreaForReports.setText(reportApptTypesByMonth());
                break;
            case "Schedule for Consultant":
                textAreaForReports.setText(reportConsultantSchedule());
                break;
            case "Appointments per Month":
                textAreaForReports.setText(reportApptsPerMonth());
                break;
            default:
                break;
        }                     
    }
    
    //reset everything on Reports screen
    @FXML private void handleResetButton (ActionEvent event) throws IOException {
        textAreaForReports.clear();
        resetButton.setDisable(true);
        reportChoiceBox.setDisable(false);
        reportChoiceBox.setItems(getReports());
        generateReportButton.setDisable(false);
        
    }
    
    
    
    //Fills combo box for report types
    public static ObservableList<String> getReports() {
        reports.removeAll(reports); //prevents types from copying themselves to the list
        reports.add("Appointment Types by Month");
        reports.add("Schedule for Consultant");
        reports.add("Appointments per Month");
        return reports;
    }
    
    
    
    //Generates report: number of appointment types by month 
    public String reportApptTypesByMonth() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            
            //Header, also a container to be appended to
            StringBuilder report1text = new StringBuilder();
            report1text.append("Month    | # of each Type  \n______________________________________________________________________\n");
            
            //Get Month, Type, and Amount
            ResultSet rs = conn.createStatement().executeQuery(String.format("SELECT MONTHNAME(start) as Month, type, COUNT(*) as Amount\n" +
                                                                                        "FROM appointment GROUP BY MONTH(start), type;"));
            while (rs.next()) {
                report1text.append(rs.getString("Month") + "          " + rs.getString("Amount") + "   " + rs.getString("type") + "\n");
            }

            return report1text.toString();
            
        } catch (SQLException e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work"; 
        }
        
    }
    
    
    
    //Generates report: the schedule for consultant 
    public String reportConsultantSchedule() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            
            //Header, also a container for where all text will be appended
            StringBuilder report2text = new StringBuilder();
            report2text.append("Consultant " + User.getCurrentUsername() + "'s Schedule\n_______________________________________________________________________________________________\n" +
                    "Date                Start       End           Appointment Info\n_______________________________________________________________________________________________\n");
            
            //SQL statement to get the schedule
            ResultSet getSchedule = conn.createStatement().executeQuery(String.format("SELECT DATE(start) date, start, end, customerName, title, description, type, location "
                                                                                    + "FROM appointment a INNER JOIN customer c ON a.customerId=c.customerId WHERE userId='%s' ORDER BY start;", User.getCurrentUserid()));
            
            //Transform the data by extracting only the numerical month MM (like 01)
            while (getSchedule.next()) {
                LocalDateTime zonedStart = convert.stringToLocalDateTime(getSchedule.getString("start"));
                LocalDateTime zonedEnd = convert.stringToLocalDateTime(getSchedule.getString("end"));              
                
                String date = getSchedule.getString("date");
                String zonedStartString = zonedStart.toString().substring(11,16);
                String zonedEndString = zonedEnd.toString().substring(11,16);
                String name = getSchedule.getString("customerName");
                String title = getSchedule.getString("title");
                String description = getSchedule.getString("description");
                String type = getSchedule.getString("type");
                String location = getSchedule.getString("location");
                
                report2text.append(date + "\t" + zonedStartString + "\t" + zonedEndString + "\t" + name + "     " + title + "     " + description + "     " +  type + "     " +  location + "\n\n");   
            }
            
           
            return report2text.toString();
 
        } catch (SQLException e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work";
        }
    }
    
    //Generates report: number of appointments per month (one additional report of your choice)
    public String reportApptsPerMonth() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            
            //Header, also a container for where all text will be appended
            StringBuilder report3text = new StringBuilder();
            report3text.append("Month - Number of Appointments\n___________________________________\n");
            
            //SQL statement to get the start date/time
            ResultSet getApptsPerMonth = conn.createStatement().executeQuery(String.format("SELECT MONTHNAME(start) Month, YEAR(start) Year, COUNT(*) Amount "
                                                                                         + "FROM appointment GROUP BY Month;"));
            
            //Transform the data by extracting only the numerical month MM (like 01)
            while (getApptsPerMonth.next()) {
                String month = getApptsPerMonth.getString("Month");
                String year = getApptsPerMonth.getString("Year");
                String amount = getApptsPerMonth.getString("Amount");
                
                report3text.append(month + ", " + year + " - " + amount + "\n");   
            }

            return report3text.toString(); 
            
        } catch (SQLException e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work";
        }
    }        
    
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetButton.setDisable(true);
        reportChoiceBox.setItems(getReports());
    }       
}
