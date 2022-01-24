package com.lms.api.stepdef.skills;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "src/test/resources/features/" }, 
				glue = {"com.lms.api.stepdef.skills" }, 
				tags = "@skill",
				monochrome = true,

plugin = {"pretty","com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"}
)

public class SkillAPIRunner {

}
