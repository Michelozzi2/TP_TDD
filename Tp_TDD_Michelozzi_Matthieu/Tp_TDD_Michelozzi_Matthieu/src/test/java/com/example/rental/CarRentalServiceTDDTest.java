package com.example.rental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarRentalServiceTDDTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarRentalService carRentalService;

    private Car toyotaCar;
    private Car hondaCar;
    private Car fordCar;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        toyotaCar = new Car("ABC123", "Toyota", true);
        hondaCar = new Car("XYZ789", "Honda", false);
        fordCar = new Car("DEF456", "Ford", true);
    }

    // Tests for Feature 1: Add a car
    
    @Test
    void addCar_whenRegistrationNumberIsUnique_shouldAddCar() {
        // Given
        when(carRepository.findByRegistrationNumber("DEF456")).thenReturn(Optional.empty());
        
        // When
        boolean result = carRentalService.addCar(fordCar);
        
        // Then
        assertTrue(result);
        verify(carRepository).findByRegistrationNumber("DEF456");
        verify(carRepository).addCar(fordCar);
    }
    
    @Test
    void addCar_whenRegistrationNumberExists_shouldNotAddCar() {
        // Given
        when(carRepository.findByRegistrationNumber("ABC123")).thenReturn(Optional.of(toyotaCar));
        
        // When
        boolean result = carRentalService.addCar(new Car("ABC123", "Toyota", true));
        
        // Then
        assertFalse(result);
        verify(carRepository).findByRegistrationNumber("ABC123");
        verify(carRepository, never()).addCar(any(Car.class));
    }
    
    // Tests for Feature 2: Search cars by model
    
    @Test
    void getCarsByModel_whenModelExists_shouldReturnMatchingCars() {
        // Given
        List<Car> toyotaCars = Collections.singletonList(toyotaCar);
        when(carRepository.getAllCars()).thenReturn(Arrays.asList(toyotaCar, hondaCar, fordCar));
        
        // When
        List<Car> result = carRentalService.getCarsByModel("Toyota");
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getModel());
        verify(carRepository).getAllCars();
    }
    
    @Test
    void getCarsByModel_whenModelDoesNotExist_shouldReturnEmptyList() {
        // Given
        when(carRepository.getAllCars()).thenReturn(Arrays.asList(toyotaCar, hondaCar, fordCar));
        
        // When
        List<Car> result = carRentalService.getCarsByModel("BMW");
        
        // Then
        assertTrue(result.isEmpty());
        verify(carRepository).getAllCars();
    }
}
