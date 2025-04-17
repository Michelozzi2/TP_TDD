package com.example.rental;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
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

class CarControllerIntegrationTest {

    private MockMvc mockMvc;
    private CarRentalService carRentalService;
    private CarController carController;

    private Car availableCar;
    private Car unavailableCar;
    private List<Car> carList;

    @BeforeEach
    void setUp() {
        
        carRentalService = mock(CarRentalService.class);
        
        carController = new CarController();
        try {
            Field field = CarController.class.getDeclaredField("carRentalService");
            field.setAccessible(true);
            field.set(carController, carRentalService);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
        
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
        
        availableCar = new Car("ABC123", "Toyota", true);
        unavailableCar = new Car("XYZ789", "Honda", false);
        carList = Arrays.asList(availableCar, unavailableCar);
    }

    @Test
    void getAllCars_shouldReturnListOfCars() throws Exception {
        when(carRentalService.getAllCars()).thenReturn(carList);

        mockMvc.perform(get("/cars")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registrationNumber").value("ABC123"))
                .andExpect(jsonPath("$[0].model").value("Toyota"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].registrationNumber").value("XYZ789"))
                .andExpect(jsonPath("$[1].model").value("Honda"))
                .andExpect(jsonPath("$[1].available").value(false));

        verify(carRentalService).getAllCars();
    }

    @Test
    void rentCar_whenCarIsAvailable_shouldReturnTrue() throws Exception {
        when(carRentalService.rentCar("ABC123")).thenReturn(true);

        mockMvc.perform(post("/cars/rent/ABC123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(carRentalService).rentCar("ABC123");
    }

    @Test
    void rentCar_whenCarIsNotAvailable_shouldReturnFalse() throws Exception {
        when(carRentalService.rentCar("XYZ789")).thenReturn(false);

        mockMvc.perform(post("/cars/rent/XYZ789"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(carRentalService).rentCar("XYZ789");
    }

    @Test
    void returnCar_shouldCallServiceAndReturnOk() throws Exception {
        doNothing().when(carRentalService).returnCar("ABC123");

        mockMvc.perform(post("/cars/return/ABC123"))
                .andExpect(status().isOk());

        verify(carRentalService).returnCar("ABC123");
    }
}
