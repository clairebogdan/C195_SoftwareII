package database;

import static database.DatabaseConnection.conn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
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
    static ObservableList<String> reports = FXCollections.observableArrayList();
    
    
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
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                return false;
            }
        }

    
    
//////////////////////////MAIN SCREEN CALENDAR FUNCTIONS////////////////////////

    //Appointment in 15 minutes
    public static boolean appointmentInFifteen() {
        try {
            ResultSet earliestAppt = conn.createStatement().executeQuery(String.format("SELECT customerName "
                    + "FROM customer c INNER JOIN appointment a ON c.customerId=a.customerId INNER JOIN user u ON a.userId=u.userId "
                    + "WHERE a.userId='%s' AND a.start BETWEEN '%s' AND '%s'",
                    User.getCurrentUserid(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(15)));
            earliestAppt.next();
            
            String name = earliestAppt.getString("customerName");
            appointmentSoon(name);
            
            return true;
        } catch (Exception e) {
            System.out.println("You don't have an appointment soon.");
            return false;
        }
    }
    
    //Weekly calendar view
    
    //Monthly calendar view
    
    
    
    
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

    
    
    //Check for overlapping Appointments
    public static boolean checkForOverlap(String startTime, String endTime, String date) {
            try {
                
                startTime = date + " " + startTime;
                endTime = date + " " + endTime;
                
                ResultSet getOverlap = conn.createStatement().executeQuery(String.format(
                           "SELECT start, end, customerName FROM appointment a INNER JOIN customer c ON a.customerId=c.customerId " +
                           "WHERE ('%s' BETWEEN start AND end) " +
                           "OR ('%s' BETWEEN start AND end) " +
                           "OR (start BETWEEN '%s' AND '%s') " +
                           "OR (end BETWEEN '%s' AND '%s')",
                           startTime, endTime, startTime, endTime, startTime, endTime));
                getOverlap.next();
                System.out.println("APPOINTMENT OVERLAP: " + getOverlap.getString("customerName"));
                return false;
            } catch (Exception e) {
            return true;
        }
    }
    
    
    
    //***FIXME***Check for overlapping Appointments, but allows for saving the same time as before
    public static boolean checkYourself(String startTime, String endTime, String date, String name, String apptId) {
            try {
                
                startTime = date + " " + startTime;
                endTime = date + " " + endTime;
                
                ResultSet getOverlap = conn.createStatement().executeQuery(String.format(
                           "SELECT start, end, customerName, appointmentId FROM appointment a INNER JOIN customer c ON a.customerId=c.customerId " +
                           "WHERE ('%s' BETWEEN start AND end) " +
                           "OR ('%s' BETWEEN start AND end) " +
                           "OR (start BETWEEN '%s' AND '%s') " +
                           "OR (end BETWEEN '%s' AND '%s')",
                           startTime, endTime, startTime, endTime, startTime, endTime));
                getOverlap.next();
                    if (getOverlap.getString("customerName").equals(name) && getOverlap.getString("appointmentId").equals(apptId)) {
                            System.out.println("Save over yourself " + getOverlap.getString("customerName"));
                            return true;
                    }
                    else {
                        return false;
                    }
            } catch (Exception e) {
            return true;
        }
    }
    
    
    
    //Adding an Appointment to the database
    public static void addAppointment(String id, String name, String title, String description, String location, String contact, String type, String url, String date, String startTime, String endTime) {
        try {
            
            //1. Change startTime  (00:00:00) , endTime (00:00:00), and date (YYYY-MM-DD) to "YYYY-MM-DD 00:00:00"
            String dateAndStartTime, dateAndEndTime;
            dateAndStartTime = date + " " + startTime;
            dateAndEndTime = date + " " + endTime;
            

            //2. Insert  data into the appointment table
            conn.createStatement().executeUpdate(String.format("INSERT INTO appointment "
                    + "(customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
                    id, User.getCurrentUserid(), title, description, location, contact, type, url, dateAndStartTime, dateAndEndTime, LocalDateTime.now(), User.getCurrentUsername(), LocalDateTime.now(), User.getCurrentUsername()));  
        } catch (Exception e) {
            System.out.println("Error adding appointment: " + e.getMessage());
        }
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
            String dateAndStartTime, dateAndEndTime;
            dateAndStartTime = date + " " + startTime;
            dateAndEndTime = date + " " + endTime;
            
            
            //2. Get customer ID, which is needed for inserting into the appointment table
            ResultSet getCustomerId = conn.createStatement().executeQuery(String.format("SELECT customerId FROM appointment WHERE appointmentId = '%s'", apptId));
            getCustomerId.next();

            //3. Insert  data into the appointment table
            conn.createStatement().executeUpdate(String.format("UPDATE appointment "
                    + "SET customerId='%s', userId='%s', title='%s', description='%s', location='%s', contact='%s', type='%s', url='%s', start='%s', end='%s', lastUpdate='%s', lastUpdateBy='%s' " +
                    "WHERE appointmentId='%s'", 
                    getCustomerId.getString("customerId"), User.getCurrentUserid(), title, description, location, contact, type, url, dateAndStartTime, dateAndEndTime, LocalDateTime.now(), User.getCurrentUsername(), apptId));  
        } catch (Exception e) {
            System.out.println("Error editing appointment: " + e.getMessage());
        }
    }
    
        
    
