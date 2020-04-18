package view_controller;

import database.DatabaseConnection;
import static database.Query.appointmentInFifteen;
import static database.Query.login;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;

public class LoginScreenController implements Initializable {
    
    //Define parts of the login screen
    
    @FXML private Label appointmentManagementSystemLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private TextField usernameField;      
    @FXML private PasswordField passwordField;      
    @FXML private Button loginButton;
    @FXML private Button resetButton;
    
    
    
    //Define alert message Strings
    
    private String errorTitle;
    private String errorHeaderMissing;
    private String errorHeaderIncorrect;
    private String errorContentMissing;
    private String errorContentIncorrect;
    
 
    
    //Define actions for buttons
    
    //Reset text fields
    @FXML private void handleResetButton (ActionEvent event) throws IOException {
        usernameField.clear();
        passwordField.clear();
    }
    
    
    //Validate username and password, move to main screen if credentials are correct
    @FXML private void handleLoginButton (ActionEvent event) throws IOException {
        
        //Get text for username and password fields
        String usernameInput, passwordInput;
        usernameInput = usernameField.getText();
        passwordInput = passwordField.getText();
        
        //Validate whether or not the username and password are correct
        if (login(usernameInput, passwordInput)) {
            
            //Validation successful, record timestamp as a log in the logs.txt file
            String filename = "src/appointment_management_system/logs.txt";
            FileWriter fWriter = new FileWriter(filename, true);
            PrintWriter outputFile = new PrintWriter(fWriter);
            outputFile.println(usernameField.getText() + " logged in on " + LocalDateTime.now()); 
            System.out.println(usernameField.getText() + " logged in on " + LocalDateTime.now());
            outputFile.close();
            
            //Create User, you will use this info later
            Connection con;
            try {
                con = DatabaseConnection.getConnection();
                ResultSet getUserInfo = con.createStatement().executeQuery(String.format("SELECT userId, userName FROM user WHERE userName='%s'", usernameInput));
                getUserInfo.next();
                User currentUser = new User(getUserInfo.getString("userName"), getUserInfo.getString("userId"), true);
                System.out.println("Current userId: " + User.getCurrentUserid() + " userName: " + User.getCurrentUsername());
            
            } catch (SQLException ex) {
                Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Change screens
            System.out.println("Login Successful! Login Screen -> Main Screen");
            Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            
            //Alerts the user if they logged in within 15 minutes of a scheduled appointment
            if (appointmentInFifteen()) {
                System.out.println("User alerted");
            }
            else {
                System.out.println("User not alerted of any appointment soon.");
            }
        }
        else {
            //Username or password field(s) left blank
            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.NONE);
                alert.setTitle(errorTitle);
                alert.setHeaderText(errorHeaderMissing);
                alert.setContentText(errorContentMissing);  
                alert.showAndWait();
            }
            //Username or password incorrect
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.NONE);
                alert.setTitle(errorTitle);
                alert.setHeaderText(errorHeaderIncorrect);
                alert.setContentText(errorContentIncorrect);  
                alert.showAndWait();
            }
        }
    }
     
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        
        //Establish language (English or Spanish) for login screen
        try {
            rb = ResourceBundle.getBundle("Languages/lang", Locale.getDefault());
            if (Locale.getDefault().getLanguage().equals("es") || Locale.getDefault().getLanguage().equals("en")) {
                appointmentManagementSystemLabel.setText(rb.getString("title"));
                usernameLabel.setText(rb.getString("username"));
                passwordLabel.setText(rb.getString("password"));
                loginButton.setText(rb.getString("loginButton"));
                resetButton.setText(rb.getString("resetButton"));
                errorTitle = rb.getString("errorTitle");
                errorHeaderMissing = rb.getString("errorHeaderMissing");
                errorHeaderIncorrect = rb.getString("errorHeaderIncorrect");
                errorContentMissing= rb.getString("errorContentMissing");
                errorContentIncorrect = rb.getString("errorContentIncorrect");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }    
    }       
}
