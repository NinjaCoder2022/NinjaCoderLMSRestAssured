package com.lms.api.stepdef.skillmap;

/*
 * 
 * Author: Mahi
 */
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/", 
						glue = {"com/lms/api/stepdef/skillmap"},
						//tags="@userskillmap",
monochrome=true,
dryRun = false,
plugin = {"pretty","com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"}
)
public class UserSkillMapAPIRunner {
 
}