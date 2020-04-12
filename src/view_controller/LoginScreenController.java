package view_controller;

import static database.Query.login;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
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
            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);
            outputFile.println(usernameField.getText() + " logged in on " + ts); 
            System.out.println(usernameField.getText() + " logged in on " + ts);
            outputFile.close();

            //Change screens
            System.out.println("Login Successful! Login Screen -> Main Screen");
            Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
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
