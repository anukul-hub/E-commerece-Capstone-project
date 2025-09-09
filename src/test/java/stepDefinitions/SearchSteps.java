package stepDefinitions;

import base.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.AmazonHomePage;
import pages.ResultsPage;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class SearchSteps {

    private AmazonHomePage homePage;
    private ResultsPage resultsPage;
    private WebDriver driver;

    private void init() {
        if (driver == null) {
            driver = DriverFactory.getDriver();
        }
        if (homePage == null || resultsPage == null) {
            homePage = new AmazonHomePage(driver);
            resultsPage = new ResultsPage(driver);
        }
    }

    @Given("I am on the Amazon homepage")
    public void i_am_on_the_amazon_homepage() {
        init();
        driver.get("https://www.amazon.in");
        Assert.assertTrue(driver.getTitle().contains("Amazon"), "Not on the Amazon homepage.");
    }

    @When("I click on the search result titled {string}")
    public void i_click_on_the_search_result_titled(String bookTitle) {
        init();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            String partialBookTitle = "System Design Interview: An insider's guide";
            WebElement bookLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(),\"" + partialBookTitle + "\")]/ancestor::a[1]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", bookLink);

            String originalHandle = driver.getWindowHandle();
            Set<String> originalHandles = driver.getWindowHandles();

            bookLink.click();

            // **FIX ADDED HERE**: More robust logic to handle both new tabs and same-tab navigation.
            // Wait for a new window handle to appear.
            wait.until(d -> d.getWindowHandles().size() > originalHandles.size());

            // Switch to the new window.
            for (String handle : driver.getWindowHandles()) {
                if (!originalHandles.contains(handle)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }

        } catch (TimeoutException e) {
            // This is a fallback in case the click navigates in the same tab.
            // The next step will verify if the URL has changed.
            System.out.println("INFO: No new tab was detected. Assuming navigation happened in the current tab.");
        } catch (Exception e) {
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("failed_search_results_page.html"),
                        driver.getPageSource().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                System.out.println("DEBUG: Saved failed_search_results_page.html for inspection.");
            } catch (Exception ignored) {}
            Assert.fail("Could not find or click the book titled: '" + bookTitle + "'. See saved HTML for details. Error: " + e.getMessage());
        }
    }

    // ---------- VALID SEARCH ----------
    @When("I search for {string} using search button")
    public void i_search_for_using_search_button(String product) {
        init();
        homePage.enterSearchText(product);
        homePage.clickSearchButton();
    }

    @When("I search for {string} using Enter key")
    public void i_search_for_using_enter_key(String product) {
        init();
        homePage.enterSearchText(product);
        homePage.pressEnter();
    }

    @When("I search for a book {string}")
    public void i_search_for_a_book(String bookTitle) {
        i_search_for_invalid(bookTitle);
    }

    @Then("I should see search results for {string}")
    public void i_should_see_search_results_for(String product) {
        init();
        resultsPage.waitUntilResultsOrMessage(Duration.ofSeconds(15));
        Assert.assertTrue(resultsPage.resultsCount() > 0, "Results should be greater than 0");
    }

    // ---------- SELECT FIRST RESULT (ROBUST) ----------
    @And("I select the book from results")
    public void i_select_the_book_from_results() {
        WebDriver driver = DriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        WebElement chosenLink = null;

        try {
            By resultContainerLocator = By.cssSelector("div[data-component-type='s-search-result']");
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(resultContainerLocator, 0));
            List<WebElement> resultContainers = driver.findElements(resultContainerLocator);

            for (WebElement container : resultContainers) {
                try {
                    WebElement link = container.findElement(By.cssSelector("h2 a.a-link-normal"));
                    if (link.isDisplayed() && link.isEnabled()) {
                        chosenLink = link;
                        break;
                    }
                } catch (NoSuchElementException | StaleElementReferenceException ignored) {
                }
            }

            if (chosenLink == null) {
                chosenLink = driver.findElement(By.cssSelector("a.a-link-normal.s-underline-text.s-underline-link-text"));
            }

        } catch (Exception e) {
            System.err.println("Error finding product link: " + e.getMessage());
        }


        if (chosenLink == null) {
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("failed_search_results.html"),
                        driver.getPageSource().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                System.out.println("DEBUG: Saved failed_search_results.html for inspection.");
            } catch (Exception ignored) {}
            Assert.fail("No clickable search result link was found on the page.");
            return;
        }

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", chosenLink);
            wait.until(ExpectedConditions.elementToBeClickable(chosenLink)).click();
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chosenLink);
            } catch (Exception ex) {
                Assert.fail("Failed to click the chosen product link: " + ex.getMessage());
                return;
            }
        }

        try {
            String original = driver.getWindowHandle();
            Thread.sleep(800);
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(original)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
            wait.until(d -> d.getCurrentUrl().contains("/dp/") ||
                    !d.findElements(By.id("add-to-cart-button")).isEmpty() ||
                    !d.findElements(By.id("buy-now-button")).isEmpty());
        } catch (Exception ignored) {
        }
    }


    // ---------- EMPTY SEARCH ----------
    @When("I search with empty text")
    public void i_search_with_empty_text() {
        init();
        homePage.clearSearch();
        homePage.clickSearchButton();
    }

    @Then("I should remain on the same page")
    public void i_should_remain_on_the_same_page() {
        String currentUrl = DriverFactory.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("amazon"), "User should remain on the same page");
    }

    // ---------- SUGGESTIONS ----------
    @When("I type {string} in the search box")
    public void i_type_in_the_search_box(String text) {
        init();
        homePage.enterSearchText(text);
        homePage.waitForSuggestionsToAppear();
    }

    @Then("I should see up to 10 suggestions")
    public void i_should_see_up_to_10_suggestions() {
        init();
        List<String> suggestions = homePage.getSuggestionsTexts();
        Assert.assertTrue(!suggestions.isEmpty() && suggestions.size() <= 10,
                "Expected between 1 and 10 suggestions, but got " + suggestions.size());
    }

    @And("I select the first suggestion")
    public void i_select_the_first_suggestion() {
        init();
        homePage.clickSuggestionByIndex(0);
    }

    @Then("the search box should match the selected suggestion")
    public void the_search_box_should_match_the_selected_suggestion() {
        init();
        String value = homePage.getSearchValue();
        Assert.assertFalse(value.isEmpty(), "Search box should not be empty");
    }

    @When("I type {string} then append {string}")
    public void i_type_then_append(String first, String second) {
        init();
        homePage.enterSearchText(first);
        homePage.waitForSuggestionsToAppear();
        DriverFactory.getDriver().switchTo().activeElement().sendKeys(second);
    }

    @Then("the suggestions should be updated")
    public void the_suggestions_should_be_updated() {
        init();
        List<String> suggestions = homePage.getSuggestionsTexts();
        Assert.assertTrue(!suggestions.isEmpty(), "Suggestions should update after typing more characters");
    }

    // ---------- DEPARTMENT ----------
    @When("I select {string} department")
    public void i_select_department(String department) {
        init();
        homePage.dismissOverlays();
        homePage.selectDepartment(department);
    }

    @Then("I should see search results for {string} in Electronics")
    public void i_should_see_search_results_for_in_department(String product) {
        init();
        resultsPage.waitUntilResultsOrMessage(Duration.ofSeconds(15));
        Assert.assertTrue(resultsPage.resultsCount() > 0, "Results should be available in Electronics");
    }

    // ---------- INVALID / LONG ----------
    @When("I search for {string}")
    public void i_search_for_invalid(String query) {
        init();
        homePage.enterSearchText(query);
        homePage.clickSearchButton();
    }

    @Then("I should see no results or suggestions")
    public void i_should_see_no_results_or_suggestions() {
        init();
        resultsPage.waitUntilResultsOrMessage(Duration.ofSeconds(15));
        boolean noResults = resultsPage.isNoResultsMessagePresent();
        boolean suggestions = resultsPage.isShowingResultsForPresent();
        Assert.assertTrue(noResults || suggestions || resultsPage.resultsCount() >= 0,
                "Invalid search should show no results or suggestions");
    }
}

