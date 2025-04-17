package com.example.rental.cucumber;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.rental.cucumber",
    plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberRunner {
    
    @SpringBootApplication
    @ComponentScan(basePackages = {"com.example.rental", "com.example.Tp_TDD_Michelozzi_Matthieu"})
    public static class TestConfig {
    }
}
