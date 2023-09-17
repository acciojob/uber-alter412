package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Optional<Customer> optionalCustomer = customerRepository2.findById(customerId);
		if(optionalCustomer.isPresent()){
			customerRepository2.delete(optionalCustomer.get());
		}
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> drivers = driverRepository2.findAll();


		int min = Integer.MAX_VALUE;

		for(Driver x : drivers){
			if(x.getCab().getAvailable()){
				if(x.getDriverId()<=min){
					min = x.getDriverId();
				}
			}
		}
		if(min==Integer.MAX_VALUE){
			throw new Exception("No cab available!");
		}
		TripBooking tripBooking = new TripBooking();
		Optional<Customer> optionalCustomer = customerRepository2.findById(customerId);
		if(optionalCustomer.isPresent()){
			Customer customer = optionalCustomer.get();
			tripBooking.setCustomer(customer);
			customer.getTripBookingList().add(tripBooking);
		}else{
			Customer customer = new Customer();
			customer.setCustomerId(customerId);
			Customer savedCustomer = customerRepository2.save(customer);
			savedCustomer.getTripBookingList().add(tripBooking);
			tripBooking.setCustomer(savedCustomer);
		}
		Optional<Driver> optionalDriver = driverRepository2.findById(min);
		Driver driver;
		if(optionalDriver.isPresent()){
			 driver = optionalDriver.get();
			tripBooking.setDriver(driver);
		}else{
			driver = new Driver();
			driver.setDriverId(min);
			Cab cab = new Cab();
			cab.setAvailable(true);
			driver.setCab(cab);
			Driver driver1=driverRepository2.save(driver);
			tripBooking.setDriver(driver1);
		}

		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setBill(driver.getCab().getPerKmRate() * distanceInKm);
		driver.getTripBookingList().add(tripBooking);

		return tripBookingRepository2.save(tripBooking);
	}

	@Override
	public int cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);
		if (optionalTripBooking.isPresent()){
			TripBooking tripBooking = optionalTripBooking.get();
			tripBooking.setStatus(TripStatus.CANCELED);
			tripBookingRepository2.save(tripBooking);
		}
		return 100;
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);
		if(optionalTripBooking.isPresent()){
			TripBooking tripBooking = optionalTripBooking.get();
			tripBooking.setStatus(TripStatus.COMPLETED);
			tripBookingRepository2.save(tripBooking);
		}
	}
}
