package database;

import static database.DatabaseConnection.conn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static model.Alerts.appointmentSoon;
import model.User;

public class Query {
    
    private static String query;
    private static Statement stmt;
    private static ResultSet result;
    static ObservableList<String> cities = FXCollections.observableArrayList();
    static ObservableList<String> types = FXCollections.observableArrayList();
    static ObservableList<String> customersTable = FXCollections.observableArrayList();
    static ObservableList<String> times = FXCollections.observableArrayList();
    
    
    
    
    
//////////////////////////LOGIN SCREEN FUNCTIONS////////////////////////////////    
    
    //Checks username and password for logging in
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
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Error: " + e.getMessage());
                return false;
            }
        }


    //Appointment in 15 minutes
    public static boolean appointmentInFifteen() {
        try {
            ResultSet earliestAppt = conn.createStatement().executeQuery(String.format("SELECT customerName "
                    + "FROM customer c INNER JOIN appointment a ON c.customerId=a.customerId INNER JOIN user u ON a.userId=u.userId "
                    + "WHERE a.userId='%s' AND a.start BETWEEN '%s' AND '%s'",
                    User.getCurrentUserid(), LocalDateTime.now(ZoneId.of("UTC")), LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(15)));
            earliestAppt.next();
            
            String name = earliestAppt.getString("customerName");
            appointmentSoon(name);
            
            return true;
        } catch (SQLException e) {
            System.out.println("You don't have an appointment soon.");
            return false;
        }
    }
    

    
    
    
