package com.example.rental;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

class CarControllerTDDTest {

    private MockMvc mockMvc;
    private CarRentalService carRentalService;
    private CarController carController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        carRentalService = mock(CarRentalService.class);
        objectMapper = new ObjectMapper();
        
        carController = new CarController();
        try {
            Field field = CarController.class.getDeclaredField("carRentalService");
            field.setAccessible(true);
            field.set(carController, carRentalService);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
        
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    @Test
    void addCar_whenValidCar_shouldReturnSuccess() throws Exception {
        Car newCar = new Car("DEF456", "Ford", true);
        String carJson = objectMapper.writeValueAsString(newCar);
        when(carRentalService.addCar(any(Car.class))).thenReturn(true);

        mockMvc.perform(post("/cars/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("true"));

        verify(carRentalService).addCar(any(Car.class));
    }
    
    @Test
    void addCar_whenDuplicateRegistrationNumber_shouldReturnConflict() throws Exception {
        Car duplicateCar = new Car("ABC123", "Toyota", true);
        String carJson = objectMapper.writeValueAsString(duplicateCar);
        when(carRentalService.addCar(any(Car.class))).thenReturn(false);

        mockMvc.perform(post("/cars/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isConflict())
                .andExpect(content().string("false"));

        verify(carRentalService).addCar(any(Car.class));
    }

    @Test
    void searchCarsByModel_whenModelExists_shouldReturnMatchingCars() throws Exception {
        Car toyotaCar = new Car("ABC123", "Toyota", true);
        when(carRentalService.getCarsByModel("Toyota")).thenReturn(Collections.singletonList(toyotaCar));

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
        when(carRentalService.getCarsByModel("BMW")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cars/search")
                .param("model", "BMW")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(carRentalService).getCarsByModel("BMW");
    }
}
