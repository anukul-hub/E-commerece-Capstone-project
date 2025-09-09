@requiresLogin
Feature: Login Page Validation

  # -------- Positive Scenarios --------
  Scenario: Login with valid email and password
    Given I launch the application
    When I enter email "mynumber" and password "mypass"
    And I click on login button
    Then I should be logged in successfully

  Scenario: Verify session persistence while browsing
    Given I am logged in with email "mynumber" and password "mypass"
    When I navigate to "Electronics" category
    Then My session should remain active


  # -------- Negative Scenarios --------
  Scenario: Login with blank fields
    Given I launch the application
    When I try to login with blank email and password
    Then I should see "Email/Password required" message

  Scenario: Login with invalid email and password
    Given I launch the application
    When I enter email "userdoesnotexist@elusive.com" and password "thispassdoestnotexist"
    And I click on login button
    Then I should see "Invalid credentials" error message

 

 
