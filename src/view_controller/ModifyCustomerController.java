package view_controller;

import database.DatabaseConnection;
import database.Query;
import static database.Query.deleteCustomer;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static model.Alerts.blankFieldError;
import static model.Alerts.customerDeleted;
import static model.Alerts.customerEdited;
import static model.Alerts.nothingSearched;
import static model.Alerts.nothingSelectedtoDelete;
import static model.Alerts.nothingSelectedtoEdit;
import model.Customer;


public class ModifyCustomerController implements Initializable {

    
    //Define parts of the screen (non-TableView)
    
    @FXML private TextField searchField;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField address1Field;
    @FXML private TextField address2Field;
    @FXML private TextField zipField;
    @FXML private TextField phoneField;     
    @FXML private ComboBox cityComboBox;     
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button editButton;
    @FXML private Button searchButton;
    @FXML private Button cancelButton;

    
    //Define TableView parts
    
    @FXML private TableView<Customer> customersTableView;
    @FXML private TableColumn<Customer, Integer> custIdCol;
    @FXML private TableColumn<Customer, String> custNameCol;
    @FXML private TableColumn<Customer, String> custAddress1Col;
    @FXML private TableColumn<Customer, String> custAddress2Col;
    @FXML private TableColumn<Customer, String> custCityCol;
    @FXML private TableColumn<Customer, String> custZipCol;
    @FXML private TableColumn<Customer, String> custCountryCol;
    @FXML private TableColumn<Customer, String> custPhoneCol;
    
    //Observable List for TableView
    
    ObservableList<Customer> customersTable = FXCollections.observableArrayList();
    

    //Define actions for pressed buttons
    
    //go back to Main Screen
    @FXML private void handleReturnButton (ActionEvent event) throws IOException {
        //clear everything
        nameField.clear();
        address1Field.clear();
        address2Field.clear();
        zipField.clear();
        phoneField.clear();
        
        System.out.println("Modify Customer -> Main Menu");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    //Search for a customer by name (or phone number?)
        //Search function
    public Customer lookupCustomerName(String searchName) {
        for (Customer cust : customersTable) {
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
            customersTableView.getSelectionModel().select(lookupCustomerName(searchString));
        }
        else {
            nothingSearched();
        }
    }
    
    //populate textfields with customer information available for editing
    @FXML private void handleEditButton (ActionEvent event) throws IOException {

        //Create customer object with selected row
        Customer selectedCustomer = customersTableView.getSelectionModel().getSelectedItem();
        
        //Check to make sure user selected a customer
        try {
            
            //Populate fields with data
            idField.setText(selectedCustomer.getId());
            nameField.setText(selectedCustomer.getName());
            address1Field.setText(selectedCustomer.getAddress());
            address2Field.setText(selectedCustomer.getAddress2());
            cityComboBox.setValue(selectedCustomer.getCity());
            zipField.setText(selectedCustomer.getZip());
            phoneField.setText(selectedCustomer.getPhone()); 
            
            //Let textfields become editable, and Save button clickable
            nameField.setDisable(false);
            address1Field.setDisable(false);
            address2Field.setDisable(false);
            zipField.setDisable(false);
            cityComboBox.setDisable(false);
            phoneField.setDisable(false);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
            
            //Disable the TableView, Edit, and Delete buttons
            customersTableView.setDisable(true);
            editButton.setDisable(true);
            deleteButton.setDisable(true);
            searchField.setDisable(true);
            searchButton.setDisable(true);
        }
        catch (Exception e) {
            String s = "a customer";
            nothingSelectedtoEdit(s);
        }
        
    }
    
    //Delete a customer from the database
    @FXML private void handleDeleteButton (ActionEvent event) throws IOException {
        
        //Select the customer to delete
        Customer customerToDelete = customersTableView.getSelectionModel().getSelectedItem();
        
        //Make sure a customer was selected
        try {
            String id = customerToDelete.getId();
            String name = customerToDelete.getName();

            //Warn the user
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Delete Customer?");
            alert.setHeaderText("Please Confirm...");
            alert.setContentText("Are you sure you want to delete " + name + "?");
            Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) { 
                    //User selected OK to delete
                    deleteCustomer(id);

                    //Refreshes screen, shows updated table (post-delete)
                    System.out.println("Delete successful! Refresh page.");
                    Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyCustomer.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                    
                    //Lets user know the deletion was successful
                    customerDeleted(name);
                } 
                else {}

        } catch (Exception e) {
            String s = "a customer";
            nothingSelectedtoDelete(s);
        }
    }
    
