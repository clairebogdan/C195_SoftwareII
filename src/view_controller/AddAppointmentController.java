package view_controller;

import database.DatabaseConnection;
import database.Query;
import static database.Query.checkForOverlap;
import static database.Query.insideBusinessHours;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import static model.Alerts.appointmentAdded;
import static model.Alerts.appointmentTimeIssue;
import static model.Alerts.blankFieldError;
import static model.Alerts.nothingSearched;
import static model.Alerts.nothingSelectedtoEdit;
import model.Appointment;
import model.Customer;
import model.LocalDateTime_Interface;


public class AddAppointmentController implements Initializable {
    
    
    //Define parts of the screen
    
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;
    @FXML private TextField contactField;
    @FXML private ComboBox typeComboBox;
    @FXML private TextField urlField;
    @FXML private TextField searchField;       
    @FXML private ComboBox startTimeComboBox;
    @FXML private ComboBox endTimeComboBox; 
    @FXML private DatePicker datePicker;
    @FXML private Button searchButton;
    @FXML private Button selectCustomerButton;
    @FXML private Button cancelButton;
    @FXML private Button addButton;
    @FXML private Button returnButton;
    
    //Define Customer TableView parts
    
    @FXML private TableView<Customer> custNamesTableView;
    @FXML private TableColumn<Customer, Integer> custIdCol;
    @FXML private TableColumn<Customer, String> custNameCol;
    
    //Observable List for Customer TableView
    
    ObservableList<Customer> idAndNamesTable = FXCollections.observableArrayList();
    
    //Define Appointment TableView parts
    
    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableColumn<Appointment, String> apptIdCol;
    @FXML private TableColumn<Appointment, String> nameCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, String> locationCol;
    @FXML private TableColumn<Appointment, String> contactCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> urlCol;
    @FXML private TableColumn<Appointment, String> dateCol;
    @FXML private TableColumn<Appointment, String> startCol;
    @FXML private TableColumn<Appointment, String> endCol;
    
    //Observable List for TableView
    
    ObservableList<Appointment> currApptTable = FXCollections.observableArrayList();
    
    
    
    
    //Define actions for pressed buttons
    
