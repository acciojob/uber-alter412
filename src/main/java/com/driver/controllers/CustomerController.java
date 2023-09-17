package com.driver.controllers;

import com.driver.model.Customer;
import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import com.driver.services.impl.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	@Autowired
	CustomerServiceImpl cs;

	@PostMapping("/register")
	public ResponseEntity<Void> registerCustomer(@RequestBody Customer customer){
		cs.register(customer);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public void deleteCustomer(@RequestParam Integer customerId){
		cs.deleteCustomer(customerId);
	}

	@PostMapping("/bookTrip")
	public ResponseEntity<Integer> bookTrip(@RequestParam Integer customerId, @RequestParam String fromLocation, @RequestParam String toLocation, @RequestParam Integer distanceInKm) throws Exception {
		TripBooking bookedTrip = cs.bookTrip(customerId,fromLocation,toLocation,distanceInKm);
		return new ResponseEntity<>(bookedTrip.getTripBookingId(), HttpStatus.CREATED);
	}

	@DeleteMapping("/complete")
	public void completeTrip(@RequestParam Integer tripId){
		cs.completeTrip(tripId);
	}

	@DeleteMapping("/cancelTrip")
	public ResponseEntity<Integer> cancelTrip(@RequestParam Integer tripId){
		return new ResponseEntity<>(cs.cancelTrip(tripId),HttpStatus.ACCEPTED);
	}
}