/////////////////////////////REPORT SCREEN FUNCTIONS////////////////////////////
    
    //Fills combo box for report types
    public static ObservableList<String> getReports() {
        reports.removeAll(reports); //prevents types from copying themselves to the list
        reports.add("Appointment Types by Month");
        reports.add("Schedule for Consultant");
        reports.add("Appointments per Month");
        return reports;
    }
    
    
    
    //Generates report: number of appointment types by month 
    public static String reportApptTypesByMonth(){
        try {
            
            //Header, also a container to be appended to
            String text = "Month          | Beginner - #     | Intermediate - #     | Advanced - #\n______________________________________________________________________\n";
            
            //Containers
            ObservableList<String> report1 = FXCollections.observableArrayList();
            
            ObservableList<String> JanB = FXCollections.observableArrayList();
            ObservableList<String> JanI = FXCollections.observableArrayList();
            ObservableList<String> JanA = FXCollections.observableArrayList();
            
            ObservableList<String> FebB = FXCollections.observableArrayList();
            ObservableList<String> FebI = FXCollections.observableArrayList();
            ObservableList<String> FebA = FXCollections.observableArrayList();
            
            ObservableList<String> MarB = FXCollections.observableArrayList();
            ObservableList<String> MarI = FXCollections.observableArrayList();
            ObservableList<String> MarA = FXCollections.observableArrayList();
            
            ObservableList<String> AprB = FXCollections.observableArrayList();
            ObservableList<String> AprI = FXCollections.observableArrayList();
            ObservableList<String> AprA = FXCollections.observableArrayList();
            
            ObservableList<String> MayB = FXCollections.observableArrayList();
            ObservableList<String> MayI= FXCollections.observableArrayList();
            ObservableList<String> MayA = FXCollections.observableArrayList();
            
            ObservableList<String> JunB = FXCollections.observableArrayList();
            ObservableList<String> JunI = FXCollections.observableArrayList();
            ObservableList<String> JunA = FXCollections.observableArrayList();
            
            ObservableList<String> JulB = FXCollections.observableArrayList();
            ObservableList<String> JulI = FXCollections.observableArrayList();
            ObservableList<String> JulA = FXCollections.observableArrayList();
            
            ObservableList<String> AugB = FXCollections.observableArrayList();
            ObservableList<String> AugI = FXCollections.observableArrayList();
            ObservableList<String> AugA = FXCollections.observableArrayList();
            
            ObservableList<String> SeptB = FXCollections.observableArrayList();
            ObservableList<String> SeptI = FXCollections.observableArrayList();
            ObservableList<String> SeptA = FXCollections.observableArrayList();
            
            ObservableList<String> OctB = FXCollections.observableArrayList();
            ObservableList<String> OctI = FXCollections.observableArrayList();
            ObservableList<String> OctA = FXCollections.observableArrayList();
            
            ObservableList<String> NovB = FXCollections.observableArrayList();
            ObservableList<String> NovI = FXCollections.observableArrayList();
            ObservableList<String> NovA = FXCollections.observableArrayList();
            
            ObservableList<String> DecB = FXCollections.observableArrayList();
            ObservableList<String> DecI = FXCollections.observableArrayList();
            ObservableList<String> DecA = FXCollections.observableArrayList();
            
            //SQL statement to get the start date/time
            ResultSet getTypeAndMonth = conn.createStatement().executeQuery(String.format("SELECT type, MONTH(start) month FROM appointment;"));
            
            while (getTypeAndMonth.next()) {
                String monthNum = getTypeAndMonth.getString("month");
                String type = getTypeAndMonth.getString("type");
                System.out.println("Month #" + monthNum + " Type: " + type);
                report1.add(monthNum + " " + type);   
            }
            
            //Add data to month containers depending on the numerical month MM
            for (int i = 0; i < report1.size(); i++) {
                switch (report1.get(i).substring(0,1)) {
                    case "1":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            JanB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            JanI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            JanA.add(report1.get(i));
                        }
                        break;
                    case "2":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            FebB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            FebI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            FebA.add(report1.get(i));
                        }
                        break;
                    case "3":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            MarB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            MarI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            MarA.add(report1.get(i));
                        }
                        break;
                    case "4":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            AprB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            AprI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            AprA.add(report1.get(i));
                        }
                        break;
                    case "5":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            MayB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            MayI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            MayA.add(report1.get(i));
                        }
                        break;
                    case "6":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            JunB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            JunI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            JunA.add(report1.get(i));
                        }
                        break;
                    case "7":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            JulB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            JulI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            JulA.add(report1.get(i));
                        }
                        break;
                    case "8":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            AugB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            AugI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            AugA.add(report1.get(i));
                        }
                        break;
                    case "9":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            SeptB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            SeptI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            SeptA.add(report1.get(i));
                        }
                        break;
                    case "10":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            OctB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            OctI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            OctA.add(report1.get(i));
                        }
                        break;
                    case "11":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            NovB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            NovI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            NovA.add(report1.get(i));
                        }
                        break;
                    case "12":
                        if (report1.get(i).substring(2,3).equals("B")) {
                            DecB.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("I")) {
                            DecI.add(report1.get(i));
                        }
                        if (report1.get(i).substring(2,3).equals("A")) {
                            DecA.add(report1.get(i));
                        }
                        break;
                    default:
                        System.out.println("This didn't work");
                }
            }
                        
            //Append the printout text
            text = text + 
                    "January          | Beginner - " + JanB.size() + "     | Intermediate - " + JanI.size() + "     | Advanced - " + JanA.size() + "\n" +
                    "February         | Beginner - " + FebB.size() + "     | Intermediate - " + FebI.size() + "     | Advanced - " + FebA.size() + "\n" +
                    "March             | Beginner - " + MarB.size() + "     | Intermediate - " + MarI.size() + "     | Advanced - " + MarA.size() + "\n" +
                    "April                | Beginner - " + AprB.size() + "     | Intermediate - " + AprI.size() + "     | Advanced - " + AprA.size() + "\n" +
                    "May                 | Beginner - " + MayB.size() + "     | Intermediate - " + MayI.size() + "     | Advanced - " + MayA.size() + "\n" +
                    "June                | Beginner - " + JunB.size() + "     | Intermediate - " + JunI.size() + "     | Advanced - " + JunA.size() + "\n" +
                    "July                 | Beginner - " + JulB.size() + "     | Intermediate - " + JulI.size() + "     | Advanced - " + JulA.size() + "\n" +
                    "August             | Beginner - " + AugB.size() + "     | Intermediate - " + AugI.size() + "     | Advanced - " + AugA.size() + "\n" +
                    "September      | Beginner - " + SeptB.size() + "     | Intermediate - " + SeptI.size() + "     | Advanced - " + SeptA.size() + "\n" +
                    "October           | Beginner - " + OctB.size() + "     | Intermediate - " + OctI.size() + "     | Advanced - " + OctA.size() + "\n" +
                    "November       | Beginner - " + NovB.size() + "     | Intermediate - " + NovI.size() + "     | Advanced - " + NovA.size() + "\n" +
                    "December       | Beginner - " + DecB.size() + "     | Intermediate - " + DecI.size() + "     | Advanced - " + DecA.size() + "\n"; 
            
            return text;
            
        } catch (Exception e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work"; 
        }
        
    }
    
    
    
    //Generates report: the schedule for consultant 
    public static String reportConsultantSchedule() {
        try {
            
            //Header, also a container for where all text will be appended
            String text = "Consultant " + User.getCurrentUsername() + "'s Schedule\n_______________________________________________________________________________________________\n" +
                    "Date                Start       End           Appointment Info\n_______________________________________________________________________________________________\n";
            
            //Containers for month strings
            ObservableList<String> report2 = FXCollections.observableArrayList();
            
            //SQL statement to get the start date/time
            ResultSet getSchedule = conn.createStatement().executeQuery(String.format("SELECT start, end, customerName, title, description, type, location "
                                                                                    + "FROM appointment a INNER JOIN customer c ON a.customerId=c.customerId WHERE userId='%s' ORDER BY start;", User.getCurrentUserid()));
            
            //Transform the data by extracting only the numerical month MM (like 01)
            while (getSchedule.next()) {
                String date = getSchedule.getString("start").substring(0,10);
                String start = getSchedule.getString("start").substring(11,16);
                String end = getSchedule.getString("end").substring(11,16);
                String name = getSchedule.getString("customerName");
                String title = getSchedule.getString("title");
                String description = getSchedule.getString("description");
                String type = getSchedule.getString("type");
                String location = getSchedule.getString("location");
                    report2.add(date + "\t" + start + "\t" + end + "\t" + name + "     " + title + "     " + description + "     " +  type + "     " +  location);   
            }
            
            for (int i = 0; i < report2.size(); i++) {
                text = text + report2.get(i) + "\n\n";
            }

            return text;
 
        } catch (Exception e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work";
        }
    }
    
    //Generates report: number of appointments per month (one additional report of your choice)
    public static String reportApptsPerMonth() {
        try {
            
            //Header, also a container for where all text will be appended
            String text = "Month - Number of Appointments\n___________________________________\n";
            
            //Containers for month strings
            ObservableList<String> report3 = FXCollections.observableArrayList();
            ObservableList<String> Jan = FXCollections.observableArrayList();
            ObservableList<String> Feb = FXCollections.observableArrayList();
            ObservableList<String> Mar = FXCollections.observableArrayList();
            ObservableList<String> Apr = FXCollections.observableArrayList();
            ObservableList<String> May = FXCollections.observableArrayList();
            ObservableList<String> Jun = FXCollections.observableArrayList();
            ObservableList<String> Jul = FXCollections.observableArrayList();
            ObservableList<String> Aug = FXCollections.observableArrayList();
            ObservableList<String> Sept = FXCollections.observableArrayList();
            ObservableList<String> Oct = FXCollections.observableArrayList();
            ObservableList<String> Nov = FXCollections.observableArrayList();
            ObservableList<String> Dec = FXCollections.observableArrayList();
            
            //SQL statement to get the start date/time
            ResultSet getMonth = conn.createStatement().executeQuery(String.format("SELECT start FROM appointment;"));
            
            //Transform the data by extracting only the numerical month MM (like 01)
            while (getMonth.next()) {
                String monthNum = getMonth.getString("start").substring(5,7);
                report3.add(monthNum);   
            }
            
            //Add data to month containers depending on the numerical month MM
            for (int i = 0; i < report3.size(); i++) {
                switch (report3.get(i)) {
                    case "01":
                        Jan.add(report3.get(i));
                        break;
                    case "02":
                        Feb.add(report3.get(i));
                        break;
                    case "03":
                        Mar.add(report3.get(i));
                        break;
                    case "04":
                        Apr.add(report3.get(i));
                        break;
                    case "05":
                        May.add(report3.get(i));
                        break;
                    case "06":
                        Jun.add(report3.get(i));
                        break;
                    case "07":
                        Jul.add(report3.get(i));
                        break;
                    case "08":
                        Aug.add(report3.get(i));
                        break;
                    case "09":
                        Sept.add(report3.get(i));
                        break;
                    case "10":
                        Oct.add(report3.get(i));
                        break;
                    case "11":
                        Nov.add(report3.get(i));
                        break;
                    case "12":
                        Dec.add(report3.get(i));
                        break;
                    default:
                        System.out.println("Date didn't make sense " + report3.get(i));   
                } 
            }
            
            //Append the printout text
            text = text + 
                    "January - " + Jan.size() + "\n" +
                    "February - " + Feb.size() + "\n" +
                    "March - " + Mar.size() + "\n" +
                    "April - " + Apr.size() + "\n" +
                    "May - " + May.size() + "\n" +
                    "June - " + Jun.size() + "\n" +
                    "July - " + Jul.size() + "\n" +
                    "August - " + Aug.size() + "\n" +
                    "September - " + Sept.size() + "\n" +
                    "October - " + Oct.size() + "\n" +
                    "November - " + Nov.size() + "\n" +
                    "December - " + Dec.size() + "\n";                      
            
            return text; 
            
        } catch (Exception e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work";
        }
    }        
}
    
    
    
    
    
    
    
    
    
    
    