//////////////////////////ADD & MODIFY CUSTOMER FUNCTIONS///////////////////////
    
    //Fills the city combo boxes
    public static ObservableList<String> getCities() {
        try {
            cities.removeAll(cities); //prevents cities from copying themselves to the list
            ResultSet cityList = conn.createStatement().executeQuery("SELECT city FROM city;");
            while (cityList.next()) {
                cities.add(cityList.getString("city"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return cities;
    }
    

    
    //Add new Customer to the database (using "throws" exception, which is handled in the AddCustomerController)
    public static void addCustomer(String name, String address1, String address2, String zip, String city, String phone) throws SQLException {
            
            //1. Get the city ID, which is needed for inserting into the address table
            ResultSet getCityId = conn.createStatement().executeQuery(String.format("SELECT cityId FROM city WHERE city = '%s'", city));
            getCityId.next();
            
            //2. Insert part of the data into the address table
            conn.createStatement().executeUpdate(String.format("INSERT INTO address "
                    + "(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
                    address1, address2, getCityId.getString("cityId"), zip, phone, LocalDateTime.now(), User.getCurrentUsername(), LocalDateTime.now(), User.getCurrentUsername()));
            
            //3. Get the address ID, which is needed for inserting into the customer table. It was generated in step 2.
            ResultSet getAddressId = conn.createStatement().executeQuery(String.format("SELECT addressId FROM address WHERE address='%s' AND address2='%s' AND cityId='%s' AND postalCode='%s'",
                                    address1, address2, getCityId.getString("cityId"), zip));
            getAddressId.next();
            
            //3. Insert the rest of the data into the customer table
            conn.createStatement().executeUpdate(String.format("INSERT INTO customer "
                    + "(customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                    "VALUES ('%s', '%s', 1, '%s', '%s', '%s', '%s')", 
                    name, getAddressId.getString("addressId"), LocalDateTime.now(), User.getCurrentUsername(), LocalDateTime.now(), User.getCurrentUsername()));    

    }
    
    
    
    //Delete existing Customer from the database
    public static void deleteCustomer(String id){
        try {
            //1. Get address id
            ResultSet getAddressId = conn.createStatement().executeQuery(String.format("SELECT addressId FROM customer WHERE customerId='%s'", id));
            getAddressId.next();
            
            //2. Delete the customer
            conn.createStatement().executeUpdate(String.format("DELETE FROM customer"
                    + " WHERE customerId='%s'", id));

            //3. Delete the customer's address
            conn.createStatement().executeUpdate(String.format("DELETE FROM address"
                    + " WHERE addressId='%s'", getAddressId.getString("addressId")));
            
            //4. Delete any appointments for the customer
            conn.createStatement().executeUpdate(String.format("DELETE FROM appointment"
                    + " WHERE customerId='%s'", id));
            
        } catch (SQLException e) {
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
                    name, LocalDateTime.now(), User.getCurrentUsername(), id));
            
            //2. Get city ID from the entered string 'city'
            ResultSet getCityId = conn.createStatement().executeQuery(String.format("SELECT cityId FROM city WHERE city = '%s'", city));
            getCityId.next();
            
            //3. Update address table
            conn.createStatement().executeUpdate(String.format("UPDATE address"
                    + " SET address='%s', address2='%s', cityId='%s', postalCode='%s', phone='%s', lastUpdate='%s', lastUpdateBy='%s'" 
                    + " WHERE addressId='%s'",
                    address1, address2, getCityId.getString("cityId"), zip, phone, LocalDateTime.now(), User.getCurrentUsername(), id));
            
        } catch (Exception e) {
            System.out.println("Error editing customer: " + e.getMessage());
        }
    }

    
    
///////////////////////ADD & MODIFY APPOINTMENT FUNCTIONS///////////////////////
    
    //Fills the type combo boxes
    public static ObservableList<String> getTypes() {
        types.removeAll(types); //prevents types from copying themselves to the list
        types.add("Beginner");
        types.add("Intermediate");
        types.add("Advanced");
        return types;
    }
    
    
          
    //Fills in the start/end time combo boxes
    public static ObservableList<String> getTimes() {
        try {     
            times.removeAll(times); //prevents cities from copying themselves to the list
            for (int i = 0; i < 24; i++ ) {
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
            times.add("24:00:00"); //add midnight to the list
                
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return times;
        }
    
    
    
    //Converts user's localdatetime to UTC 
    public static LocalDateTime stringToLDT_UTC(String time, String date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt =  LocalDateTime.parse(date + " " + time, format).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        return ldt;
    }
    
    
    
    //Checks to make sure the start and end times the user selected are within business hours (9:00-17:00 UTD)
    public static boolean insideBusinessHours(String startTime, String endTime, String date) {
        
        //convert start and end times selected to UTC equivalents
        LocalDateTime localStart = stringToLDT_UTC(startTime, date);
        LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
        String UTCstart = localStart.toString().substring(11,16);
        String UTCend = localEnd.toString().substring(11,16);
       
        //Compare by using LocalTime datatypes
        LocalTime enteredStart = LocalTime.parse(UTCstart);
        LocalTime enteredEnd = LocalTime.parse(UTCend);
        LocalTime openingHour = LocalTime.parse("08:59");
        LocalTime closingHour = LocalTime.parse("16:59");
        Boolean startTimeAllowed = enteredStart.isAfter(openingHour);
        Boolean endTimeAllowed = enteredEnd.isBefore(closingHour);
        
        if (startTimeAllowed && endTimeAllowed) {
            return true;
        }
        else {
            return false;
        }
        
    }

    
    
    //Check for overlapping Appointments
    public static boolean checkForOverlap(String startTime, String endTime, String date) {
            try {
                
                //1. Change startTime  (00:00:00) , endTime (00:00:00), and date (YYYY-MM-DD) to "YYYY-MM-DD 00:00:00"
                LocalDateTime localStart = stringToLDT_UTC(startTime, date);
                LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
                String UTCstart = localStart.toString();
                String UTCend = localEnd.toString();

                //3. Look for overlap
                ResultSet getOverlap = conn.createStatement().executeQuery(String.format(
                           "SELECT start, end, customerName FROM appointment a INNER JOIN customer c ON a.customerId=c.customerId " +
                           "WHERE ('%s' >= start AND '%s' <= end) " +
                           "OR ('%s' <= start AND '%s' >= end) " +
                           "OR ('%s' <= start AND '%s' >= start) " +
                           "OR ('%s' <= end AND '%s' >= end)",
                           UTCstart, UTCstart, UTCend, UTCend, UTCstart, UTCend, UTCstart, UTCend));
                getOverlap.next();
                System.out.println("APPOINTMENT OVERLAP: " + getOverlap.getString("customerName"));
                return false;
            } catch (SQLException e) {
                System.out.println("There are no appointment conflicts.");
                return true;
            }
    }
    
    
    
    //Check for overlapping Appointments, but allows for saving the same time as before
    public static boolean checkYourself(String startTime, String endTime, String date, String name, String apptId) {
            try {
                
                //1. Change startTime  (00:00:00) , endTime (00:00:00), and date (YYYY-MM-DD) to "YYYY-MM-DD 00:00:00"
                LocalDateTime localStart = stringToLDT_UTC(startTime, date);
                LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
                String UTCstart = localStart.toString();
                String UTCend = localEnd.toString();

                //3. Look for overlap
                ResultSet getOverlap = conn.createStatement().executeQuery(String.format(
                           "SELECT start, end, customerName, appointmentId FROM appointment a INNER JOIN customer c ON a.customerId=c.customerId " +
                           "WHERE ('%s' >= start AND '%s' <= end) " +
                           "OR ('%s' <= start AND '%s' >= end) " +
                           "OR ('%s' <= start AND '%s' >= start) " +
                           "OR ('%s' <= end AND '%s' >= end)",
                           UTCstart, UTCstart, UTCend, UTCend, UTCstart, UTCend, UTCstart, UTCend));
                getOverlap.next();
                String checkStart = getOverlap.getString("start").substring(0,16);
                String checkUTCstart = UTCstart.replace('T', ' ');
                String checkEnd = getOverlap.getString("end").substring(0,16);
                String checkUTCend = UTCend.replace('T', ' ');
                    if (getOverlap.getString("customerName").equals(name) && getOverlap.getString("appointmentId").equals(apptId) && checkStart.equals(checkUTCstart) || checkEnd.equals(checkUTCend)) {
                            System.out.println("A time wasn't changed. Save over self: " + getOverlap.getString("customerName"));
                            return true;
                    }
                    else {
                        System.out.println("Went to else");
                        System.out.println(getOverlap.getString("customerName") + " " + name + " " + getOverlap.getString("appointmentId") + " " + apptId + " "  + checkStart + " " + checkUTCstart + " " + checkEnd + " " + checkUTCend);
                        return false;
                    }
                
            } catch (SQLException e) {
                //SQL returned a null set, meaning the appointment can be made here
                return true;
        }
            
    }
    
    
    
    //Adding an Appointment to the database
    public static void addAppointment(String id, String name, String title, String description, String location, String contact, String type, String url, String date, String startTime, String endTime) throws SQLException {
            
            //1. Change startTime  (00:00:00) , endTime (00:00:00), and date (YYYY-MM-DD) to "YYYY-MM-DD 00:00:00"
            LocalDateTime localStart = stringToLDT_UTC(startTime, date);
            LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
            String UTCstart = localStart.toString();
            String UTCend = localEnd.toString();
            
            System.out.println("Converted date and start time (UTC): " + UTCstart);

            //3. Insert  data into the appointment table
            conn.createStatement().executeUpdate(String.format("INSERT INTO appointment "
                    + "(customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
                    id, User.getCurrentUserid(), title, description, location, contact, type, url, UTCstart, UTCend, LocalDateTime.now(), User.getCurrentUsername(), LocalDateTime.now(), User.getCurrentUsername()));  
    }
    
    
    
    //Delete existing Appointment from the database
    public static void deleteAppointment(String id){
        try {
            conn.createStatement().executeUpdate(String.format("DELETE FROM appointment WHERE appointmentId='%s'", id));
  
        } catch (Exception e) {
            System.out.println("Error deleting appointment: " + e.getMessage());
        }
    }
    
    
    
    //Update existing Appointment in the database
    public static void editAppointment(String apptId, String custName, String title, String description, String contact, String url, String type, String location, String date, String startTime, String endTime) {
        try {
            //1. Change startTime  (00:00:00) , endTime (00:00:00), and date (YYYY-MM-DD) to "YYYY-MM-DD 00:00:00"
            LocalDateTime localStart = stringToLDT_UTC(startTime, date);
            LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
            String UTCstart = localStart.toString();
            String UTCend = localEnd.toString();
            
            System.out.println("Converted date and start time (UTC): " + UTCstart);

            
            //3. Get customer ID, which is needed for inserting into the appointment table
            ResultSet getCustomerId = conn.createStatement().executeQuery(String.format("SELECT customerId FROM appointment WHERE appointmentId = '%s'", apptId));
            getCustomerId.next();

            //4. Insert  data into the appointment table
            conn.createStatement().executeUpdate(String.format("UPDATE appointment "
                    + "SET customerId='%s', userId='%s', title='%s', description='%s', location='%s', contact='%s', type='%s', url='%s', start='%s', end='%s', lastUpdate='%s', lastUpdateBy='%s' " +
                    "WHERE appointmentId='%s'", 
                    getCustomerId.getString("customerId"), User.getCurrentUserid(), title, description, location, contact, type, url, UTCstart, UTCend, LocalDateTime.now(), User.getCurrentUsername(), apptId));  
        } catch (SQLException e) {
            System.out.println("Error editing appointment: " + e.getMessage());
        }
    }
}