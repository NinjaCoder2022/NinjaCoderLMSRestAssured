package com.lms.api.stepdef.skills;



import java.io.IOException;
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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.junit.Assert.assertEquals;
public class SkillDeleteStepDef {

	RequestSpecification requestSpec;
	Response response;
	String path;
	String sheetDelete;

	ExcelReaderUtil excelSheetReaderUtil;
	Scenario scenario;
	Properties properties;
	Dbmanager dbmanager;
	
	public SkillDeleteStepDef() {
		PropertiesReaderUtil propUtil = new PropertiesReaderUtil();
		properties = propUtil.loadProperties();
	}

	@Before
	public void initializeDataTable(Scenario scenario) throws Exception {
		this.scenario = scenario;
		sheetDelete = properties.getProperty("sheetDelete");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("skills.excel.path"));
		excelSheetReaderUtil.readSheet(sheetDelete);

	}

	public void requestSpecificationDelete() {
		requestSpec.header("Content-Type", "application/json");
		requestSpec.log();
		response = requestSpec.when().delete(path);
	}

	@Given("User is on DELETE method with endpoint")
	public void user_is_on_delete_method_with_endpoint() throws IOException {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		String skill_id = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Skill_id");
		System.out.println("SkillId is : " + skill_id);
		path = properties.getProperty("skills.endpoint.Delete") + skill_id;
		System.out.println("Path for Delete is " + path);

	}

	@When("User sends request with existing skill_id")
	public void user_sends_request_with_existing_skill_id() throws IOException {
		requestSpecificationDelete();
		
	}

	@Then("User should be able to delete the existing skill_id")
	public void user_should_be_able_to_delete_the_existing_skill_id() throws IOException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");

		String responseBody = response.asPrettyString();
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		System.out.println("Response Body is =>  " + responseBody);
		// Status code validation
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		// Message validation
		JsonPath js = new JsonPath(responseBody);
		System.out.println(js);
		response.then().assertThat().extract().asString().contains("Deleted");
		System.out.println("Response Message =>  " + responseMessage);

	}

	@When("User sends request with non-existing skill_id")
	public void user_sends_request_with_non_existing_skill_id() {
		requestSpecificationDelete();
	}

	@When("User sends request with blank skill_id")
	public void user_sends_request_with_blank_skill_id() {
		requestSpecificationDelete();
	}

	@When("User sends the request with alphanumeric skill Id")
	public void user_sends_the_request_with_alphanumeric_skill_id() {
		requestSpecificationDelete();
	}

	@When("User enter the Skill_id as decimal")
	public void user_enter_the_skill_id_as_decimal() {
		requestSpecificationDelete();
	}

	@Then("User should recieve an error status code")
	public void user_should_recieve_an_error_status_code() throws IOException {
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String responseMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);
		String responseBody = response.asPrettyString();
		System.out.println("Response Body is =>  " + responseBody);
		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		System.out.println("Response Message =>  " + responseMessage);

	}
}
