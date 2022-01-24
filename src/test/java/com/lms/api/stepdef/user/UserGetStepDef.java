package com.lms.api.stepdef.user;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
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
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UserGetStepDef {

	RequestSpecification requestSpec;
	Response response;
	String userId;
	String path;
	String sheetGet;
	String invStatusCode;
	static String validId;

	ExcelReaderUtil excelReaderUtil;
	Scenario scenario;
	Properties properties;
	Dbmanager dbmanager;

	public UserGetStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
		dbmanager = new Dbmanager();
	}

	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetGet = properties.getProperty("sheetGet");
		excelReaderUtil = new ExcelReaderUtil(properties.getProperty("userapi.excel.path"));
		excelReaderUtil.readSheet(sheetGet);

	}

	public void requestSpecification() {

		requestSpec.header("Accept", ContentType.JSON.getAcceptHeader()).contentType(ContentType.JSON);
		requestSpec.log().all();
		response = requestSpec.when().get(path);
	}

	
	@Given("User is on Get Method with end point and no authentication")
	public void user_is_on_get_method_with_end_point_and_no_authentication() {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given();
		path = properties.getProperty("endpoint");
	}

	@When("User sends request without username and password")
	public void user_sends_request_without_username_and_password() {
		requestSpecification();
	}

	@Then("User should receive error status code and message for auth")
	public void user_should_receive_error_status_code_and_message_for_auth() {
		String responseBody = response.prettyPrint();
		assertEquals(Integer.parseInt(properties.getProperty("authStatusCode")), response.statusCode());

		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Response Body is => " + responseBody);
	}

	@Given("User is on Get Method with end point")
	public void user_is_on_get_method_with_end_point() throws IOException {
		RestAssured.baseURI = properties.getProperty("base_uri");
		String ex_username = excelReaderUtil.getDataFromExcel(scenario.getName(), "Username");
		String ex_password = excelReaderUtil.getDataFromExcel(scenario.getName(), "Password");

		requestSpec = RestAssured.given().auth().preemptive().basic(ex_username, ex_password);
		path = properties.getProperty("endpoint");

	}

	@When("User sends correct username and incorrect password")
	public void user_sends_correct_username_and_incorrect_password() {
		requestSpecification();
	}

	@When("User sends incorrect username and correct password")
	public void user_sends_incorrect_username_and_correct_password() {
		requestSpecification();
	}

	@Then("User should receive error status code and message for get")
	public void user_should_receive_error_status_code_and_message_for_get() throws Exception {
		String expStatusCode = excelReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");

		int code = response.getStatusCode();
		String responseBody = response.prettyPrint();
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		System.out.println("Response Status code is =>  " + response.statusCode());
	}

	@When("User sends the request with valid inputs")
	public void user_sends_the_request_with_valid_inputs() {
		requestSpecification();
	}

	@Then("User is able to read all user details and receive status code for get")
	public void User_is_able_to_read_all_user_details_and_receive_status_code_for_get() throws Exception {
		String expStatusCode = excelReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseBody = response.prettyPrint();
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		//assertThat(Integer.parseInt(expStatusCode),is(response.statusCode()));
		assertThat(responseBody, matchesJsonSchemaInClasspath("userGetAll_schema.json"));
		System.out.println("Response Status code is =>  " + response.statusCode());
	}

	@Given("User is on Get Method with end point for single user")
	public void user_is_on_get_method_with_end_point_for_single_user() throws IOException {
		RestAssured.baseURI = properties.getProperty("base_uri");
		String ex_username = excelReaderUtil.getDataFromExcel(scenario.getName(), "Username");
		String ex_password = excelReaderUtil.getDataFromExcel(scenario.getName(), "Password");

		requestSpec = RestAssured.given().auth().preemptive().basic(ex_username, ex_password);
		String userId = excelReaderUtil.getDataFromExcel(scenario.getName(), "UserId");
		
//Converted the userId as int or decimal as it is passed only as a string from excel
		if (userId.equalsIgnoreCase("000")) {
			int invUserid = Integer.parseInt(userId);
			path = properties.getProperty("endpointGet") + invUserid;
		} else if (userId.equalsIgnoreCase("1.23")) {
			float invid = Float.parseFloat(userId);
			path = properties.getProperty("endpointGet") + invid;
		} else
			
			path = properties.getProperty("endpointGet") + userId;
			validId = userId;
			System.out.println("UserId from excel : " + validId);
			

	}

	@When("User sends the request with existing userId")
	public void user_sends_the_request_with_existing_user_id() {
		requestSpecification();

	}

	@Then("User should receive status code and db is validated for specific user")
	public void user_should_receive_status_code_and_db_is_validated_for_specific_user() throws Exception {
		String expStatusCode = excelReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseBody = response.prettyPrint();
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		assertThat(responseBody, matchesJsonSchemaInClasspath("userGet_schema.json"));
		System.out.println("Response Status code is =>  " + response.statusCode());

		System.out.println("Response Body is => " + responseBody);
		
		JsonPath js = response.jsonPath();
		String rsUser_id = js.get("user_id");
		
		// Retrieve a particular user record from tbl_lms_user
		ArrayList<String> dbValidList = dbmanager.dbvalidationUser(rsUser_id);
		String record = dbValidList.toString();
		String dbUserId = dbValidList.get(0);
		
		// DB validation for a get request for an existing user_id
		assertEquals(validId, dbUserId);
		ExtentCucumberAdapter.addTestStepLog("Get specific user " +dbUserId+ " record from DB : " + dbValidList.toString());

	}

	@When("User sends request with invalid userId")
	public void user_sends_request_with_invalid_user_id() {
		requestSpecification();
	}

	@When("User sends request with alphanumeric userId")
	public void user_sends_request_with_alphanumeric_user_id() {
		requestSpecification();
	}

	@When("User sends request with a null userId")
	public void user_sends_request_with_a_null_user_id() {
		requestSpecification();
	}

	@When("User sends request with a decimal userId")
	public void user_sends_request_with_a_decimal_user_id() {
		requestSpecification();
	}

	@Given("User is on Get Method with end point with userID as blank")
	public void user_is_on_get_method_with_end_point_with_user_id_as_blank() {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		path = properties.getProperty("endpointGet");
	}

	@When("User sends request with a blank userId")
	public void user_sends_request_with_a_blank_user_id() {
		requestSpecification();
	}

	@Then("User should receive error status code for get")
	public void user_should_receive_error_status_code_for_get() {

		invStatusCode = properties.getProperty("invStatusCode");
		String responseBody = response.prettyPrint();
		assertEquals(Integer.parseInt(invStatusCode), response.statusCode());

		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Response Body is => " + responseBody);
	}
}
