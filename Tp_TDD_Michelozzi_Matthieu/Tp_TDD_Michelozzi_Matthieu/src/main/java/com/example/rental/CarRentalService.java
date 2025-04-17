package com.example.rental;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarRentalService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> getAllCars() {
        return carRepository.getAllCars();
    }

    public boolean rentCar(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        if (car.isPresent() && car.get().isAvailable()) {
            car.get().setAvailable(false);
            carRepository.updateCar(car.get());
            return true;
        }
        return false;
    }

    public void returnCar(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        car.ifPresent(c -> {
            c.setAvailable(true);
            carRepository.updateCar(c);
        });
    }

    /**
     * Add a new car to the repository if its registration number is unique
     * 
     * @param car the car to add
     * @return true if the car was added, false if a car with the same registration number already exists
     */
    public boolean addCar(Car car) {
        Optional<Car> existingCar = carRepository.findByRegistrationNumber(car.getRegistrationNumber());
        if (existingCar.isPresent()) {
            return false;
        }
        carRepository.addCar(car);
        return true;
    }

    /**
     * Get all cars with a specific model
     * 
     * @param model the model to search for
     * @return a list of cars with the specified model
     */
    public List<Car> getCarsByModel(String model) {
        return carRepository.getAllCars().stream()
                .filter(car -> car.getModel().equals(model))
                .collect(Collectors.toList());
    }
}
