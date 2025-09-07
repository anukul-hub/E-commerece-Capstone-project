package stepDefinitions;

import base.DriverFactory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import reporters.ExtentManager;

public class Hooks {

    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();
    private static ExtentReports extent;

    @BeforeAll
    public static void beforeAll() {
        // ✅ Start a single browser for the whole test run
        DriverFactory.getDriver();

        // ✅ Initialize Extent once (don’t fail run if XML is missing)
        try {
            extent = ExtentManager.getInstance();
        } catch (Exception e) {
            System.err.println("Extent init warning: " + e.getMessage());
            extent = null;
        }
    }

    @AfterAll
    public static void afterAll() {
        // ✅ Flush Extent once
        try {
            if (extent != null) extent.flush();
        } catch (Exception e) {
            System.err.println("Extent flush warning: " + e.getMessage());
        }

        // ✅ Close the browser ONCE after all scenarios
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
        try {
            if (extent != null && TEST.get() != null && driver instanceof TakesScreenshot) {
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String b64 = java.util.Base64.getEncoder().encodeToString(png);

                if (scenario.isFailed()) {
                    TEST.get().fail("Scenario failed",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(b64, scenario.getName()).build());
                } else {
                    TEST.get().pass("Scenario passed",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(b64, scenario.getName()).build());
                }
            } else if (extent != null && TEST.get() != null) {
                if (scenario.isFailed()) TEST.get().fail("Scenario failed");
                else TEST.get().pass("Scenario passed");
            }
        } catch (Exception e) {
            System.err.println("After scenario reporting warning: " + e.getMessage());
        } finally {
            TEST.remove();

            // ❌ DO NOT quit the driver here (we want a single window for all scenarios)
            // Optional isolation between scenarios:
            try {
                driver.manage().deleteAllCookies();
            } catch (Exception ignored) {
            }
        }
    }
}
