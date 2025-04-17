package com.example.rental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

class CarRentalServiceSpyTest {

    @Spy
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
        
        // Configuration des comportements du spy
        doReturn(Optional.of(availableCar)).when(carRepository).findByRegistrationNumber("ABC123");
        doReturn(Optional.of(unavailableCar)).when(carRepository).findByRegistrationNumber("XYZ789");
        doReturn(Optional.empty()).when(carRepository).findByRegistrationNumber("NON_EXISTENT");
    }

    @Test
    void rentCar_whenCarIsAvailable_shouldCallUpdateCar() {
        // When
        boolean result = carRentalService.rentCar("ABC123");
        
        // Then
        assertTrue(result);
        assertFalse(availableCar.isAvailable()); // Vérifier que la voiture est maintenant indisponible
        verify(carRepository).findByRegistrationNumber("ABC123");
        verify(carRepository).updateCar(availableCar); // Vérifier que updateCar a été appelé avec la voiture correcte
    }

    @Test
    void rentCar_whenCarIsNotAvailable_shouldNotCallUpdateCar() {
        // When
        boolean result = carRentalService.rentCar("XYZ789");
        
        // Then
        assertFalse(result);
        verify(carRepository).findByRegistrationNumber("XYZ789");
        verify(carRepository, never()).updateCar(any(Car.class)); // Vérifier que updateCar n'a pas été appelé
    }

    @Test
    void rentCar_whenCarDoesNotExist_shouldNotCallUpdateCar() {
        // When
        boolean result = carRentalService.rentCar("NON_EXISTENT");
        
        // Then
        assertFalse(result);
        verify(carRepository).findByRegistrationNumber("NON_EXISTENT");
        verify(carRepository, never()).updateCar(any(Car.class)); // Vérifier que updateCar n'a pas été appelé
    }

    @Test
    void returnCar_whenCarExists_shouldCallUpdateCar() {
        // When
        carRentalService.returnCar("XYZ789");
        
        // Then
        assertTrue(unavailableCar.isAvailable()); // Vérifier que la voiture est maintenant disponible
        verify(carRepository).findByRegistrationNumber("XYZ789");
        verify(carRepository).updateCar(unavailableCar); // Vérifier que updateCar a été appelé avec la voiture correcte
    }

    @Test
    void returnCar_whenCarDoesNotExist_shouldNotCallUpdateCar() {
        // When
        carRentalService.returnCar("NON_EXISTENT");
        
        // Then
        verify(carRepository).findByRegistrationNumber("NON_EXISTENT");
        verify(carRepository, never()).updateCar(any(Car.class)); // Vérifier que updateCar n'a pas été appelé
    }

    @Test
    void testSequenceOfOperations() {
        // Spy pour vérifier l'ordre des opérations
        CarRepository orderedSpy = spy(new CarRepository());
        CarRentalService service = new CarRentalService();
        
        // Configuration manuelle du repository (sans @InjectMocks qui est déjà utilisé plus haut)
        try {
            java.lang.reflect.Field field = CarRentalService.class.getDeclaredField("carRepository");
            field.setAccessible(true);
            field.set(service, orderedSpy);
        } catch (Exception e) {
            fail("Failed to set repository: " + e.getMessage());
        }
        
        // Ajout d'une voiture pour le test
        Car testCar = new Car("TEST123", "TestModel", true);
        orderedSpy.addCar(testCar);
        
        // When
        service.rentCar("TEST123");
        
        // Then - Vérification de l'ordre des appels
        InOrder inOrder = inOrder(orderedSpy);
        inOrder.verify(orderedSpy).findByRegistrationNumber("TEST123");
        inOrder.verify(orderedSpy).updateCar(any(Car.class));
    }
}
