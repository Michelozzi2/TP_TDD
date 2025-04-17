package com.example.rental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {

    private CarRepository carRepository;
    private Car car1;
    private Car car2;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
        car1 = new Car("ABC123", "Toyota", true);
        car2 = new Car("XYZ789", "Honda", false);
        
        carRepository.addCar(car1);
        carRepository.addCar(car2);
    }

    @Test
    void getAllCars_shouldReturnAllCars() {
        // When
        List<Car> cars = carRepository.getAllCars();
        
        // Then
        assertEquals(2, cars.size());
        assertTrue(cars.contains(car1));
        assertTrue(cars.contains(car2));
    }

    @Test
    void findByRegistrationNumber_whenCarExists_shouldReturnCar() {
        // When
        Optional<Car> foundCar = carRepository.findByRegistrationNumber("ABC123");
        
        // Then
        assertTrue(foundCar.isPresent());
        assertEquals("Toyota", foundCar.get().getModel());
    }

    @Test
    void findByRegistrationNumber_whenCarDoesNotExist_shouldReturnEmpty() {
        // When
        Optional<Car> foundCar = carRepository.findByRegistrationNumber("NONEXISTENT");
        
        // Then
        assertFalse(foundCar.isPresent());
    }

    @Test
    void addCar_shouldAddCarToRepository() {
        // Given
        Car newCar = new Car("DEF456", "Ford", true);
        
        // When
        carRepository.addCar(newCar);
        
        // Then
        List<Car> cars = carRepository.getAllCars();
        assertEquals(3, cars.size());
        assertTrue(cars.contains(newCar));
    }

    @Test
    void updateCar_shouldUpdateExistingCar() {
        // Given
        Car updatedCar = new Car("ABC123", "Toyota", false);
        
        // When
        carRepository.updateCar(updatedCar);
        
        // Then
        Optional<Car> foundCar = carRepository.findByRegistrationNumber("ABC123");
        assertTrue(foundCar.isPresent());
        assertFalse(foundCar.get().isAvailable());
    }
}
