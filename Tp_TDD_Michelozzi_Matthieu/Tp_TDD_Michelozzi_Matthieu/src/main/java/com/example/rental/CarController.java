package com.example.rental;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarRentalService carRentalService;

    @GetMapping
    public List<Car> getAllCars() {
        return carRentalService.getAllCars();
    }

    @PostMapping("/rent/{registrationNumber}")
    public boolean rentCar(@PathVariable String registrationNumber) {
        return carRentalService.rentCar(registrationNumber);
    }

    @PostMapping("/return/{registrationNumber}")
    public void returnCar(@PathVariable String registrationNumber) {
        carRentalService.returnCar(registrationNumber);
    }

    /**
     * Add a new car
     * 
     * @param car the car to add
     * @return true if the car was added, false if a car with the same registration number already exists
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean addCar(@RequestBody Car car) {
        boolean added = carRentalService.addCar(car);
        if (!added) {
            throw new CarRegistrationNumberAlreadyExistsException();
        }
        return true;
    }

    /**
     * Search cars by model
     * 
     * @param model the model to search for
     * @return a list of cars with the specified model
     */
    @GetMapping("/search")
    public List<Car> searchCarsByModel(@RequestParam String model) {
        return carRentalService.getCarsByModel(model);
    }

    /**
     * Exception handler for duplicate registration number
     */
    @ExceptionHandler(CarRegistrationNumberAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public boolean handleCarRegistrationNumberAlreadyExistsException() {
        return false;
    }

    /**
     * Exception for duplicate registration number
     */
    public static class CarRegistrationNumberAlreadyExistsException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
