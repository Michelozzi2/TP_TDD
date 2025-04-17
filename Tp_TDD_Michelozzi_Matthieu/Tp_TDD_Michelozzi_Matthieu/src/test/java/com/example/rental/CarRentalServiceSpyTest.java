package com.example.rental;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

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
        
        doReturn(Optional.of(availableCar)).when(carRepository).findByRegistrationNumber("ABC123");
        doReturn(Optional.of(unavailableCar)).when(carRepository).findByRegistrationNumber("XYZ789");
        doReturn(Optional.empty()).when(carRepository).findByRegistrationNumber("NON_EXISTENT");
    }

    @Test
    void rentCar_whenCarIsAvailable_shouldCallUpdateCar() {
        boolean result = carRentalService.rentCar("ABC123");
        
        assertTrue(result);
        assertFalse(availableCar.isAvailable());
        verify(carRepository).findByRegistrationNumber("ABC123");
        verify(carRepository).updateCar(availableCar);
    }

    @Test
    void rentCar_whenCarIsNotAvailable_shouldNotCallUpdateCar() {
        boolean result = carRentalService.rentCar("XYZ789");
        
        assertFalse(result);
        verify(carRepository).findByRegistrationNumber("XYZ789");
        verify(carRepository, never()).updateCar(any(Car.class));
    }

    @Test
    void rentCar_whenCarDoesNotExist_shouldNotCallUpdateCar() {
        boolean result = carRentalService.rentCar("NON_EXISTENT");
        
        assertFalse(result);
        verify(carRepository).findByRegistrationNumber("NON_EXISTENT");
        verify(carRepository, never()).updateCar(any(Car.class));
    }

    @Test
    void returnCar_whenCarExists_shouldCallUpdateCar() {
        carRentalService.returnCar("XYZ789");
        
        assertTrue(unavailableCar.isAvailable());
        verify(carRepository).findByRegistrationNumber("XYZ789");
        verify(carRepository).updateCar(unavailableCar);
    }

    @Test
    void returnCar_whenCarDoesNotExist_shouldNotCallUpdateCar() {
        carRentalService.returnCar("NON_EXISTENT");
        
        verify(carRepository).findByRegistrationNumber("NON_EXISTENT");
        verify(carRepository, never()).updateCar(any(Car.class));
    }

    @Test
    void testSequenceOfOperations() {
        CarRepository orderedSpy = spy(new CarRepository());
        CarRentalService service = new CarRentalService();
        
        try {
            java.lang.reflect.Field field = CarRentalService.class.getDeclaredField("carRepository");
            field.setAccessible(true);
            field.set(service, orderedSpy);
        } catch (Exception e) {
            fail("Failed to set repository: " + e.getMessage());
        }
        
        Car testCar = new Car("TEST123", "TestModel", true);
        orderedSpy.addCar(testCar);
        
        service.rentCar("TEST123");
        
        InOrder inOrder = inOrder(orderedSpy);
        inOrder.verify(orderedSpy).findByRegistrationNumber("TEST123");
        inOrder.verify(orderedSpy).updateCar(any(Car.class));
    }
}
