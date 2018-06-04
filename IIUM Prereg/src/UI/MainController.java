package UI;

import BaseClass.Course;
import BaseClass.preReg;
import javafx.application.Platform;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @FXML
    private AnchorPane anchorpane;

    //Javafx Variable
    private ObservableList<Course> data;
    private preReg prereg = new preReg();
    private boolean toggleSem = false;

    //Thread variable
    private int ThreadState = -1; //State for thread, -1 for not started/dead, 0 for pause, 1 for resume
    private Thread backgroundThread;
    private static volatile boolean killThread = false;
    private static volatile boolean pause = false;

    //Time and Date variable
    private boolean runDateOnce = false; //Declare and add status message to GUI once to say that the Program is running but wait until specified time
    private LocalDateTime dateTimeLimit;
    private ZoneId z = ZoneId.of("Asia/Singapore");
    private LocalDateTime now;
    private Boolean isLate;


    //Clicking stage will give focus to stage, avoid focus on other thing
    public void focus() {
        anchorpane.requestFocus();
    }

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

    }

    //Delete data by row
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

    //Start main task, that is, start web driver on different thread, wait for time to expire so that web driver can login and register course on Website
    public synchronized void start() {
        if (mainTable.getItems().size() <= 0) {
            addStatus("No data in table");
            return;
        }


        if (ThreadState == -1) { //Start new thread first time
            enableButton(false);
            ThreadState = 1;

            Runnable task = () -> {
                boolean messageOnce = true;

                while(!killThread) {
                    if (!pause) { //Pausecontrol = don't pause
                        messageOnce = true;
                        if (startAt("00:00:00")) {
                            System.out.println("Task is run!");
//                            runTask();
                        }
                    }
                    else { //Pausecontrol = pause, but don't kill thread
                        if (messageOnce) {
                            System.out.println("Thread is paused...");
                            messageOnce = false;
                        }
                    }
                }
                System.out.println("Thread is done/killed");
            };

            backgroundThread = new Thread(task);
            backgroundThread.setDaemon(true);
            backgroundThread.start();
        }
        else if (ThreadState == 1) { //Pause thread
            enableButton(true);
            ThreadState = 0;
            pause = true;
        }
        else if (ThreadState == 0) { //Resume thread
            enableButton(false);
            ThreadState = 1;
            pause = false;
        }


    }

    //Threads for main task to be used in start()
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
                    enableButton(true);
                    startBtn.setDisable(false);
                    loadIcon.setVisible(false);
                }
        );
        ThreadState = -1;
    }

    //Observer list for data
    private ObservableList<Course> getInitialTableData() {
        List<Course> list = new ArrayList<>();
        ObservableList<Course> data = FXCollections.observableList(list);

        //Insert initial data here, data.add(new Course))

        return data;
    }

    //Adding status to status listview given from Prereg class
    private void addStatus() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusMsg.getItems().add(0, prereg.getStatusMsg());
            }
        });
    }

    //Adding status to status listview manually
    private void addStatus(String Msg) {
        statusMsg.getItems().add(0, Msg);
    }

    //Function that take current time and day in Singapore Time and start if equal to specified start time and next day (day+1)
    //TimeToStart is String in HH:MM:SS format, Date is automatically set to This Day + 1 day so that it can start 12AM next day
    private boolean startAt(String timeToStart) {
        if (!runDateOnce) {
            LocalTime limit = LocalTime.parse(timeToStart);
            dateTimeLimit =  LocalDateTime.of(LocalDate.now(z).plusDays(1), limit);
            System.out.println("Waiting until " + dateTimeLimit);
            runDateOnce = true;
            Platform.runLater(
                    () -> {
                        addStatus("Wait until " + dateTimeLimit.toString().replace('T',' ').replace('-','/'));
                    }
            );
        }

        //While loop start here
        now = LocalDateTime.now(z);
        System.out.println(now);

        try { //Check every 1 seconds
            Thread.sleep(1000);
        }
        catch (InterruptedException a) {
            a.printStackTrace();
        }

        isLate = now.isAfter(dateTimeLimit);
        if (isLate) {
            System.out.println("Starting main execution");
            return true;
        }
        return false;



    }

    //Enable or disable button to pause/unpause or disable all button when program is running
    private void enableButton(boolean enable) {
        if (enable == true) {
            startBtn.setText("Start");
            OpenAddCs.setDisable(false);
            remCs.setDisable(false);
            toggleSemBtn.setDisable(false);
        }
        else {
            startBtn.setText("Stop");
            OpenAddCs.setDisable(true);
            remCs.setDisable(true);
            toggleSemBtn.setDisable(true);
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