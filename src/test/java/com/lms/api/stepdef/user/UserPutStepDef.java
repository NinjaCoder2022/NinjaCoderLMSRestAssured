package com.lms.api.stepdef.user;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.lms.api.dbmanager.Dbmanager;
import com.lms.api.utilities.ExcelReaderUtil;
import com.lms.api.utilities.PropertiesReaderUtil;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UserPutStepDef {

	RequestSpecification RequestSpec;
	Response response;
	String userId;
	String path;
	String sheetPut;

	static String exUserId;

	ExcelReaderUtil excelSheetReaderUtil;
	Scenario scenario;
	Properties properties;
	Dbmanager dbmanager;

	public UserPutStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
		dbmanager = new Dbmanager();
	}

	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetPut = properties.getProperty("sheetPut");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("userapi.excel.path"));
		excelSheetReaderUtil.readSheet(sheetPut);

	}

	public void requestSpecification() throws Exception {
		RequestSpec.header("Content-Type", "application/json");
		String bodyExcel = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Body");
		RequestSpec.body(bodyExcel).log().all();

		// Validation of requestBody with User schema
		assertThat("Schema Validation Failed", bodyExcel, matchesJsonSchemaInClasspath("userPutRequest_schema.json"));
		response = RequestSpec.when().put(path);

	}

	@Given("^User is on Put Method with endpoint")
	public void user_is_on_put_method_with_endpoint() throws IOException {

		RestAssured.baseURI = properties.getProperty("base_uri");
		RequestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));

		String userId = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "UserId");

		path = properties.getProperty("endpointGet") + userId;

	}

	@When("User sends request to update user name with valid input")
	public void user_sends_request_to_update_user_name_with_valid_input() throws Exception {

		requestSpecification();

	}

	@Then("User should receive status code and message for put")
	public void user_should_receive_status_code_and_message_for_put() throws Exception {
		String userId = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "UserId");
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Expected response code: " + expStatusCode + "Expected message is: " + expMessage);

		String responseBody = response.prettyPrint();
		JsonPath js = response.jsonPath();
		System.out.println(response.statusCode());
		// Put Schema Validation
		assertThat(responseBody, matchesJsonSchemaInClasspath("userResponse_schema.json"));

		// Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		// Message validation
		response.then().assertThat().extract().asString().contains("User Successfully Updated");

		// Retrieve an updated user_id from tbl_lms_user
		ArrayList<String> dbValidList = dbmanager.dbvalidationUser(userId);
		String dbUserId = dbValidList.get(0);
		boolean putResult = response.equals(dbValidList);
		if(putResult = "true" != null)
			ExtentCucumberAdapter.addTestStepLog("Failed to update the user");
		else 
			ExtentCucumberAdapter.addTestStepLog("User is updated: ");
		

		// DB validation for a put request for updated user_id
		assertEquals(userId, dbUserId);

		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Response Body is =>  " + responseBody);
	}

	@When("User sends request to update user name with alphanumeric input")
	public void user_sends_request_to_update_user_name_with_alphanumeric_input() throws Exception {
		requestSpecification();
	}

	@When("User sends request to update phone number with invalid input")
	public void user_sends_request_to_update_phone_number_with_invalid_input() throws Exception {
		requestSpecification();
	}

	@When("User sends request to update location and comments with alphanumeric input.")
	public void user_sends_request_to_update_location_and_comments_with_alphanumeric_input() throws Exception {
		requestSpecification();
	}

	@When("User sends request to update time zone with valid input")
	public void user_sends_request_to_update_time_zone_with_valid_input() throws Exception {
		requestSpecification();
	}

	@When("User sends request to update time zone with invalid input")
	public void user_sends_request_to_update_time_zone_with_invalid_input() throws Exception {
		requestSpecification();
	}

	@When("User sends request to update Linkedin id with valid input")
	public void user_sends_request_to_update_linkedin_id_with_valid_input() throws Exception {
		requestSpecification();
	}

	@When("user sends request to update visa status with numeric input")
	public void user_sends_request_to_update_visa_status_with_numeric_input() throws Exception {
		requestSpecification();
	}

	@Then("User should receive error status code and message for put")
	public void user_should_receive_error_status_code_and_message_for_put() throws Exception {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Expected response code: " + expStatusCode + "Expected message is: " + expMessage);

		JsonPath js = response.jsonPath();
		String resp_msg = js.get("message");

		// Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Resposne Message is =>  " + resp_msg);
		System.out.println("Response Body is =>  " + response.prettyPrint());
	}

}
