@requiresLogin
Feature: Login Page Validation

  # -------- Positive Scenarios --------
  Scenario: Login with valid email and password
    Given I launch the application
    When I enter email "6391625760" and password "4TbRQrnBP9j&@7c"
    And I click on login button
    Then I should be logged in successfully

  Scenario: Verify session persistence while browsing
    Given I am logged in with email "6391625760" and password "4TbRQrnBP9j&@7c"
    When I navigate to "Electronics" category
    Then My session should remain active


  # -------- Negative Scenarios --------
  Scenario: Login with blank fields
    Given I launch the application
    When I try to login with blank email and password
    Then I should see "Email/Password required" message

  Scenario: Login with invalid email and password
    Given I launch the application
    When I enter email "wronguser@test.com" and password "WrongPass123"
    And I click on login button
    Then I should see "Invalid credentials" error messagea

 

 
