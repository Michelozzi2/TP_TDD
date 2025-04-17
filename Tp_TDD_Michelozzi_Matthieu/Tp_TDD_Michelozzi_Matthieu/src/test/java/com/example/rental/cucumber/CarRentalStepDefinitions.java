package com.example.rental.cucumber;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import com.example.rental.Car;
import com.example.rental.CarRepository;
import com.example.rental.cucumber.CucumberRunner.TestConfig;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(
    classes = TestConfig.class,
    webEnvironment = WebEnvironment.RANDOM_PORT
)
public class CarRentalStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private CarRepository carRepository;
    
    private String testCarRegistration;
    private List<Car> carList;
    
    @Given("des voitures sont disponibles")
    public void des_voitures_sont_disponibles() {
        carRepository.addCar(new Car("TEST-001", "Toyota", true));
        carRepository.addCar(new Car("TEST-002", "Honda", true));
    }

    @When("je demande la liste des voitures")
    public void je_demande_la_liste_des_voitures() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity("/cars", Car[].class);
        carList = List.of(response.getBody());
    }

    @Then("toutes les voitures sont affichées")
    public void toutes_les_voitures_sont_affichées() {
        assertNotNull(carList);
        assertTrue(carList.size() >= 2);
    }

    @Given("une voiture est disponible")
    public void une_voiture_est_disponible() {
        testCarRegistration = "TEST-003";
        carRepository.addCar(new Car(testCarRegistration, "Ford", true));
    }

    @When("je loue cette voiture")
    public void je_loue_cette_voiture() {
        restTemplate.postForEntity("/cars/rent/" + testCarRegistration, null, Boolean.class);
    }

    @Then("la voiture n'est plus disponible")
    public void la_voiture_n_est_plus_disponible() {
        Car car = carRepository.findByRegistrationNumber(testCarRegistration)
                .orElseThrow(() -> new RuntimeException("Voiture non trouvée"));
        assertFalse(car.isAvailable());
    }

    @Given("une voiture est louée")
    public void une_voiture_est_louée() {
        testCarRegistration = "TEST-004";
        Car car = new Car(testCarRegistration, "BMW", false);
        carRepository.addCar(car);
    }

    @When("je retourne cette voiture")
    public void je_retourne_cette_voiture() {
        restTemplate.postForEntity("/cars/return/" + testCarRegistration, null, Void.class);
    }

    @Then("la voiture est marquée comme disponible")
    public void la_voiture_est_marquée_comme_disponible() {
        Car car = carRepository.findByRegistrationNumber(testCarRegistration)
                .orElseThrow(() -> new RuntimeException("Voiture non trouvée"));
        assertTrue(car.isAvailable());
    }
}
