package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        // point to the folder that contains all your .feature files
        features = "src/test/resources/features",
        // glue code package where your step definitions live
        glue = {"stepDefinitions"},
        // output reports
        plugin = {
                "pretty",
                // We'll put the reports in a dedicated folder for cleanliness
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {


}
