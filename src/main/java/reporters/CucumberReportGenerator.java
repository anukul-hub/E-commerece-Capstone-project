package reporters;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CucumberReportGenerator {

    public static void generateReport() {
        // The output directory for the beautiful report
        File reportOutputDirectory = new File("target/beautiful-reports");

        // The list of JSON files to use as input
        List<String> jsonFiles = new ArrayList<>();
        jsonFiles.add("target/cucumber-reports/cucumber.json"); // <-- Reading from the new location

        String buildNumber = "1";
        String projectName = "Amazon Automation Project";

        Configuration configuration = new Configuration(reportOutputDirectory, projectName);
        configuration.setBuildNumber(buildNumber);
        configuration.addClassifications("Browser", System.getProperty("browser", "chrome"));
        configuration.addClassifications("Platform", System.getProperty("os.name"));
        configuration.addClassifications("Java Version", System.getProperty("java.version"));

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();
    }
}

