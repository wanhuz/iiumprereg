package BaseClass;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class preReg {

    private String mat_no = "1719365";
    private String pin_no = "Wahwa1996";
    private WebDriver driver;
    private String statusMsg;

    public preReg() {

    }

    public preReg(String mat_no, String pin_no) {
        this.mat_no = mat_no;
        this.pin_no = pin_no;
    }

    public String getMat_no() { return mat_no; }

    public String getPin_no() {
        return pin_no;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void start() {
//        ChromeOptions chromeOptions = new ChromeOptions(); //Start chrome headless
//        chromeOptions.addArguments("--headless");
//        driver = new ChromeDriver(chromeOptions);
        driver = new HtmlUnitDriver();
        driver.get("http://prereg.iium.edu.my/");
        driver.get("http://prereg.iium.edu.my/login.php");
        statusMsg = "Web driver started";
    }

    public void logIn() {
        WebElement element = driver.findElement(By.name("mat")); //Find matric no input box
        element.sendKeys(mat_no); //Send matric_no
        element = driver.findElement(By.name("pin"));
        element.sendKeys(pin_no);
        driver.findElement(By.name("Submit")).click();
        System.out.println("Successfully logged in");
        statusMsg = "Successfully logged in";
    }

    public void logIn(String mat_no, String pin_no) {
        WebElement element = driver.findElement(By.name("mat")); //Find matric no input box
        element.sendKeys(mat_no); //Send matric_no
        element = driver.findElement(By.name("pin"));
        element.sendKeys(pin_no);
        driver.findElement(By.name("Submit")).click();
        System.out.println("Successfully logged in");
        statusMsg = "Successfully logged in";
    }

    public void toggleSem() { //Change first time to Sem 1, second time to Second 3, Only useful for inter-vacation registration
        driver.get("http://prereg.iium.edu.my/ora6.php?&toggle_sem=1"); //toggle change semester
        System.out.println("Semester successfully changed.");
        sleep(5);
        statusMsg = "Semester successfully changed.";
    }

    public void addCourse(String courseCode, String sectionNo) {
        driver.get("http://prereg.iium.edu.my/addform.php");
        WebElement element = driver.findElement(By.name("Course_code"));
        element.sendKeys(courseCode);
        element = driver.findElement(By.name("Section"));
        element.sendKeys(sectionNo);
        driver.findElement(By.name("Submit")).click();
        sleep(3);
        System.out.println("Adding course " + courseCode + "...");


        WebElement status; //Find status if adding course is successful or not
        if (driver.findElements(By.xpath("/html/body/p[2]")).size() > 0) //If this status message is found, then adding course is not successful
            status = driver.findElement(By.xpath("/html/body/p[2]"));
        else
            status = driver.findElement(By.xpath("/html/body/center/h3")); //If this status message is found, then adding course is successful
        System.out.println(status.getText());
        statusMsg = status.getText();
    }

    public void sleep(int Seconds) {
        Seconds *= 1000;
        try {
            Thread.sleep(Seconds);
            System.out.println("Sleep for " + Seconds/1000 + " seconds.");
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void quit() {
        driver.quit();
        System.out.println("Chrome exited");
        statusMsg = "Driver exited";
    }
}