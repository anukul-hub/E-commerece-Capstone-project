package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private final int max = 1; // retry once
    private int count = 0;

    @Override
    public boolean retry(ITestResult result) {
        return count++ < max;
    }
}
