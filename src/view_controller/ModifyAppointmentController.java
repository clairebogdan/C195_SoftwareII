package view_controller;

import database.DatabaseConnection;
import database.Query;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import static model.Alerts.nothingSearched;
import static model.Alerts.nothingSelectedtoEdit;
import model.Appointment;



public class ModifyAppointmentController implements Initializable {


    //Define parts of the screen
    
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField contactField;
    @FXML private ComboBox typeComboBox;
    @FXML private TextField urlField;
    @FXML private TextField searchField;     
    @FXML private TextField locationField;  
    @FXML private ComboBox startTimeComboBox;
    @FXML private ComboBox endTimeComboBox; 
    @FXML private DatePicker datePicker;
    @FXML private Button searchButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;
    @FXML private Button returnButton;
    
    //Define TableView parts
    
    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableColumn<Appointment, String> apptIdCol;
    @FXML private TableColumn<Appointment, String> custCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> descCol;
    @FXML private TableColumn<Appointment, String> locCol;
    @FXML private TableColumn<Appointment, String> contactCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> urlCol;
    @FXML private TableColumn<Appointment, String> startCol;
    @FXML private TableColumn<Appointment, String> endCol;
    
    //Observable List for TableView
    
    ObservableList<Appointment> appointmentTable = FXCollections.observableArrayList();
    
    

    //Define actions for pressed buttons
    
    //Go back to Main Screen
    @FXML private void handleReturnButton (ActionEvent event) throws IOException {
        System.out.println("Add Appointment -> Main Menu");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //Search Appointment table by type
        //Search function
    public Appointment lookupType(String searchName) {
            for (Appointment a : appointmentTable) {
                if (a.getType().equalsIgnoreCase(searchName)) {
                    return a;
                }
            }
            return null;
        }
        //Search button
    @FXML private void handleSearchButton (ActionEvent event) throws IOException {
        String searchString = searchField.getText();
        if (!searchString.isEmpty()) {
        appointmentTableView.getSelectionModel().select(lookupType(searchString));
        }
        else {
            nothingSearched();
        }
    }
    
    //delete an appointment from the database
    @FXML private void handleDeleteButton (ActionEvent event) throws IOException {

    }
    
    //select appointment from the TableView and load appointment data to fields
    @FXML private void handleEditButton (ActionEvent event) throws IOException {
        
        //Create appointment object with selected row
        Appointment selectedAppointment = (Appointment) appointmentTableView.getSelectionModel().getSelectedItem();
        
        try {
            
            //Populate fields with data
            idField.setText(selectedAppointment.getId());
            nameField.setText(selectedAppointment.getName());
            titleField.setText(selectedAppointment.getTitle());
            descriptionField.setText(selectedAppointment.getDescription());
            locationField.setText(selectedAppointment.getLocation());
            contactField.setText(selectedAppointment.getContact());
            typeComboBox.setValue(selectedAppointment.getType());
            urlField.setText(selectedAppointment.getUrl());
            //datePicker.setValue(selectedAppointment.getDate());
            startTimeComboBox.setValue(selectedAppointment.getStart()); 
            endTimeComboBox.setValue(selectedAppointment.getEnd());
            
            
            //Set fields to editable
            titleField.setDisable(false);
            descriptionField.setDisable(false);
            contactField.setDisable(false);
            urlField.setDisable(false);
            typeComboBox.setDisable(false);
            locationField.setDisable(false);
            startTimeComboBox.setDisable(false);
            datePicker.setDisable(false);
            endTimeComboBox.setDisable(false);
            saveButton.setDisable(false);
            
            //Disable the TableView, Edit, and Delete buttons
            appointmentTableView.setDisable(true);
            editButton.setDisable(true);
            deleteButton.setDisable(true);
            searchField.setDisable(true);
            searchButton.setDisable(true);
        }
        catch (Exception e) {
            String s = "an appointment";
            nothingSelectedtoEdit(s);
        }
    }
    
    
    //add new appointment to the database, clear fields once this is done
    @FXML private void handleSaveButton (ActionEvent event) throws IOException {
        
        //set textfields to disabled once the Add Button is clicked
        nameField.setDisable(true);
        titleField.setDisable(true);
        descriptionField.setDisable(true);
        contactField.setDisable(true);
        urlField.setDisable(true);
        typeComboBox.setDisable(true);
        locationField.setDisable(true);
        datePicker.setDisable(true);
        startTimeComboBox.setDisable(true);
        endTimeComboBox.setDisable(true);
        saveButton.setDisable(true);
        
        //display added message

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //set textfields to disabled until Select Edit button is clicked when an appointment is selected
        idField.setDisable(true); //ALWAYS
        nameField.setDisable(true); //ALWAYS
        titleField.setDisable(true);
        descriptionField.setDisable(true);
        contactField.setDisable(true);
        typeComboBox.setDisable(true);
        typeComboBox.setItems(Query.getTypes());
        urlField.setDisable(true);
        locationField.setDisable(true);
        datePicker.setDisable(true);
        startTimeComboBox.setDisable(true);
        startTimeComboBox.setItems(Query.getTimes());
        endTimeComboBox.setDisable(true);
        endTimeComboBox.setItems(Query.getTimes());
        saveButton.setDisable(true);
        
        
        //Populate Appointment table using the database
        Connection con;
        try {
            con = DatabaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT appointmentId, customerName, title, description, location, contact, type, url, start, end\n" +
                                                          "FROM customer c INNER JOIN appointment a ON c.customerId = a.customerId;");
           
            while (rs.next()) {
                appointmentTable.add(new Appointment(rs.getString("appointmentId"), 
                                                     rs.getString("customerName"), 
                                                     rs.getString("title"), 
                                                     rs.getString("description"),
                                                     rs.getString("location"), 
                                                     rs.getString("contact"), 
                                                     rs.getString("type"), 
                                                     rs.getString("url"),
                                                     rs.getString("start"),
                                                     rs.getString("end")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModifyAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            custCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            locCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
            startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
            endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
            
            appointmentTableView.setItems(appointmentTable); 
            
    }    
    
}
