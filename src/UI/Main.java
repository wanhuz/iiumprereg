package UI;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("IIUM Pre-Registration Utility");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.getIcons().add(new Image("/iiumlogo.png"));
        primaryStage.show();
        root.requestFocus();
    }


    public static void main (String[]args){
        launch(args);
    }

}
