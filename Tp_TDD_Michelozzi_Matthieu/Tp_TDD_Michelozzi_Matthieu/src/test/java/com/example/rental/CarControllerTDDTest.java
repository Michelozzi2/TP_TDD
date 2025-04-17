package com.example.rental;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CarControllerTDDTest {

    private MockMvc mockMvc;
    private CarRentalService carRentalService;
    private CarController carController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create classic mock manually
        carRentalService = mock(CarRentalService.class);
        objectMapper = new ObjectMapper();
        
        // Create controller and inject mock using reflection
        carController = new CarController();
        try {
            Field field = CarController.class.getDeclaredField("carRentalService");
            field.setAccessible(true);
            field.set(carController, carRentalService);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
        
        // Set up MockMvc with standalone configuration
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    // Tests for Feature 1: Add a car
    
    @Test
    void addCar_whenValidCar_shouldReturnSuccess() throws Exception {
        // Given
        Car newCar = new Car("DEF456", "Ford", true);
        String carJson = objectMapper.writeValueAsString(newCar);
        when(carRentalService.addCar(any(Car.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/cars/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("true"));

        verify(carRentalService).addCar(any(Car.class));
    }
    
    @Test
    void addCar_whenDuplicateRegistrationNumber_shouldReturnConflict() throws Exception {
        // Given
        Car duplicateCar = new Car("ABC123", "Toyota", true);
        String carJson = objectMapper.writeValueAsString(duplicateCar);
        when(carRentalService.addCar(any(Car.class))).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/cars/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isConflict())
                .andExpect(content().string("false"));

        verify(carRentalService).addCar(any(Car.class));
    }

    // Tests for Feature 2: Search cars by model
    
    @Test
    void searchCarsByModel_whenModelExists_shouldReturnMatchingCars() throws Exception {
        // Given
        Car toyotaCar = new Car("ABC123", "Toyota", true);
        when(carRentalService.getCarsByModel("Toyota")).thenReturn(Collections.singletonList(toyotaCar));

        // When & Then
        mockMvc.perform(get("/cars/search")
                .param("model", "Toyota")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registrationNumber").value("ABC123"))
                .andExpect(jsonPath("$[0].model").value("Toyota"))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(carRentalService).getCarsByModel("Toyota");
    }
    
    @Test
    void searchCarsByModel_whenModelDoesNotExist_shouldReturnEmptyList() throws Exception {
        // Given
        when(carRentalService.getCarsByModel("BMW")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/cars/search")
                .param("model", "BMW")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(carRentalService).getCarsByModel("BMW");
    }
}
