package view_controller;

import database.DatabaseConnection;
import database.Query;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import static model.Alerts.appointmentAdded;
import static model.Alerts.blankFieldError;
import static model.Alerts.nothingSearched;
import static model.Alerts.nothingSelectedtoEdit;
import model.Customer;


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
    
    //Define TableView parts
    
    @FXML private TableView<Customer> custNamesTableView;
    @FXML private TableColumn<Customer, Integer> custIdCol;
    @FXML private TableColumn<Customer, String> custNameCol;
    
    //Observable List for TableView
    
    ObservableList<Customer> idAndNamesTable = FXCollections.observableArrayList();
    
    
    
    
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
    @FXML private void handleAddButton (ActionEvent event) throws IOException {
        
        /*FIXME!
        *Add button when empty is not triggering the blank field errors
        *Catch overlapping appointments
        *Catch out of office hours
        */
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
            String addStartTime = (String) startTimeComboBox.getValue().toString();
            String addEndTime = (String) endTimeComboBox.getValue().toString();

            if (!addTitle.isEmpty() && !addDescription.isEmpty() && !addLocation.isEmpty() && !addType.isEmpty() && !addContact.isEmpty() && !addDate.isEmpty() && !addStartTime.isEmpty() && !addEndTime.isEmpty()) {

                //Executes adding customer query
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
            }
            else {
                blankFieldError("URL", "add", "an appointment");
            }
        } catch (Exception e) {
            blankFieldError("URL", "add", "an appointment");
        }
    }
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
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
        datePicker.setPromptText("YYYY-MM-DD");
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
            ResultSet rs = con.createStatement().executeQuery("SELECT customerId, customerName FROM customer;");
            while (rs.next()) {
                idAndNamesTable.add(new Customer(rs.getString("customerId"), rs.getString("customerName")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

            custIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            custNamesTableView.setItems(idAndNamesTable);
    }    
}
