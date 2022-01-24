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

public class UserPostStepDef {

	RequestSpecification requestSpec;
	Response response;
	String userId;
	String path;
	String sheetPost;

	ExcelReaderUtil excelSheetReaderUtil;
	Scenario scenario;

	Properties properties;
	Dbmanager dbmanager;

	public UserPostStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
		dbmanager = new Dbmanager();
	}

	// Scenario class will give us information at the runtime like the scenario,
	// name, getid() or isFailed()
	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetPost = properties.getProperty("sheetPost");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("userapi.excel.path"));
		excelSheetReaderUtil.readSheet(sheetPost);

	}

	public void requestSpecificationPost() throws Exception {
		requestSpec.header("Content-Type", "application/json");
		String bodyExcel = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Body");
		assertThat("Schema Validation Failed", bodyExcel, matchesJsonSchemaInClasspath("userPostRequest_schema.json"));
		requestSpec.body(bodyExcel).log().all();

		response = requestSpec.when().post(path);
	}

	@Given("User is on Post Method with endpoint")
	public void user_is_on_post_method_with_endpoint() throws IOException {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));

		path = properties.getProperty("endpointPost");
	}

	@When("User sends request with valid inputs")
	public void user_sends_request_with_valid_inputs() throws IOException {
		String bodyExcel = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Body");
		requestSpec.header("Content-Type", "application/json");
		requestSpec.body(bodyExcel).log().all();

		// Validation of requestBody with User schema
		assertThat(bodyExcel, matchesJsonSchemaInClasspath("userPostRequest_schema.json"));
		System.out.println("Validated the schema");
		response = requestSpec.post(path);
		// response = RequestSpec.request(Method.POST, path);

	}

	@Then("User should receive status code and message for post")
	public void User_should_receive_status_code_and_message_for_post() throws Exception {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Expected response code: " + expStatusCode + " Expected message is: " + expMessage);

		String responseBody = response.prettyPrint();
		JsonPath js = response.jsonPath();
		System.out.println(response.statusCode());
		String newUser = js.get("user_id");
		//String responseSchema = System.getProperty("/src/test/resources/user_response_schema.json");
		// Post Schema Validation
		assertThat(responseBody, matchesJsonSchemaInClasspath("userResponse_schema.json"));
		//assertThat(responseBody,ma)
		
		//Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		
		//Message validation
		response.then().assertThat().extract().asString().contains("User successfully Created!");

		// Retrieve an auto generated user_id for newly created user from tbl_lms_user
		ArrayList<String> dbValidList = dbmanager.dbvalidationUser(newUser);
		String dbUserId = dbValidList.get(0);
		
		ExtentCucumberAdapter.addTestStepLog("Newly created User record from DB : " + dbValidList.toString());
		// DB validation for a post request for a newly created user_id
		assertEquals(newUser, dbUserId);

		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Response Body is =>  " + responseBody);
	}

	@When("User sends request with blank inputs for visa status and location")
	public void user_sends_request_with_blank_inputs_for_visa_status_and_location() throws Exception {
		requestSpecificationPost();
	}

	@When("User sends request with only First name with all other fields valid")
	public void user_sends_request_with_only_first_name_with_all_other_fields_valid() throws Exception {
		requestSpecificationPost();
	}

	@When("User sends request with only Last name with all other fields valid")
	public void user_sends_request_with_only_last_name_with_all_other_fields_valid() throws Exception {
		requestSpecificationPost();
	}

	@When("User sends request with alphanumeric inputs")
	public void user_sends_request_with_alphanumeric_inputs() throws Exception {
		requestSpecificationPost();
	}

	@When("User sends request with invalid linkedin url")
	public void user_sends_request_with_invalid_linkedin_url() throws Exception {
		requestSpecificationPost();
	}

	@When("User sends request with invalid time zone")
	public void user_sends_request_with_invalid_time_zone() throws Exception {
		requestSpecificationPost();
	}

	@When("User sends request with invalid visa status")
	public void user_sends_request_with_invalid_visa_status() throws Exception {
		requestSpecificationPost();
	}

	@When("User sends request with invalid phone number")
	public void user_sends_request_with_invalid_phone_number() throws Exception {
		requestSpecificationPost();
	}

	@When("User sends request with blank inputs for linkedin_url, education_pg, comments")
	public void user_sends_request_with_blank_inputs_for_linkedin_url_education_pg_comments() throws Exception {
		requestSpecificationPost();
	}

	@Then("User should receive error status code and message for post")
	public void user_should_receive_error_status_code_and_message_for_post() throws Exception {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Expected response code: " + expStatusCode + "Expected message is: " + expMessage);
		
		//Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Response Body is =>  " + response.prettyPrint());
	}

}