    //go back to Main Screen
    @FXML private void handleReturnButton (ActionEvent event) throws IOException {
        System.out.println("Add Appointment -> Main Menu");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //Search for a customer by name (or phone number?)
        //Search function
    public Customer lookupCustomerName2(String searchName) {
            for (Customer cust : idAndNamesTable) {
                if (cust.getName().equalsIgnoreCase(searchName)) {
                    return cust;
                }
            }
            return null;
        }
    
        //Search button
    @FXML private void handleSearchButton (ActionEvent event) throws IOException {
        String searchString = searchField.getText();
        if (!searchString.isEmpty()) {
        custNamesTableView.getSelectionModel().select(lookupCustomerName2(searchString));
        }
        else {
            nothingSearched();
        }
    }
    
    //Select customer from the TableView and load customer name to the textfield
    @FXML private void handleSelectCustomerButton (ActionEvent event) throws IOException {
        
        //Create customer object for which to add appointment
        Customer addApptCustomer = custNamesTableView.getSelectionModel().getSelectedItem();
        
        //Check to make sure user selected someone
        try {
            
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
            cancelButton.setDisable(false);
            addButton.setDisable(false);
            
            //Populate id and name fields
            idField.setText(addApptCustomer.getId());
            nameField.setText(addApptCustomer.getName());

            //Disable the TableView, Edit, and Delete buttons
            custNamesTableView.setDisable(true);
            selectCustomerButton.setDisable(true);
            searchField.setDisable(true);
            searchButton.setDisable(true);
        }
        catch (Exception e) {
            String s = "an appointment";
            nothingSelectedtoEdit(s);
            
            //Keep fields disabled
            idField.setDisable(true); //ALWAYS
            nameField.setDisable(true); //ALWAYS
            titleField.setDisable(true);
            descriptionField.setDisable(true);
            contactField.setDisable(true);
            urlField.setDisable(true);
            locationField.setDisable(true);
            typeComboBox.setDisable(true);
            datePicker.setDisable(true);
            startTimeComboBox.setDisable(true);
            endTimeComboBox.setDisable(true);
            cancelButton.setDisable(true);
            addButton.setDisable(true);
        }
    }
    
    //Clear all editable fields, reset back to original
    @FXML private void handleCancelButton (ActionEvent event) throws IOException {
        //Clear anything that was entered
        idField.clear();
        nameField.clear();
        titleField.clear();
        descriptionField.clear();
        locationField.clear();
        contactField.clear();
        urlField.clear();
        datePicker.setValue(null);
        typeComboBox.getSelectionModel().clearSelection();
        startTimeComboBox.getSelectionModel().clearSelection();
        endTimeComboBox.getSelectionModel().clearSelection();
        
        //Disable the right side of the screen
        idField.setDisable(true);
        nameField.setDisable(true);
        nameField.setText("Select a customer first");
        titleField.setDisable(true);
        descriptionField.setDisable(true);
        contactField.setDisable(true);
        typeComboBox.setDisable(true);
        urlField.setDisable(true);
        locationField.setDisable(true);
        datePicker.setDisable(true);
        startTimeComboBox.setDisable(true);
        endTimeComboBox.setDisable(true);
        cancelButton.setDisable(true);
        addButton.setDisable(true);
        
        //Enable the TableView again and Search
        custNamesTableView.setDisable(false);
        selectCustomerButton.setDisable(false);
        searchField.setDisable(false);
        searchButton.setDisable(false);

    }
    
    //add new appointment to the database, clear fields once this is done
    @FXML private void handleAddButton (ActionEvent event) throws IOException, NullPointerException {
        
        try {
            //Gather user-entered data from textfields
            String addId = idField.getText();
            String addName = nameField.getText();
            String addTitle = titleField.getText();
            String addDescription = descriptionField.getText();
            String addLocation = locationField.getText();
            String addType = typeComboBox.getValue().toString();
            String addContact = contactField.getText();
            String addURL = urlField.getText();
            String addDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int checkAddTime = startTimeComboBox.getSelectionModel().getSelectedIndex();
            int checkEndTime = endTimeComboBox.getSelectionModel().getSelectedIndex();
            String addStartTime = (String) startTimeComboBox.getValue().toString();
            String addEndTime = (String) endTimeComboBox.getValue().toString();
            

            try {
                if (addTitle.isEmpty() || addDescription.isEmpty() || addLocation.isEmpty() || addType.isEmpty() || addContact.isEmpty() || addDate.isEmpty() || addStartTime.isEmpty() || addEndTime.isEmpty()) {
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
                
                        //Executes adding appointment query
                        if (checkForOverlap(addStartTime, addEndTime, addDate) && insideBusinessHours(addStartTime, addEndTime, addDate)) {
                            try {
                                Query.addAppointment(addId, addName, addTitle, addDescription, addLocation, addContact, addType, addURL, addDate, addStartTime, addEndTime);
                            
                                //Refreshes screen, shows the new data in the table
                                System.out.println("Add successful! Refresh page.");
                                Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/AddAppointment.fxml"));
                                Scene scene = new Scene(parent);
                                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();

                                //Pop-up confirming that a new 
                                appointmentAdded(addName);

                                //Enable TableView, Edit, and Delete buttons, disable Save button
                                custNamesTableView.setDisable(false);
                                searchButton.setDisable(false);
                                searchField.setDisable(false);
                                selectCustomerButton.setDisable(false);

                                //set textfields to disabled once the Add Button is clicked
                                titleField.setDisable(true);
                                descriptionField.setDisable(true);
                                contactField.setDisable(true);
                                urlField.setDisable(true);
                                locationField.setDisable(true);
                                typeComboBox.setDisable(true);
                                datePicker.setDisable(true);
                                startTimeComboBox.setDisable(true);
                                endTimeComboBox.setDisable(true);
                                cancelButton.setDisable(true);
                                addButton.setDisable(true); 
                                
                            } catch (SQLException e) {
                                System.out.println("ERROR WITH SQL: " + e.getMessage());
                            }
                        }      
                        else {
                            if (!checkForOverlap(addStartTime, addEndTime, addDate)) { appointmentTimeIssue("Appointment could not be scheduled due to overlap with another existing appiontment."); }
                            if (!insideBusinessHours(addStartTime, addEndTime, addDate)) { appointmentTimeIssue("Appointment could not be scheduled because the start and/or end times are outside of business hours.\n"
                                    + "Business hours are 9:00 - 17:00 UTC time."); }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } 
        } catch (NullPointerException e) {
            System.out.println("Something was left blank.");
            blankFieldError("URL", "add", "an appointment");
        }
    }
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        LocalDateTime_Interface convert = (String dateTime) -> { //Lambda used to convert the UTC datetime from the database to the user's localdatetime
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime ldt =  LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            return ldt;
        };
        
        //set textfields to disabled until Select Customer button is clicked when a customer is selected
        idField.setDisable(true); //ALWAYS
        nameField.setDisable(true); //ALWAYS
        nameField.setText("Select a customer first");
        titleField.setDisable(true);
        descriptionField.setDisable(true);
        contactField.setDisable(true);
        urlField.setDisable(true);
        locationField.setDisable(true);
        typeComboBox.setDisable(true);
        typeComboBox.setItems(Query.getTypes());
        
        datePicker.setDisable(true);
        datePicker.getEditor().setEditable(false);
        
        datePicker.setDayCellFactory(picker -> new DateCell() { //Lambda used to disable past dates and weekends from being selected
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
        cancelButton.setDisable(true);
        addButton.setDisable(true);
        
        //Populate Customers table using the database
        Connection con;
        try {
            con = DatabaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT customerId, customerName FROM customer ORDER BY customerId;");
            while (rs.next()) {
                idAndNamesTable.add(new Customer(rs.getString("customerId"), rs.getString("customerName")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

            custIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            custNamesTableView.setItems(idAndNamesTable);
            
            
            
        //Populate Appointment table using the database
        try {
            con = DatabaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT appointmentId, customerName, title, description, location, contact, type, url, DATE(start) date, start, end\n" +
                                                          "FROM customer c INNER JOIN appointment a ON c.customerId = a.customerId ORDER BY start;");
           
            while (rs.next()) {
                //Convert UTC timestamp from database to user's local datetime
                LocalDateTime zonedStart = convert.stringToLocalDateTime(rs.getString("start"));
                LocalDateTime zonedEnd = convert.stringToLocalDateTime(rs.getString("end"));
                String zonedStartString = zonedStart.toString().substring(11,16);
                String zonedEndString = zonedEnd.toString().substring(11,16);

                //Add data to the visible table
                currApptTable.add(new Appointment(rs.getString("appointmentId"), 
                                                     rs.getString("customerName"), 
                                                     rs.getString("title"), 
                                                     rs.getString("description"),
                                                     rs.getString("location"), 
                                                     rs.getString("contact"), 
                                                     rs.getString("type"), 
                                                     rs.getString("url"),
                                                     rs.getString("date"),
                                                     zonedStartString,
                                                     zonedEndString));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModifyAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
            endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
            
            appointmentTableView.setItems(currApptTable);
    }    
}
