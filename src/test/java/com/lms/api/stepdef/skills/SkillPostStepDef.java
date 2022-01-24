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

public class SkillPostStepDef {

	RequestSpecification requestSpec;
	Response response;
	String path;
	Scenario scenario;
	ExcelReaderUtil excelSheetReaderUtil;
	String sheetPost;
	Properties properties;
	Dbmanager dbmanager;

	public SkillPostStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
		dbmanager = new Dbmanager();
	}

	// Scenario class will give us information at the runtime like the scenario, name, getid() or isFailed()
	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetPost = properties.getProperty("sheetPost");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("skills.excel.path"));
		excelSheetReaderUtil.readSheet(sheetPost);

	}

	public void requestSpecificationPost() throws IOException {
		requestSpec.header("Content-Type", "application/json");
		String bodyExcel = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Body");
		requestSpec.body(bodyExcel).log().body();
		assertThat("Schema Validation Failed",bodyExcel, matchesJsonSchemaInClasspath("skillPost_schema.json"));
		System.out.println("Validated the schema");
		response = requestSpec.when().post(path);

	}

	@Given("User is on POST method with endpoint url Skills")
	public void user_is_on_post_method_with_endpoint_url_skills() throws IOException {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		path = properties.getProperty("skills.endpoint.Post");
		System.out.println("Path for Post is " + path);
	}

	@When("User sends request with inputs  skill name with valid Json Schema")
	public void user_sends_request_with_inputs_skill_name_with_valid_json_schema() throws IOException {
		requestSpecificationPost();

	}

	@Then("User is able to create a new Skill id and db is validated")
	public void user_is_able_to_create_a_new_skill_id_and_db_is_validated() throws IOException, SQLException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		System.out.println("Response Body is =>  " + response.asPrettyString());
		// Post Schema Validation
		assertThat(response.asPrettyString(), matchesJsonSchemaInClasspath("skillResponse_schema.json"));
		
		//Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		
		//Message validation
		response.then().assertThat().extract().asString().contains(expMessage);
		System.out.println("Response Message =>  " + expMessage);
		
		//Retrieve the auto generated skillid from response
		JsonPath js = response.jsonPath();
		String newSkill_id = js.get("skill_id").toString();
		
		// Retrieve an updated skill_id from tbl_lms_skillmaster
		ArrayList<String> dbValidList = dbmanager.dbvalidationSkill(newSkill_id);
		String dbskill_Id = dbValidList.get(0);
		ExtentCucumberAdapter.addTestStepLog("Newly created Skill record from DB : " + dbValidList.toString());
		// DB validation for a post request for an newly created skill_id
		assertEquals(newSkill_id, dbskill_Id);
	}

	@When("User sends request with blank inputs")
	public void user_sends_request_with_blank_inputs() throws IOException {
		requestSpecificationPost();
	}

	@When("User sends request with boolean as skill name")
	public void user_sends_request_with_boolean_as_skill_name() throws IOException {
		requestSpecificationPost();
	}

	@When("User sends request with integer as  skill name")
	public void user_sends_request_with_integer_as_skill_name() throws IOException {
		requestSpecificationPost();
	}

	@When("User sends request with Alphanumeric as skill name")
	public void user_sends_request_with_alphanumeric_as_skill_name() throws IOException {
		requestSpecificationPost();
	}

	@When("User sends request with an existing skill name")
	public void user_sends_request_with_an_existing_skill_name() throws IOException {
		requestSpecificationPost();
	}

	@Then("User cannot create a new Skill id")
	public void user_cannot_create_a_new_skill_id() throws IOException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		System.out.println("Response Body is =>  " + response.asPrettyString());
		
		//Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		System.out.println("Response Message =>  " + responseMessage);
	}

}
