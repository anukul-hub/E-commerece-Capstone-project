Feature: E-commerce Book Purchase Flow (Reduced)


  @negative @requiresLogin
  Scenario: Invalid Login (Negative Flow)
    Given I launch the application
    And I try to login with invalid credentials "temp@ggail.com" and "thisispass"
    Then I should see login error message


  @negative @requiresLogin
  Scenario: Invalid Payment Details (Negative Flow)
    Given I launch the application
    And I login with valid credentials
    When I search for a book "system design by alex wu"
    And I select the book from results
    And I add the book to cart

    And I enter valid shipping details
    And I select payment method "Credit Card" with invalid details
    Then I should see payment failure message

