package UI;

import BaseClass.Course;
import Misc.LimitedTextField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;


public class AddCourseController implements Initializable {
    @FXML private Button addCs, closeCs;
//    @FXML private TextField CsCode, SecCode;
    @FXML LimitedTextField CsCode, SecCode;
    private ObservableList<Course> data ;




    @FXML
    private void onMouseClicked(ActionEvent event) {
        if(event.getSource() == closeCs) {
            Stage stage = (Stage) closeCs.getScene().getWindow();
            stage.hide();
        }
    }

    @FXML
    private void AddButtonListener(){
        String CsCodeStr = CsCode.getText();
        String SecCodeStr = SecCode.getText();

        CsCodeStr = CsCodeStr.replace(" ", "");

        if (CsCodeStr.length() != 0 && SecCodeStr.length() != 0) { //Check if textfield is empty
            CsCodeStr = CsCodeStr.toUpperCase();
            data.add(new Course(CsCodeStr, SecCodeStr));
            CsCode.clear();
            SecCode.clear();
        }
    }



    public void setItems(ObservableList<Course> data) {
        this.data = data ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CsCode.setMaxLength(8);
        SecCode.setMaxLength(1);
    }


}
