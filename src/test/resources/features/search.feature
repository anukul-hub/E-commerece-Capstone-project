Feature: Amazon Product Search

  # ====================== Positive Scenarios ======================
  @positive
  Scenario: Valid search using search button
    Given I launch the application
    When I search for "mosquito all out" using search button
    Then I should see search results for "mosquito all out"


  @positive
  Scenario: Search within Electronics department
    Given I launch the application
    When I select "Electronics" department
    And I search for "power bank"
    Then I should see search results for "power bank" in Electronics

 

  # ====================== Negative Scenarios ======================
  @negative
  Scenario: Empty search should not navigate
    Given I launch the application
    When I search with empty text
    Then I should remain on the same page

  @negative
  Scenario: Invalid search with no results
    Given I launch the application
    When I search for "ksdfjerfyt"
    Then I should see no results or suggestions

  @negative
  Scenario: Long string search
    Given I launch the application
    When I search for a very long string
    Then the page should handle the input gracefully
