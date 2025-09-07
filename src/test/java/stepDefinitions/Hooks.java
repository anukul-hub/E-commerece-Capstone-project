package stepDefinitions;

import base.DriverFactory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.cucumber.java.*;
import org.openqa.selenium.WebDriver;
import reporters.CucumberReportGenerator; // <-- IMPORT THE NEW CLASS
import utils.ResultsExcelWriter;
import utils.ScreenshotUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();
    private static ExtentReports extent;
    private static String RUN_FOLDER;

    @BeforeAll
    public static void beforeAll() {
        // Start a single browser for the whole test run
        DriverFactory.getDriver();

        // Initialize Extent Reports
        extent = reporters.ExtentManager.getInstance(); // Using the reporters package version

        // Create a unique folder for this test run's screenshots
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        RUN_FOLDER = "test-output/screenshots/run_" + ts;
        ScreenshotUtils.initRunFolder(RUN_FOLDER);

        // Initialize the Excel writer
        ResultsExcelWriter.init();
    }

    @AfterAll
    public static void afterAll() {
        // Flush the Extent report to write everything to the file
        if (extent != null) {
            extent.flush();
        }

        // Close the Excel writer
        ResultsExcelWriter.close();

        // Generate the pretty Cucumber HTML report from the JSON file
        CucumberReportGenerator.generateReport();

        // Close the browser ONCE after all scenarios are done
        DriverFactory.quitDriver();
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        if (extent != null) {
            TEST.set(extent.createTest(scenario.getName()));
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        String status = scenario.getStatus().toString();
        String snapPath = null;

        try {
            // 1. Take screenshot and save it as a file
            snapPath = ScreenshotUtils.takeScreenshot(driver, scenario.getName());

            // 2. Attach the FILE path to the Extent Report
            if (extent != null && TEST.get() != null) {
                if (scenario.isFailed()) {
                    TEST.get().fail("Scenario failed",
                            MediaEntityBuilder.createScreenCaptureFromPath(snapPath).build());
                } else {
                    TEST.get().pass("Scenario passed",
                            MediaEntityBuilder.createScreenCaptureFromPath(snapPath).build());
                }
            }
        } catch (Exception e) {
            System.err.println("After scenario reporting warning: " + e.getMessage());
        } finally {
            // 3. Log the result to the Excel file
            try {
                ResultsExcelWriter.append(scenario.getName(), status, snapPath);
            } catch (Exception ignored) {
            }

            // Clean up for the next scenario
            TEST.remove();
            try {
                driver.manage().deleteAllCookies();
            } catch (Exception ignored) {
            }
        }
    }
}

