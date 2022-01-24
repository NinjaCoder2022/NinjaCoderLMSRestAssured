package com.lms.api.stepdef.skills;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
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

public class SkillGetStepDef {
	RequestSpecification requestSpec;
	Response response;
	String path;
	String sheetGet;

	ExcelReaderUtil excelSheetReaderUtil;
	Scenario scenario;

	Properties properties;
	Dbmanager dbmanager;

	public SkillGetStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
		dbmanager = new Dbmanager();
	}

	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetGet = properties.getProperty("sheetGet");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("skills.excel.path"));
		excelSheetReaderUtil.readSheet(sheetGet);

	}

	public void requestSpecificationGet() {
		requestSpec.header("Content-Type", "application/json");
		requestSpec.log();
		response = requestSpec.when().get(path);
	}

	@Given("User is on GET method with endpoint Skills")
	public void user_is_on_get_method_with_endpoint_skills() {

		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		path = properties.getProperty("skills.endpoint.getAll");
		System.out.println("Path for GetAll is " + path);
	}

	@When("User sends request from Skill API")
	public void user_sends_request() {
		requestSpecificationGet();
	}

	@Then("User receives list of all Skills with Json Schema Validation")
	public void user_receives_list_of_all_skills_with_json_schema_validation() throws IOException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseBody = response.asPrettyString();
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		System.out.println("Response Body is =>  " + responseBody);
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		assertThat(responseBody, matchesJsonSchemaInClasspath("skillGetAll_schema.json"));
		System.out.println("Response Status code is =>  " + response.statusCode());

	}

	@Given("User is on GET method with endpoint Skills with Skill_id")
	public void user_is_on_get_method_with_endpoint_url_skills_with_skill_id() throws IOException {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		String skill_id = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Skill_id");
		System.out.println("SkillId is : " + skill_id);
		path = properties.getProperty("skills.endpoint") + skill_id;
		System.out.println("Path for Get is " + path);
	}

	@When("User sends the request with specific Skill_Id")
	public void user_sends_the_request_with_specific_skill_id() {
		requestSpecificationGet();
	}

	@Then("User receives the particular Skill_Id details")
	public void user_receives_the_particular_skill_Id_details() throws IOException, SQLException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseBody = response.asPrettyString();
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		System.out.println("Response Body is =>  " + responseBody);
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		String skill_id = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Skill_id");

		JsonPath js = response.jsonPath();
		String rsSkill_id = js.get("skill_id").toString();
		// Retrieve a particular user record from tbl_lms_skillmaster
		ArrayList<String> dbValidList = dbmanager.dbvalidationSkill(rsSkill_id);
		String dbskill_Id = dbValidList.get(0);
System.out.println("Skilld id from db :  " +dbskill_Id);
		// DB validation for a get request for an existing skill_id
		assertEquals(skill_id, dbskill_Id);
		ExtentCucumberAdapter.addTestStepLog("Get specific skill " +dbskill_Id+ " record from DB : " + dbValidList.toString());
	}

	@When("User sends the request with invalid Skill Id")
	public void user_sends_the_request_with_invalid_skill_id() {
		requestSpecificationGet();
	}

	@When("User sends the request with alphanumeric Skill Id")
	public void user_sends_the_request_with_alphanumeric_skill_id() {
		requestSpecificationGet();
	}

	@Given("User is on GET method with endpoint Skills and Skill id null")
	public void user_is_on_GET_method_with_endpoint_skills_and_skill_id_null() throws IOException {

		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));

		String skill_id = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Skill_id");
		System.out.println("SkillId is : " + skill_id);
		path = properties.getProperty("skills.endpoint") + skill_id;
		System.out.println("Path for Get is " + path);
	}

	@When("User sends the request with skill id as null")
	public void user_sends_the_request_with_skill_id_as_null() {
		requestSpecificationGet();
	}

	@Then("User doesnot get the particular Skill_Id")
	public void user_doesnot_get_the_particular_skill_id() throws IOException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseBody = response.asPrettyString();
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		System.out.println("Response Body is =>  " + responseBody);
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

	}
}
