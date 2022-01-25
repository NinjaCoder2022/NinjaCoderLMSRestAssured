package com.lms.api.stepdef.skillmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

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
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class UserSkillMapDeleteStepDef {

	RequestSpecification RequestSpec;
	Response response;
	String userId;
	String path;
	String sheetDelete;

	ExcelReaderUtil excelSheetReaderUtil;
	Scenario scenario;

	Properties properties;
	Dbmanager dbmanager;

	public UserSkillMapDeleteStepDef() {
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
		sheetDelete = properties.getProperty("sheetDelete");
		excelSheetReaderUtil = new ExcelReaderUtil(properties.getProperty("skillmap.excel.path"));
		excelSheetReaderUtil.readSheet(sheetDelete);

	}

	public void requestSpecificationDelete() throws IOException {
		String UserSkillsId = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "UserSkills");
		RequestSpec.log().all();
		response = RequestSpec.when().delete(path + UserSkillsId);
	}

	@Given("User is on DELETE Method")
	public void user_is_on_delete_method() {

		RestAssured.baseURI = properties.getProperty("base_uri");

		RequestSpec = RestAssured.given().auth().preemptive().basic(properties.getProperty("username"),
				properties.getProperty("password"));
		path = properties.getProperty("skillmap.endpoint.delete");

	}

	@When("User sends request with existing user_skill_ Id  as input")
	public void user_sends_request_with_existing_user_skill_id_as_input() throws IOException {
		requestSpecificationDelete();
	}

	@Then("User should recieve a success status code")
	public void User_should_recieve_a_success_status_code() throws IOException {
		String deleteUserSkillsId = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "UserSkills");
		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);

		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());
		
		try {
			//retrieve an array list from DBmanager for delete request
			ArrayList<String> dbValidList = dbmanager.dbvalidationUser(deleteUserSkillsId);

			if (dbValidList.get(0) == "Deleted")
				ExtentCucumberAdapter.addTestStepLog("DB validation for UserSkills " + deleteUserSkillsId + " is Deleted");

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Expected message is: " + expMessage);
		System.out.println("Response Body is =>  " + response.prettyPrint());
		

	}

	@When("User sends request with nonexisting user_skill_ Id  as input")
	public void user_sends_request_with_nonexisting_user_skill_id_as_input() throws IOException {
		requestSpecificationDelete();
	}

	@When("User sends request with user_skill_id as blank")
	public void user_sends_request_with_user_skill_id_as_blank() throws IOException {
		requestSpecificationDelete();
	}

	@When("User sends request with  user_skill_id as alphanumeric")
	public void user_sends_request_with_user_skill_id_as_alphanumeric() throws IOException {
		requestSpecificationDelete();
	}

	@When("User sends request with  user_skill_id as decimal no")
	public void user_sends_request_with_user_skill_id_as_decimal_no() throws IOException {
		requestSpecificationDelete();
	}

	@Then("User should recieve an error status code for UserSkillMap")
	public void user_should_recieve_an_error_status_code_for_userskillmap() throws IOException {

		String expStatusCode = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "StatusCode");
		String expMessage = excelSheetReaderUtil.getDataFromExcel(scenario.getName(), "Message");
		System.out.println("Actual Response Status code=>  " + response.statusCode()
				+ "  Expected Response Status code=>  " + expStatusCode);

		assertEquals(Integer.parseInt(expStatusCode), response.statusCode());

		System.out.println("Expected message is: " + expMessage);
		System.out.println("Response Body is =>  " + response.prettyPrint());

	}

}
