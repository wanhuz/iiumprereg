package UI;

import BaseClass.Course;
import BaseClass.preReg;
import Misc.LimitedTextField;
import Misc.PauseControl;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button OpenAddCs, startBtn, remCs, openSite;
    @FXML
    private ToggleButton toggleSemBtn;
    @FXML
    private ImageView loadIcon;
    @FXML
    private TableView<Course> mainTable;
    @FXML
    private TableColumn CsCodeCol;
    @FXML
    private TableColumn SecCodeCol;
    @FXML
    private ListView statusMsg;

    private ObservableList<Course> data;
    private preReg prereg = new preReg();
    private boolean toggleSem = false;
    private int ThreadState = -1; //State for thread, -1 for not started/dead, 0 for pause, 1 for resume
    private PauseControl pauseCtrl = new PauseControl();


    //Function to check if button to open adding course is clicked and check if button to open prereg site is clicked
    @FXML
    private void onMouseClicked(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        if (event.getSource() == OpenAddCs) {
            Scene scene;
            FXMLLoader loader = new FXMLLoader();
            AnchorPane anchorPane;
            loader.setLocation(getClass().getResource("/addCourse.fxml"));
            anchorPane = loader.load();
            AddCourseController controller = loader.getController();
            controller.setItems(mainTable.getItems());

            scene = new Scene(anchorPane, 350, 250);
            stage.setTitle("Add Course");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image("/iiumlogo.png"));
            stage.showAndWait();
        }
        else if (event.getSource() == openSite) {
            try {
                Desktop desktop = java.awt.Desktop.getDesktop();
                URI oURL = new URI("http://prereg.iium.edu.my/");
                desktop.browse(oURL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (event.getSource() == stage) { //unfinished
            loadIcon.requestFocus();
        }

    }

    @FXML
    private void deleteButtonListener() {
        int ix = mainTable.getSelectionModel().getSelectedIndex();

        if (mainTable.getItems().size() == 0) {
            System.out.println("No data in table");
            return;
        }

        data.remove(ix);
        if (ix != 0) {
            ix = ix - 1;
        }

        mainTable.requestFocus();
        mainTable.getSelectionModel().select(ix);
        mainTable.getFocusModel().focus(ix);
    }

    //Toggle semester 1 or Semester 3
    public void ToggleSemAction() {

        ToggleGroup group = new ToggleGroup();
        toggleSemBtn.setToggleGroup(group);
        Toggle selectedTogger = group.getSelectedToggle();

        if (selectedTogger == toggleSemBtn) {
            toggleSem = true;
            addStatus("Toggled for Semester 1");
        } else {
            toggleSem = false;
            addStatus("Toggled for Semester 3");
        }

    }

    //Start main task, that is, start web driver, login register course on Website
    public void start() {
        if (mainTable.getItems().size() == 0) {
            addStatus("No data in table");
            return;
        }

        Thread backgroundThread;

        if (ThreadState == 1) { //Pause thread
            System.out.println("Thread state is " + ThreadState);
            startBtn.setText("Start");
            OpenAddCs.setDisable(false);
            remCs.setDisable(false);
            toggleSemBtn.setDisable(false);
            ThreadState = 0;
            pauseCtrl.pause();
        } else if (ThreadState == 0) { //Resume thread
            System.out.println("Thread state is " + ThreadState);
            startBtn.setText("Stop");
            OpenAddCs.setDisable(true);
            remCs.setDisable(true);
            toggleSemBtn.setDisable(true);
            ThreadState = 1;
            pauseCtrl.unpause();
        } else if (ThreadState == -1) { //Start new thread
            System.out.println("Thread state is " + ThreadState);
            startBtn.setText("Stop");
            OpenAddCs.setDisable(true);
            remCs.setDisable(true);
            toggleSemBtn.setDisable(true);
            ThreadState = 1;

            //Create a new thread, there are two function is used in this thread: A function to check when the program is going to start driver and starting the driver
            Runnable task = new Runnable() {
                public void run() {
                    startAt("00:00:00");
                    runTask();
                }
            };

            backgroundThread = new Thread(task);
            backgroundThread.setDaemon(true);
            backgroundThread.start();
        }
    }

    //Thread for main task to be used in start()
    private void runTask() {
        Platform.runLater(
                () -> {
                    startBtn.setDisable(true);
                    loadIcon.setVisible(true);
                }
        );

        prereg.start();
        addStatus();
        prereg.logIn();
        addStatus();

        //Check for sem change, change is semester 1
        if (toggleSem) {
            prereg.toggleSem();
            addStatus();
        }

        //Add course
        int row = data.size(); //How many row are there
        int index = 0;
        while (row > 0) {
            prereg.addCourse(data.get(index).getCourseCode(), data.get(index).getCourseSec());
            addStatus();
            row--;
            index++;
        }

        prereg.quit();
        addStatus();

        Platform.runLater(
                () -> {
                    startBtn.setText("Start");
                    startBtn.setDisable(false);
                    loadIcon.setVisible(false);
                    OpenAddCs.setDisable(false);
                    remCs.setDisable(false);
                    toggleSemBtn.setDisable(false);
                }
        );
        ThreadState = -1;
    }

    private ObservableList<Course> getInitialTableData() {
        List<Course> list = new ArrayList<>();
        ObservableList<Course> data = FXCollections.observableList(list);

        //Insert initial data here, data.add(new Course))

        return data;
    }

    //Adding status given from Prereg class
    private void addStatus() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusMsg.getItems().add(0, prereg.getStatusMsg());
            }
        });
    }

    //Adding status manually
    private void addStatus(String Msg) {
        statusMsg.getItems().add(0, Msg);
    }

    //Function that take current time in Singapore Time and stop if equal to specified start time
    //Timetostart is String in HH:MM:SS format
    private void startAt(String timeToStart) {
        ZoneId z = ZoneId.of("Asia/Singapore");
        LocalTime now;
        Boolean isLate;
        LocalTime limit = LocalTime.parse(timeToStart);
        System.out.println("Waiting until " + limit);
        while (true) {
            now = LocalTime.now(z);
            isLate = now.isAfter(limit);
            if (isLate) {
                return;
            }
        }
    }

    // Set loading icon to be invisible, get initial data if exist, set table properties: No resize, no sorting, align text center
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadIcon.setVisible(false);
        data = getInitialTableData();
        mainTable.setItems(data);
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        CsCodeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        SecCodeCol.setCellValueFactory(new PropertyValueFactory<>("courseSec"));
        CsCodeCol.setSortable(false);
        SecCodeCol.setSortable(false);
        mainTable.getColumns().setAll(CsCodeCol, SecCodeCol);
        SecCodeCol.setStyle("-fx-alignment: CENTER;");
    }


}
