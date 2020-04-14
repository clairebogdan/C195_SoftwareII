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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import static model.Alerts.blankFieldError;
import static model.Alerts.customerAdded;
import model.Customer;


public class AddCustomerController implements Initializable {

    //Define parts of the top half of the screen
    
    @FXML private TextField nameField;
    @FXML private TextField address1Field;
    @FXML private TextField address2Field;
    @FXML private TextField zipField;
    @FXML private TextField phoneField;     
    @FXML private ComboBox cityComboBox;     
    
    //Define TableView parts
    
    @FXML private TableView<Customer> customersTableView;
    @FXML private TableColumn<Customer, Integer> custIDCol;
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
    
    @FXML private void handleReturnButton (ActionEvent event) throws IOException {
        //clear everything
        nameField.clear();
        address1Field.clear();
        address2Field.clear();
        zipField.clear();
        phoneField.clear();
        
        //go back to main menu
        System.out.println("Add Customer -> Main Menu");
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML private void handleClearButton (ActionEvent event) throws IOException {
        nameField.clear();
        address1Field.clear();
        address2Field.clear();
        zipField.clear();
        phoneField.clear();
        cityComboBox.setItems(Query.getCities());
    }
    
    @FXML private void handleAddButton (ActionEvent event) throws IOException {
        
        /*FIXME!
        *Add button when empty is not triggering the blank field errors
        */
        
        try{
            //Gather user-entered data from textfields
            String addName = nameField.getText();
            String addAddress1 = address1Field.getText();
            String addAddress2 = address2Field.getText();
            String addZip = zipField.getText();
            String addPhone = phoneField.getText();
            String addCity = (String) cityComboBox.getValue().toString();

            //Adds new customer to the database
            if (!addName.isEmpty() && !addAddress1.isEmpty() && !addCity.isEmpty() && !addZip.isEmpty() && !addPhone.isEmpty()) {

                //Executes adding customer query
                Query.addCustomer(addName, addAddress1, addAddress2, addZip, addCity, addPhone);

                //Refreshes screen, shows the new data in the table
                System.out.println("Add successful! Refresh page.");
                Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/AddCustomer.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                //Pop-up confirming that a new 
                customerAdded(addName);
            }
            else {
                blankFieldError("Address Line 2", "add", "a customer");
            }
        } catch (Exception e) {
            blankFieldError("Address Line 2", "add", "a customer");
        }
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Set choice box cities
        cityComboBox.setItems(Query.getCities());
        
        //Populate Customers table using the database
        Connection con;
        try {
            con = DatabaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT customerId, customerName, address, address2, city, postalCode, country, phone\n" +
                                                          "FROM customer c INNER JOIN address a ON c.addressId = a.addressId\n" +
                                                          "INNER JOIN city i ON a.cityId = i.cityId\n" +
                                                          "INNER JOIN country o ON i.countryId = o.countryId ORDER BY customerId;");
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
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

            custIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
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
