Feature: E-commerce Book Purchase Flow

  @positive @smoke
  Scenario: Successful Book Purchase using Cash on Delivery
    Given I am on the Amazon homepage
    When I search for "system design by alex wu"
    And I click on the search result titled "System Design Interview: An insider's guide – 2 Volume Set (Full Colour Edition)"
    Then I should be taken to the product detail page
    When I click the "Add to Cart" button
    Then I should see a confirmation message that the item was added
    When I click on the cart icon to view my cart
    Then the "Shopping Cart" page should be displayed
    And the book "System Design Interview: An insider's guide – 2 Volume Set (Full Colour Edition)" should be in the cart
    And I validate that the price of the book is "3,064"
    When I click the "Proceed to Buy" button
    When I enter email "mynumber" and password "mypass"
    When I select the "Cash on Delivery/Pay on Delivery" radio button
    Then the payment method should be selected and I can place the order

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
