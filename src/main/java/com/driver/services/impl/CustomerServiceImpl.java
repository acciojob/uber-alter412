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

		Driver driver = null;

		for(Driver x : drivers){
			if(x.getCab().getAvailable()){
				if(driver==null || driver.getDriverId()>x.getDriverId()){
					driver = x;
				}
			}
		}
		if(driver==null){
			throw new Exception("No cab available!");
		}
		TripBooking tripBooking = new TripBooking();
		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		customerRepository2.save(customer);
//		Customer customer = customerRepository2.findById(customerId).orElse(null);
//		if(customer==null){
//			customer = new Customer();
//
//			customerRepository2.save(customer);
//		}
		tripBooking.setCustomer(customer);
		tripBooking.setDriver(driver);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setBill(driver.getCab().getPerKmRate() * distanceInKm);
		tripBooking.getDriver().getCab().setAvailable(false);

		//tripBooking =
		 tripBookingRepository2.save(tripBooking);

//
//		driver.getTripBookingList().add(tripBooking);
//		customer.getTripBookingList().add(tripBooking);
//
//		driverRepository2.save(driver);
////		return  tripBooking;
//		customerRepository2.save(customer);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);
		if (optionalTripBooking.isPresent()){
			TripBooking tripBooking = optionalTripBooking.get();
			tripBooking.setStatus(TripStatus.CANCELED);
			tripBooking.getDriver().getCab().setAvailable(true);
			tripBooking.setBill(0);
			tripBookingRepository2.save(tripBooking);
		}
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);
		if(optionalTripBooking.isPresent()){
			TripBooking tripBooking = optionalTripBooking.get();
			tripBooking.setStatus(TripStatus.COMPLETED);
			tripBooking.getDriver().getCab().setAvailable(true);
			tripBookingRepository2.save(tripBooking);
		}
	}
}