    //Commit changes to a customer to the database
    @FXML private void handleCancelButton (ActionEvent event) throws IOException {
        
        //Clear anything that was entered
        idField.clear();
        nameField.clear();
        address1Field.clear();
        address2Field.clear();
        zipField.clear();
        phoneField.clear();
        cityComboBox.setItems(Query.getCities());

        //Disable the textfields and bottom buttons
        idField.setDisable(true);
        nameField.setDisable(true);
        address1Field.setDisable(true);
        address2Field.setDisable(true);
        zipField.setDisable(true);
        phoneField.setDisable(true);
        cityComboBox.setDisable(true);
        cancelButton.setDisable(true);
        saveButton.setDisable(true);
        
        //Enable the TableView again and Search
        customersTableView.setDisable(false);
        editButton.setDisable(false);
        deleteButton.setDisable(false);
        searchField.setDisable(false);
        searchButton.setDisable(false);
    }
     
    //Commit changes to a customer to the database
    @FXML private void handleSaveButton (ActionEvent event) throws IOException {
        
        try {

            //Gather user-entered data from textfields
            String id = idField.getText();
            String editName = nameField.getText();
            String editAddress1 = address1Field.getText();
            String editAddress2 = address2Field.getText();
            String editZip = zipField.getText();
            String editPhone = phoneField.getText();
            String editCity = (String) cityComboBox.getValue().toString();

            //Adds new customer to the database
            if (!editName.isEmpty() && !editAddress1.isEmpty() && !editCity.isEmpty() && !editZip.isEmpty() && !editPhone.isEmpty()) {

                //Executes adding customer query
                Query.editCustomer(id, editName, editAddress1, editAddress2, editCity, editZip, editPhone);

                //Refreshes screen, shows the new data in the table
                System.out.println("Edit successful! Refresh page.");
                Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyCustomer.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                //Pop-up confirming that a new 
                customerEdited(editName);

                //Enable TableView, Edit, and Delete buttons, disable Save button
                saveButton.setDisable(true);
                customersTableView.setDisable(false);
                editButton.setDisable(false);
                deleteButton.setDisable(false);

                //disable textfield edits until Edit button is clicked again or Delete
                nameField.setDisable(true);
                address1Field.setDisable(true);
                address2Field.setDisable(true);
                zipField.setDisable(true);
                cityComboBox.setDisable(true);
                phoneField.setDisable(true);
                saveButton.setDisable(true);
                cancelButton.setDisable(true);
            }
            else {
                blankFieldError("Address Line 2", "modify", "a customer");
            }
        } catch (Exception e) {
            blankFieldError("Address Line 2", "modify", "a customer");
        }
    }
    
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Set customer textfields to disabled until Edit button is clicked when a customer is selected
        idField.setDisable(true); //Stays disabled forever
        nameField.setDisable(true);
        address1Field.setDisable(true);
        address2Field.setDisable(true);
        zipField.setDisable(true);
        cityComboBox.setItems(Query.getCities()); //Sets choices for city
        cityComboBox.setDisable(true);
        phoneField.setDisable(true);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);

        //Populate Customers table using the database
        Connection con;
        try {
            con = DatabaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT customerId, customerName, address, address2, city, postalCode, country, phone\n" +
                                                          "FROM customer c INNER JOIN address a ON c.addressId = a.addressId\n" +
                                                          "INNER JOIN city i ON a.cityId = i.cityId\n" +
                                                          "INNER JOIN country o ON i.countryId = o.countryId;");
            while (rs.next()) {
                customersTable.add(new Customer(rs.getString("customerId"), 
                                        rs.getString("customerName"), 
                                        rs.getString("address"), 
                                        rs.getString("address2"),
                                        rs.getString("city"), 
                                        rs.getString("postalCode"), 
                                        rs.getString("country"), 
                                        rs.getString("phone")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModifyCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

            custIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            custAddress1Col.setCellValueFactory(new PropertyValueFactory<>("address"));
            custAddress2Col.setCellValueFactory(new PropertyValueFactory<>("address2"));
            custCityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
            custZipCol.setCellValueFactory(new PropertyValueFactory<>("zip"));
            custCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
            custPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

            customersTableView.setItems(customersTable);  
    }           
}    
    
