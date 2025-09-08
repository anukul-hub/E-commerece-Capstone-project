package stepDefinitions;

import base.DriverFactory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import reporters.CucumberReportGenerator;
import reporters.ExtentManager;
import utils.ResultsExcelWriter;
import utils.ScreenshotUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();
    private static ExtentReports extent;
    private static String runFolder;

    // A public static method to get the current test object
    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    @BeforeAll
    public static void beforeAll() {
        // Start a single browser for the whole test run
        DriverFactory.getDriver();

        // Initialize Extent Reports and other reporting tools
        try {
            extent = ExtentManager.getInstance();
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            runFolder = "test-output/screenshots/run_" + ts;
            ScreenshotUtils.initRunFolder(runFolder);
            ResultsExcelWriter.init();
        } catch (Exception e) {
            System.err.println("Reporting initialization warning: " + e.getMessage());
            extent = null;
        }
    }

    @AfterAll
    public static void afterAll() {
        // Flush Extent report
        if (extent != null) {
            extent.flush();
        }
        // Close Excel writer
        ResultsExcelWriter.close();
        // Close the browser
        DriverFactory.quitDriver();

        // Generate the beautiful Cucumber report
        CucumberReportGenerator.generateReport();
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        if (extent != null) {
            ExtentTest test = extent.createTest(scenario.getName());
            EXTENT_TEST.set(test);
        }
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        // This will run after every Gherkin step and log it to the report
        if (getTest() != null) {
            String stepName = "Step: " + scenario.getSourceTagNames(); // A way to get step text would be more complex, this is a placeholder
            if (scenario.isFailed()) {
                getTest().log(Status.FAIL, "Step Failed");
            } else {
                getTest().log(Status.PASS, "Step Passed");
            }
        }
    }


    @After
    public void afterScenario(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        String status = scenario.getStatus().toString();
        String snapPath = null;

        try {
            // Take screenshot and log to Extent Report
            if (driver instanceof TakesScreenshot) {
                snapPath = ScreenshotUtils.takeScreenshot(driver, scenario.getName());
                if (scenario.isFailed()) {
                    getTest().fail("Scenario Failed.",
                            MediaEntityBuilder.createScreenCaptureFromPath(snapPath).build());
                    getTest().fail(scenario.toString()); // Log exception
                } else {
                    getTest().pass("Scenario Passed.",
                            MediaEntityBuilder.createScreenCaptureFromPath(snapPath).build());
                }
            }
        } catch (Exception e) {
            System.err.println("After scenario reporting warning: " + e.getMessage());
            if (getTest() != null) {
                getTest().warning("Could not take screenshot: " + e.getMessage());
            }
        } finally {
            // Log to Excel
            ResultsExcelWriter.append(scenario.getName(), status, snapPath);
            EXTENT_TEST.remove();

            // Clear cookies for isolation between scenarios
            try {
                driver.manage().deleteAllCookies();
            } catch (Exception ignored) {
            }
        }
    }
}

