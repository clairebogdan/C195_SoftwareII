package view_controller;

import database.DatabaseConnection;
import database.Query;
import static database.Query.checkForOverlap;
import static database.Query.deleteAppointment;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static model.Alerts.appointmentDeleted;
import static model.Alerts.appointmentEdited;
import static model.Alerts.appointmentTimeIssue;
import static model.Alerts.blankFieldError;
import static model.Alerts.nothingSearched;
import static model.Alerts.nothingSelectedtoDelete;
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
    @FXML private Button cancelButton;
    
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
    public Appointment lookupId(String searchId) {
            for (Appointment a : appointmentTable) {
                if (a.getId().equals(searchId)) {
                    return a;
                }
            }
            return null;
        }
        //Search button
    @FXML private void handleSearchButton (ActionEvent event) throws IOException {
        String searchString = searchField.getText();
        if (!searchString.isEmpty()) {
            appointmentTableView.getSelectionModel().select(lookupId(searchString));
        }
        else {
            nothingSearched();
        }
    }
    
    //delete an appointment from the database
    @FXML private void handleDeleteButton (ActionEvent event) throws IOException {
        
        //Select the appointment to delete
        Appointment appointmentToDelete = appointmentTableView.getSelectionModel().getSelectedItem();
        
        //Make sure a appointment was selected
        try {
            String deleteApptId = appointmentToDelete.getId();
            String custName = appointmentToDelete.getName();

            //Warn the user
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Delete Appointment?");
            alert.setHeaderText("Please Confirm...");
            alert.setContentText("Are you sure you want to delete appointment ID #" + deleteApptId + " for customer " + custName + "?");
            Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) { 
                    //User selected OK to delete
                    deleteAppointment(deleteApptId);

                    //Refreshes screen, shows updated table (post-delete)
                    System.out.println("Delete Successful! Refresh page.");
                    Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyAppointment.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                    
                    //Lets user know the deletion was successful
                    appointmentDeleted(custName);
                } 
                else {}

        } catch (Exception e) {
            String s = "an appointment";
            nothingSelectedtoDelete(s);
        }
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
            startTimeComboBox.setValue(selectedAppointment.getStart().substring(11,16) + ":00");
            endTimeComboBox.setValue(selectedAppointment.getEnd().substring(11,16) + ":00");
            
            //Populate date picker
            String dateString = selectedAppointment.getStart().substring(0,10);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDateObj = LocalDate.parse(dateString, dateTimeFormatter);
            datePicker.setValue(localDateObj);

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
            cancelButton.setDisable(false);
            
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
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    //Commit changes to a customer to the database
    @FXML private void handleCancelButton (ActionEvent event) throws IOException {
        
        //Clear anything that was entered
        idField.clear();
        nameField.clear();
        titleField.clear();
        descriptionField.clear();
        contactField.clear();
        urlField.clear();
        typeComboBox.setItems(Query.getTypes());
        locationField.clear();
        datePicker.setValue(null);
        startTimeComboBox.setItems(Query.getTimes());
        endTimeComboBox.setItems(Query.getTimes());

        //Disable the textfields and bottom buttons
        idField.setDisable(true);
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
        cancelButton.setDisable(true);
        
        //Enable the TableView again and Search
        appointmentTableView.setDisable(false);
        editButton.setDisable(false);
        deleteButton.setDisable(false);
        searchField.setDisable(false);
        searchButton.setDisable(false);
    }
    
    
    //add new appointment to the database, clear fields once this is done
    @FXML private void handleSaveButton (ActionEvent event) throws IOException {
        
        try {
            //Gather user-entered data from textfields
            String apptId = idField.getText();
            String custName = nameField.getText();
            String editTitle = titleField.getText();
            String editDescription = descriptionField.getText();
            String editContact = contactField.getText();
            String editUrl = urlField.getText();
            String editType = typeComboBox.getValue().toString();
            String editLocation = locationField.getText();
            String editDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int checkAddTime = startTimeComboBox.getSelectionModel().getSelectedIndex();
            int checkEndTime = endTimeComboBox.getSelectionModel().getSelectedIndex();
            String editStartTime = startTimeComboBox.getValue().toString();
            String editEndTime = endTimeComboBox.getValue().toString();
            
            System.out.println("Start Time Index: " + checkAddTime + " End Time Index: " + checkEndTime);

            if (editTitle.isEmpty() || editDescription.isEmpty() || editLocation.isEmpty() || editType.isEmpty() || editContact.isEmpty() || editDate.isEmpty() || editStartTime.isEmpty() || editEndTime.isEmpty()) {
                blankFieldError("URL", "add", "an appointment");              
            }
            
            else {
                
                if (checkEndTime == checkAddTime) {          
                    appointmentTimeIssue("Appointment start and end times cannot be the same.");
                }
                
                else if (checkEndTime < checkAddTime){
                    appointmentTimeIssue("Appointment start time must be earlier than the end time.");
                }
                
                else {
                                 
                    //Adds new appointment to the database
                    if (checkForOverlap(editStartTime, editEndTime, editDate)) {
                        Query.editAppointment(apptId, custName, editTitle, editDescription, editContact, editUrl, editType, editLocation, editDate, editStartTime, editEndTime);

                        //Refreshes screen, shows the new data in the table
                        System.out.println("Edit successful! Refresh page.");
                        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyAppointment.fxml"));
                        Scene scene = new Scene(parent);
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();

                        //Pop-up confirming that a new 
                        appointmentEdited(apptId, custName);

                        //set textfields to disabled once the Save Button is clicked
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
                        cancelButton.setDisable(true);
                    }
                    else {
                        blankFieldError("URL", "modify", "an appointment");
                    }
                }
            }            
        } catch (Exception e) {
            blankFieldError("URL", "modify", "an appointment");
        }
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
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        
        
        datePicker.setDisable(true);
        datePicker.getEditor().setEditable(false);
        
        //Disables past dates and weekends from being selected
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
                if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                    setDisable(true);
            }
        });
        startTimeComboBox.setDisable(true);
        startTimeComboBox.setItems(Query.getTimes());
        endTimeComboBox.setDisable(true);
        endTimeComboBox.setItems(Query.getTimes());
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        
        
        //Populate Appointment table using the database
        Connection con;
        try {
            con = DatabaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT appointmentId, customerName, title, description, location, contact, type, url, start, end\n" +
                                                          "FROM customer c INNER JOIN appointment a ON c.customerId = a.customerId ORDER BY start;");
           
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
