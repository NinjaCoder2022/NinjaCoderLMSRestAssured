package com.lms.api.stepdef.skillmap;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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


public class UserSkillMapPostStepDef {

	RequestSpecification RequestSpec;
	Response response;
	String userId;
	String path;
	String sheetPost;

	ExcelReaderUtil excelSheetReaderUtil;
	Scenario scenario;
	String bodyExcel;
	Properties properties;
	Dbmanager dbmanager;
	
	private static final Logger logger = LogManager.getLogger(UserSkillMapPostStepDef.class);

	public UserSkillMapPostStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
		dbmanager = new Dbmanager();
	}

	// Before annotation from io cucumber
	// Scenario class will give us information at the runtime like the scenario
	// name, getid() or isFailed()
	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetPost = properties.getProperty("sheetPost");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("skillmap.excel.path"));
		excelSheetReaderUtil.readSheet(sheetPost);
	}

	public void requestSpecificationPOST() throws IOException {

		bodyExcel = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Body");
		RequestSpec.header("Content-Type", "application/json");
		RequestSpec.body(bodyExcel).log().all();

		// Validation of requestBody with User schema
		assertThat(bodyExcel, matchesJsonSchemaInClasspath("userSkillMapPost_schema.json"));
		logger.info("Validated the schema");
		response = RequestSpec.post(path);
	}

	@Given("User is on Post Method with endpoint with valid JSON schema")
	public void user_is_on_post_method_with_endpoint_with_valid_json_schema() throws IOException {
		logger.info("@Given User is on Post Method with endpoint with valid JSON schema");
		RestAssured.baseURI = properties.getProperty("base_uri");
		RequestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		path = properties.getProperty("skillmap.endpoint.post");

	}
	
	@When("User sends request with valid input")
	public void user_sends_request_with_valid_input() throws IOException {
		logger.info("@When User sends request with valid input");
		requestSpecificationPOST();
	}
	
	@Given("User is on Post Method with endpoint url SkillsMap")
	public void user_is_on_post_method_with_endpoint_url_skillsmap() throws IOException {
		logger.info("@Given User is on Post Method with endpoint url SkillsMap");
		RestAssured.baseURI = properties.getProperty("base_uri");
		RequestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));

		path = properties.getProperty("skillmap.endpoint.post");
	}
	

	@When("User sends request with inputs where skill id is alphanumeric")
	public void user_sends_request_with_inputs_where_skill_id_is_alphanumeric() throws IOException {
		logger.info("@When User sends request with inputs where skill id is alphanumeric");
			requestSpecificationPOSTWhenExceptionExpected();
	}

	@Then("User should receive valid status codes")
	public void user_should_receive_valid_status_codes() throws Exception {
		logger.info("@Then User should receive valid status codes");
		thenMethodSpecificationPOST();
	}

	@When("User sends request with inputs where skill id is null")
	public void user_sends_request_with_inputs_where_skill_id_is_null() throws IOException {
		logger.info("@When User sends request with inputs where skill id is null");
		requestSpecificationPOSTWhenExceptionExpected();
	}

	@When("User sends request with inputs where user id is null")
	public void user_sends_request_with_inputs_where_user_id_is_null() throws IOException {
		logger.info("@When User sends request with inputs where user id is null");
		requestSpecificationPOSTWhenExceptionExpected();
	}

	@When("User sends request with inputs where month of experience is alphanumeric")
	public void user_sends_request_with_inputs_where_month_of_experience_is_alphanumeric() throws IOException {
		logger.info("@When User sends request with inputs where month of experience is alphanumeric");
		requestSpecificationPOSTWhenExceptionExpected();
	}

	@When("User sends request with inputs where months of experience as null")
	public void user_sends_request_with_inputs_where_months_of_experience_as_null() throws IOException {
		logger.info("@When User sends request with inputs where months of experience as null");
			requestSpecificationPOSTWhenExceptionExpected();
	}

	private void requestSpecificationPOSTWhenExceptionExpected() throws IOException {

		bodyExcel = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Body");
		RequestSpec.header("Content-Type", "application/json");
		RequestSpec.body(bodyExcel).log().all();
		// Below assertion is the hard assertion
		assertThat("Schema Validation Failed",bodyExcel, matchesJsonSchemaInClasspath("userSkillMapPost_schema.json"));
		logger.info("Validated the schema");
		response = RequestSpec.post(path);
	}

	public void thenMethodSpecificationPOST() throws IOException, Exception {

		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		logger.info("Expected response code: " + expStatusCode + "Expected message is: " + expMessage);
		String responseBody = response.prettyPrint();
		logger.info("Response Status code is =>  " + response.statusCode());
		
		//Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		JsonPath js = response.jsonPath();
		String newUserSkill = js.get("user_skill_id");
		
		// Post Schema Validation
		assertThat(responseBody, matchesJsonSchemaInClasspath("userSkillMapPostResponse_schema.json"));
		
		//Message validation
		response.then().assertThat().extract().asString().contains("User successfully Created!");
		
		// Retrieve an auto generated user_id for newly created user from tbl_lms_user
		ArrayList<String> dbValidList = dbmanager.dbvalidationUserSkillMap(newUserSkill);
		String dbUserSkillId = dbValidList.get(0);
		ExtentCucumberAdapter.addTestStepLog("Newly created UserSkill record from DB : " + dbValidList.toString());
		
		// DB validation for a post request for a newly created user_id
		assertEquals(newUserSkill, dbUserSkillId);
		
		logger.info("Response Body is =>  " + responseBody);
	}
	
	@Then("User should receive error status code")
	public void user_should_receive_error_status_code() throws Exception {
		logger.info("@Then User should receive error status code");
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		logger.info("Expected response code: " + expStatusCode + "Expected message is: " + expMessage);
		
		//Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		logger.info("Response Status code is =>  " + response.statusCode());
		logger.info("Response Body is =>  " + response.prettyPrint());
	}
}
