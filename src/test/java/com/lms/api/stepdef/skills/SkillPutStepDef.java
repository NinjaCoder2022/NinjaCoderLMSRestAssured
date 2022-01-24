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

public class SkillPutStepDef {

	RequestSpecification requestSpec;
	Response response;
	String sheetPut;
	String path;

	Scenario scenario;
	ExcelReaderUtil excelSheetReaderUtil;
	Properties properties;
	Dbmanager dbmanager;

	public SkillPutStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
		dbmanager = new Dbmanager();

	}

	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetPut = properties.getProperty("sheetPut");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("skills.excel.path"));
		excelSheetReaderUtil.readSheet(sheetPut);

	}

	public void requestSpecificationPut() throws IOException {
		requestSpec.header("Content-Type", "application/json");
		String bodyExcel = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Body");
		requestSpec.body(bodyExcel).log().body();
		assertThat("Schema Validation Failed", bodyExcel, matchesJsonSchemaInClasspath("skillPut_schema.json"));
		System.out.println("Validated the schema");
		response = requestSpec.when().put(path);

	}

	@Given("User is on PUT method with endpoint Skills")
	public void user_is_on_put_method_with_endpoint_skills() throws IOException {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		String skill_id = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Skill_id");
		System.out.println("SkillId is : " + skill_id);
		path = properties.getProperty("skills.endpoint") + skill_id;
		System.out.println("Path for Put is " + path);

	}

	@When("User sends request  with valid skill id with valid Json Schema")
	public void user_sends_request_with_valid_skill_id_with_valid_json_schema() throws IOException {
		requestSpecificationPut();

	}

	@Then("User should be able to update the Skill name and db is validated")
	public void user_should_be_able_to_update_the_skill_name() throws IOException, SQLException {
		String skill_id = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Skill_id");
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		String responseBody = response.prettyPrint();

		// Put Schema Validation
		assertThat("Schema Validation Passed", responseBody, matchesJsonSchemaInClasspath("skillResponse_schema.json"));

		// Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		// Message validation
		response.then().assertThat().extract().asString().contains("Skill Successfully Updated");

		System.out.println("Response Message =>  " + responseMessage);

		// Retrieve a particular skill record from tbl_lms_skillmaster
		ArrayList<String> dbValidList = dbmanager.dbvalidationSkill(skill_id);
		String dbskill_Id = dbValidList.get(0);
		boolean putResult = response.equals(dbValidList);
		if(putResult = "true" != null)
			ExtentCucumberAdapter.addTestStepLog("Failed to update the user");
		else 
			ExtentCucumberAdapter.addTestStepLog("User is updated: ");
		// DB validation for a get request for an existing skill_id
		assertEquals(skill_id, dbskill_Id);

		// Extracting a specific string response
		String ResString = response.then().extract().asString();
		JsonPath js = new JsonPath(ResString);

		System.out.println("The Message in PUT is :  " + js.get("message"));
		System.out.println("Response Body is =>  " + response.asPrettyString());

	}

	@When("User sends request with valid skill name but non existing skill id")
	public void user_sends_request_with_valid_skill_name_but_non_existing_skill_id() throws IOException {
		requestSpecificationPut();
	}

	@When("User sends request with inputs like Selenium and Java")
	public void user_sends_request_with_inputs_like_selenium_and_java() throws IOException {
		requestSpecificationPut();

	}

	@When("To update skill name with invalid datatypes")
	public void to_update_skill_name_with_invalid_datatypes() throws IOException {
		requestSpecificationPut();
	}

	@When("User sends request with blank skill Id")
	public void user_sends_request_with_blank_skill_id() throws IOException {
		requestSpecificationPut();
	}

	@When("User sends request  with existing skill name with valid Json Schema")
	public void user_sends_request_with_existing_skill_name_with_valid_json_schema() throws IOException {
		requestSpecificationPut();
	}

	@Then("User should not be able to update the Skill name")
	public void user_should_not_be_able_to_update_the_skill_name() throws IOException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expresponseMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		JsonPath js = response.jsonPath();
		Object resp_msg = js.get("message");
		// Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		System.out.println("Expected Response Message =>  " + expresponseMessage);
		System.out.println("Response Body is =>  " + response.asPrettyString());
		System.out.println("Response Message is =>  " + resp_msg);
	}
}
