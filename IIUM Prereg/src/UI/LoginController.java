package UI;

import BaseClass.preReg;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;



import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;


public class LoginController implements Initializable {

    @FXML private Button logBtn;
    @FXML private TextField matField;
    @FXML private PasswordField pinField;
    @FXML private Label errorStatus;
    preReg logOnly = new preReg();
    Thread thread = new Thread();


    public void enterPressed(ActionEvent event) throws IOException {
        if (event.getSource() == matField || event.getSource() == pinField) {
            login();
        }
    }

    public void focus() {
        errorStatus.requestFocus();
    }

    public void login() throws IOException {
        if (matField.getLength() > 0 && pinField.getLength() > 0) {


            if (matField.getText().equals(logOnly.getMat_no()) && pinField.getText().equals(logOnly.getPin_no())) {
                Stage primaryStage = (Stage) logBtn.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/mainScreen.fxml"));
                primaryStage.setTitle("IIUM Pre-Registration Utility");
                primaryStage.setResizable(false);
                primaryStage.setScene(new Scene(root, 640, 407));
                primaryStage.show();
                root.requestFocus();
            }
            else { //wrong password/matric
                System.out.println("Wrong Matric number or Password");
                errorStatus.setText("Error: Wrong matric number or password");
            }

        }
        else { //Error code if cannot login
            System.out.println("Blank field detected");
            errorStatus.setText("Error: Blank field detected");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorStatus.setText("");
    }
}
