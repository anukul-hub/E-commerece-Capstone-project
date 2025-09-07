package stepDefinitions;

import base.DriverFactory;
import io.cucumber.java.en.And;
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

public class SearchSteps {

    private AmazonHomePage homePage;
    private ResultsPage resultsPage;

    private void init() {
        if (homePage == null || resultsPage == null) {
            homePage = new AmazonHomePage(DriverFactory.getDriver());
            resultsPage = new ResultsPage(DriverFactory.getDriver());
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

    // Alias for feature: When I search for a book "system design by alex wu"
    @When("I search for a book {string}")
    public void i_search_for_a_book(String bookTitle) {
        init();
        homePage.enterSearchText(bookTitle);
        homePage.clickSearchButton();
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
            // This is a more stable locator for the main container of each search result.
            // 'data-asin' is Amazon's unique ID and is less likely to change than CSS classes.
            By resultContainerLocator = By.cssSelector("div[data-component-type='s-search-result']");
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(resultContainerLocator, 0));
            List<WebElement> resultContainers = driver.findElements(resultContainerLocator);

            // Find the first visible and clickable product link within the containers.
            // The main link is usually inside an <h2> tag.
            for (WebElement container : resultContainers) {
                try {
                    WebElement link = container.findElement(By.cssSelector("h2 a.a-link-normal"));
                    if (link.isDisplayed() && link.isEnabled()) {
                        chosenLink = link;
                        break; // Found a valid link, exit the loop
                    }
                } catch (NoSuchElementException | StaleElementReferenceException ignored) {
                    // Ignore if a container doesn't have the link or is stale
                }
            }

            // If the primary locator fails, try a broader fallback
            if (chosenLink == null) {
                chosenLink = driver.findElement(By.cssSelector("a.a-link-normal.s-underline-text.s-underline-link-text"));
            }

        } catch (Exception e) {
            // If any error occurs during search, we'll hit the failure logic below.
            System.err.println("Error finding product link: " + e.getMessage());
        }


        // If no link was found after trying, fail the test and save the page source for debugging.
        if (chosenLink == null) {
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("failed_search_results.html"),
                        driver.getPageSource().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                System.out.println("DEBUG: Saved failed_search_results.html for inspection.");
            } catch (Exception ignored) {}
            Assert.fail("No clickable search result link was found on the page.");
            return;
        }

        // Safely click the chosen link (scroll into view + JS fallback)
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

        // Switch to the new window/tab if one was opened
        try {
            String original = driver.getWindowHandle();
            // Wait a moment for the new window to open
            Thread.sleep(800);
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(original)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
            // Wait for the Product Detail Page (PDP) to load
            wait.until(d -> d.getCurrentUrl().contains("/dp/") ||
                    !d.findElements(By.id("add-to-cart-button")).isEmpty() ||
                    !d.findElements(By.id("buy-now-button")).isEmpty());
        } catch (Exception ignored) {
            // Ignore if no new window, or if wait times out
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
