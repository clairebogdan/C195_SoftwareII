package database;

import static database.DatabaseConnection.conn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Query {
    
    private static String query;
    private static Statement stmt;
    private static ResultSet result;
    static ObservableList<String> cities = FXCollections.observableArrayList();
    static ObservableList<String> types = FXCollections.observableArrayList();
    static ObservableList<String> customersTable = FXCollections.observableArrayList();
    static ObservableList<String> times = FXCollections.observableArrayList();
    
    
//////////////////////////LOGIN SCREEN FUNCTIONS////////////////////////////////    
    
    //checks username and password for logging in
    public static boolean login(String usernameInput, String passwordInput) {
            try{
                DatabaseConnection.makeConnection();
                PreparedStatement pst = conn.prepareStatement("SELECT * FROM user WHERE userName=? AND password=?");
                pst.setString(1, usernameInput);
                pst.setString(2, passwordInput);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    return true;
                }
                else {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                return false;
            }
        }
   
    
    
//////////////////////////MAIN SCREEN CALENDAR FUNCTIONS////////////////////////

    //weekly calendar view
    
    //monthly calendar view
    
    
    
    
//////////////////////////ADD & MODIFY CUSTOMER FUNCTIONS///////////////////////
    
    //Fills the city combo boxes
    public static ObservableList<String> getCities() {
        try {
            cities.removeAll(cities); //prevents cities from copying themselves to the list
            ResultSet cityList = conn.createStatement().executeQuery("SELECT city FROM city;");
            while (cityList.next()) {
                cities.add(cityList.getString("city"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return cities;
    }
    

    
    //Add new Customer to the database
    public static void addCustomer(String name, String address1, String address2, String zip, String city, String phone) {
        try {
            //1. Get the city ID, which is needed for inserting into the address table
            ResultSet getCityId = conn.createStatement().executeQuery(String.format("SELECT cityId FROM city WHERE city = '%s'", city));
            getCityId.next();
            
            //2. Insert part of the data into the address table
            conn.createStatement().executeUpdate(String.format("INSERT INTO address "
                    + "(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
                    address1, address2, getCityId.getString("cityId"), zip, phone, LocalDateTime.now(), "test", LocalDateTime.now(), "test"));
            
            //3. Get the address ID, which is needed for inserting into the customer table. It was generated in step 2.
            ResultSet getAddressId = conn.createStatement().executeQuery(String.format("SELECT addressId FROM address WHERE address='%s' AND address2='%s' AND cityId='%s' AND postalCode='%s'",
                                    address1, address2, getCityId.getString("cityId"), zip));
            getAddressId.next();
            
            //3. Insert the rest of the data into the customer table
            conn.createStatement().executeUpdate(String.format("INSERT INTO customer "
                    + "(customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                    "VALUES ('%s', '%s', 1, '%s', '%s', '%s', '%s')", 
                    name, getAddressId.getString("addressId"), LocalDateTime.now(), "test", LocalDateTime.now(), "test"));    
        } catch (Exception e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }
    
    
    
    //Delete existing Customer from the database
    public static void deleteCustomer(String id){
        try {
            //1. Delete from customer table
            conn.createStatement().executeUpdate(String.format("DELETE FROM customer"
                    + " WHERE customerId='%s'", id));
            
            //2. Delete from address table
            conn.createStatement().executeUpdate(String.format("DELETE FROM address"
                    + " WHERE addressId='%s'", id));
            
            
        } catch (Exception e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }
    }

    
    
    //Update existing Customer in the database
    public static void editCustomer(String id, String name, String address1, String address2, String city, String zip, String phone) {
        try {
            //1. Update customer table
            conn.createStatement().executeUpdate(String.format("UPDATE customer"
                    + " SET customerName='%s', lastUpdate='%s', lastUpdateBy='%s'" 
                    + " WHERE customerId='%s'",
                    name, LocalDateTime.now(), "test", id));
            
            //2. Get city ID from the entered string 'city'
            ResultSet getCityId = conn.createStatement().executeQuery(String.format("SELECT cityId FROM city WHERE city = '%s'", city));
            getCityId.next();
            
            //3. Update address table
            conn.createStatement().executeUpdate(String.format("UPDATE address"
                    + " SET address='%s', address2='%s', cityId='%s', postalCode='%s', phone='%s', lastUpdate='%s', lastUpdateBy='%s'" 
                    + " WHERE addressId='%s'",
                    address1, address2, getCityId.getString("cityId"), zip, phone, LocalDateTime.now(), "test", id));
            
        } catch (Exception e) {
            System.out.println("Error editing customer: " + e.getMessage());
        }
    }
    
    
    
    
    
    
//////////////////////////ADD & MODIFY CUSTOMER FUNCTIONS///////////////////////
    
    //Fills the type combo boxes
    public static ObservableList<String> getTypes() {
        try {
            ResultSet typeList = conn.createStatement().executeQuery("SELECT type FROM appointment;");
            while (typeList.next()) {
                types.add(typeList.getString("type"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return types;
    }
    
          
    //Fills in the start/end time combo boxes
    public static ObservableList<String> getTimes() {
        try {         
            for (int i = 9; i < 17; i++ ) {
                String hour;
                    if(i < 10) {                   
                        hour = "0" + i;
                    }
                    else {
                        hour = Integer.toString(i);
                    }
                    times.add(hour + ":00:00");
                    times.add(hour + ":15:00");
                    times.add(hour + ":30:00");
                    times.add(hour + ":45:00");
                }
                
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return times;
        }
    
    
    //Add new Appointment to the database
    public static void addAppointment(String id, String name, String title, String description, String location, String contact, String type, String url, String date, String startTime, String endTime) {
        try {
            
            //1. Change startTime  (00:00:00) , endTime (00:00:00), and date (YYYY-MM-DD) to "YYYY-MM-DD 00:00:00"
            String dateAndStartTime, dateAndEndTime;
            dateAndStartTime = date + " " + startTime;
            dateAndEndTime = date + " " + endTime;
            
            //2. Get the user ID, which is needed for inserting into the appointment table
            ResultSet getUserId = conn.createStatement().executeQuery(String.format("SELECT userId FROM user WHERE userName = '%s'", "test"));
            getUserId.next();
            
            
            //***FIXME*** replace "test" with a user function or class.
            
            //3. Insert  data into the appointment table
            conn.createStatement().executeUpdate(String.format("INSERT INTO appointment "
                    + "(customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
                    id, getUserId.getString("userId"), title, description, location, contact, type, url, dateAndStartTime, dateAndEndTime, LocalDateTime.now(), "test", LocalDateTime.now(), "test"));  
        } catch (Exception e) {
            System.out.println("Error adding appointment: " + e.getMessage());
        }
    }
    
    //Delete existing Appointment from the database
    
    //Update existing Appointment in the database
    
    //Refresh the database and TableView Appointments

       
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
    
    
    
    
    
    
    
    
    
    
    

