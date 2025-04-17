package com.example.rental;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

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

    
    @Test
    void addCar_whenRegistrationNumberIsUnique_shouldAddCar() {
        when(carRepository.findByRegistrationNumber("DEF456")).thenReturn(Optional.empty());
        
        boolean result = carRentalService.addCar(fordCar);
        
        assertTrue(result);
        verify(carRepository).findByRegistrationNumber("DEF456");
        verify(carRepository).addCar(fordCar);
    }
    
    @Test
    void addCar_whenRegistrationNumberExists_shouldNotAddCar() {
        when(carRepository.findByRegistrationNumber("ABC123")).thenReturn(Optional.of(toyotaCar));
        
        boolean result = carRentalService.addCar(new Car("ABC123", "Toyota", true));
        
        assertFalse(result);
        verify(carRepository).findByRegistrationNumber("ABC123");
        verify(carRepository, never()).addCar(any(Car.class));
    }
    
    
    @Test
    void getCarsByModel_whenModelExists_shouldReturnMatchingCars() {
        List<Car> toyotaCars = Collections.singletonList(toyotaCar);
        when(carRepository.getAllCars()).thenReturn(Arrays.asList(toyotaCar, hondaCar, fordCar));
        
        List<Car> result = carRentalService.getCarsByModel("Toyota");
        
        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getModel());
        verify(carRepository).getAllCars();
    }
    
    @Test
    void getCarsByModel_whenModelDoesNotExist_shouldReturnEmptyList() {
        when(carRepository.getAllCars()).thenReturn(Arrays.asList(toyotaCar, hondaCar, fordCar));
        
        List<Car> result = carRentalService.getCarsByModel("BMW");
        
        assertTrue(result.isEmpty());
        verify(carRepository).getAllCars();
    }
}
