#User logged in as "APIPROCESSING" with "Basic Auth"
@user
Feature: Delete User Feature

@check
Scenario: To delete the user id with existing user id
    Given User is on Delete Method with endpoint
    When User sends request with existing User_id as input
    Then User should receive status code and message for delete

Scenario: To delete the user id with non-existing user id
    Given User is on Delete Method with endpoint
    When User sends request with  non_existing User_id as input
    Then User should receive status code and message for delete
    
Scenario: To delete the user with invalid user id
    Given User is on Delete Method with endpoint
    When User sends request with user_id as alphanumeric
    Then User should receive status code and message for delete

Scenario: To delete the user id without passing a userid
    Given User is on Delete Method with endpoint without userid as parameter
    When User sends request without a user_id
    Then User should receive status code <nfStatusCode> for delete without parameter