package com.lms.api.stepdef.skillmap;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.lms.api.dbmanager.Dbmanager;
import com.lms.api.utilities.ExcelReaderUtil;
import com.lms.api.utilities.PropertiesReaderUtil;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UserSkillMapGetStepDef {

	RequestSpecification requestSpec;
	Response response;
	String path;
	String sheetGet;
	static String validId;

	ExcelReaderUtil excelSheetReaderUtil;
	Scenario scenario;

	Properties properties;
	Dbmanager dbmanager;

	public UserSkillMapGetStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
		dbmanager = new Dbmanager();
	}

	// Scenario class will give us information at the runtime like the scenario
	// name, getid() or isFailed()
	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetGet = properties.getProperty("sheetGet");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("skillmap.excel.path"));
		excelSheetReaderUtil.readSheet(sheetGet);

	}
	
	public void requestSpecificationGET() throws IOException {

		String UserSkillsId = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "UserSkills");
		requestSpec.log().all();
		response = requestSpec.when().get(path + UserSkillsId);
	}

	@Given("User is on GETall Method")
	public void user_is_on_getall_method() throws IOException {

		RestAssured.baseURI = properties.getProperty("base_uri");

		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		path = properties.getProperty("skillmap.endpoint.getAll");

	}

	@When("User sends request from UserSkillMap API")
	public void user_sends_request_from_UserSkillMap_API() {
		response = requestSpec.request(Method.GET, path);
	}

	@Then("User receives status code with valid json schemaforall")
	public void user_receives_status_code_with_valid_json_schemaforall() throws IOException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseBody = response.prettyPrint();
		//int statuscode = response.statusCode();
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		assertThat(responseBody, matchesJsonSchemaInClasspath("userSkillMap_schemaAll.json"));
		System.out.println("Validated the schema");
		
		System.out.println("Expected response code: " + expStatusCode);
		System.out.println("Response Body is =>  " + responseBody);
		

	}

	@Given("User is on GET Method")
	public void user_is_on_get_methods() throws IOException {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		path = properties.getProperty("skillmap.endpoint.get");
		

	}

	@When("User sends request with Valid id")
	public void user_sends_request_with_valid_id() throws IOException {
		requestSpecificationGET();
	}

	@Then("User receives status code with valid json schemas")
	public void user_receives_status_code_with_valid_json_schemas() throws IOException, SQLException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseBody = response.prettyPrint();
		
		//int statuscode = response.statusCode();
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		assertThat(responseBody, matchesJsonSchemaInClasspath("userSkillMap_schema.json"));
		System.out.println("Validated the schema");
		
		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Response Body is =>  " + responseBody);
		
		String UserSkillsId = excelSheetReaderUtil.getDataFromExcel(scenario.getName(),"UserSkills");
		validId = UserSkillsId;
		System.out.println("UserId from excel : " + validId);
		
		JsonPath js = response.jsonPath();
		String rsUser_id = js.get("user_skill_id");
		
		// Retrieve a particular user record from tbl_lms_user
		ArrayList<String> dbValidList = dbmanager.dbvalidationUserSkillMap(rsUser_id);
		//String record = dbValidList.toString();
		String dbUserSkillsId = dbValidList.get(0);

		// DB validation for a get request for an existing user_id
		assertEquals(validId, dbUserSkillsId);
				
		

	}

	@When("User sends request with Invalid id")
	public void user_sends_request_with_invalid_id() throws IOException {
		requestSpecificationGET();
	}

	@Then("User gets Response as Bad Request")
	public void user_gets_response_as_bad_request() throws IOException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Expected response code: " + expStatusCode + "Expected message is: " + expMessage);
		//int statuscode = response.statusCode();
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		String responseBody = response.prettyPrint();
		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Response Body is =>  " + responseBody);
	}

	@When("User sends request with Blank id")
	public void user_sends_request_with_blank_id() throws IOException {
		requestSpecificationGET();
	}

	@When("User sends request  with AlphaNumeric id")
	public void user_sends_request_with_alpha_numeric_id() throws IOException {
		requestSpecificationGET();
	}

	@When("User sends request with Decimal as id")
	public void user_sends_request_with_decimal_as_id() throws IOException {
		requestSpecificationGET();
	}

	@Given("User  sets GET request with a valid endpoint UserSkillsMap")
	public void user_sets_get_request_with_a_valid_endpoint_as_url_user_skills_map() {

		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		path = properties.getProperty("skillmap.endpoint.getSkillMap");

	}
	
	@When("User sends request with query param")
	public void user_sends_request_with_query_param() throws IOException {
		requestSpecificationGET();
	}

	@Then("User receives status code with valid json schema for all Skills")
	public void user_receives_status_code_with_valid_json_schemaforall_skills() throws IOException {

		String responseBody = response.prettyPrint();
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		assertThat(responseBody, matchesJsonSchemaInClasspath("userSkillMap_schemaGetAllSkills.json"));
		System.out.println("Validated the schema");
		
		System.out.println("Response Status code is =>  " + response.statusCode());
		System.out.println("Response Body is =>  " + responseBody);
	}

	

}
