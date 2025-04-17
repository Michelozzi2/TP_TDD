package com.example.rental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarRentalServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarRentalService carRentalService;

    private Car availableCar;
    private Car unavailableCar;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        availableCar = new Car("ABC123", "Toyota", true);
        unavailableCar = new Car("XYZ789", "Honda", false);
    }

    @Test
    void getAllCars_shouldReturnAllCarsFromRepository() {
        // Given
        List<Car> expectedCars = Arrays.asList(availableCar, unavailableCar);
        when(carRepository.getAllCars()).thenReturn(expectedCars);
        
        // When
        List<Car> result = carRentalService.getAllCars();
        
        // Then
        assertEquals(expectedCars, result);
        verify(carRepository).getAllCars();
    }

    @Test
    void rentCar_whenCarIsAvailable_shouldReturnTrueAndUpdateCar() {
        // Given
        String registrationNumber = "ABC123";
        when(carRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(availableCar));
        
        // When
        boolean result = carRentalService.rentCar(registrationNumber);
        
        // Then
        assertTrue(result);
        assertFalse(availableCar.isAvailable());
        verify(carRepository).findByRegistrationNumber(registrationNumber);
        verify(carRepository).updateCar(availableCar);
    }

    @Test
    void rentCar_whenCarIsNotAvailable_shouldReturnFalse() {
        // Given
        String registrationNumber = "XYZ789";
        when(carRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(unavailableCar));
        
        // When
        boolean result = carRentalService.rentCar(registrationNumber);
        
        // Then
        assertFalse(result);
        verify(carRepository).findByRegistrationNumber(registrationNumber);
        verify(carRepository, never()).updateCar(any(Car.class));
    }

    @Test
    void rentCar_whenCarDoesNotExist_shouldReturnFalse() {
        // Given
        String registrationNumber = "NONEXISTENT";
        when(carRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.empty());
        
        // When
        boolean result = carRentalService.rentCar(registrationNumber);
        
        // Then
        assertFalse(result);
        verify(carRepository).findByRegistrationNumber(registrationNumber);
        verify(carRepository, never()).updateCar(any(Car.class));
    }

    @Test
    void returnCar_whenCarExists_shouldMakeCarAvailableAndUpdate() {
        // Given
        String registrationNumber = "XYZ789";
        when(carRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(unavailableCar));
        
        // When
        carRentalService.returnCar(registrationNumber);
        
        // Then
        assertTrue(unavailableCar.isAvailable());
        verify(carRepository).findByRegistrationNumber(registrationNumber);
        verify(carRepository).updateCar(unavailableCar);
    }

    @Test
    void returnCar_whenCarDoesNotExist_shouldDoNothing() {
        // Given
        String registrationNumber = "NONEXISTENT";
        when(carRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.empty());
        
        // When
        carRentalService.returnCar(registrationNumber);
        
        // Then
        verify(carRepository).findByRegistrationNumber(registrationNumber);
        verify(carRepository, never()).updateCar(any(Car.class));
    }
}
