package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

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
		Optional<Customer> optionalCustomer = customerRepository2.findById(customerId);
			Customer customer = optionalCustomer.get();
			TripBooking tripBooking = new TripBooking();
			tripBooking.setStatus(TripStatus.CONFIRMED);
			tripBooking.setCustomer(customer);
			tripBooking.setDistanceInKm(distanceInKm);
			tripBooking.setFromLocation(fromLocation);
			tripBooking.setToLocation(toLocation);
			List<Driver> drivers = driverRepository2.findAll();

			int min = Integer.MAX_VALUE;

			for(Driver x : drivers){
				if(x.getCab().getAvailable()){
					if(x.getDriverId()<min){
						min = x.getDriverId();
					}
				}
			}
			if(min==Integer.MAX_VALUE){
				throw new Exception("No cab available!");
			}
			Optional<Driver> optionalDriver = driverRepository2.findById(min);
				Driver driver = optionalDriver.get();
				tripBooking.setDriver(driver);
				tripBooking.setBill(driver.getCab().getPerKmRate() * distanceInKm);
				customer.getTripBookingList().add(tripBooking);
				driver.getTripBookingList().add(tripBooking);
				return tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);
		if(optionalTripBooking.isPresent()){
			TripBooking tripBooking = optionalTripBooking.get();
			tripBooking.setStatus(TripStatus.CANCELED);
			tripBooking.getCustomer().getTripBookingList().remove(tripBooking);
			tripBooking.getDriver().getTripBookingList().remove(tripBooking);
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
			tripBookingRepository2.save(tripBooking);
		}

	}
}
