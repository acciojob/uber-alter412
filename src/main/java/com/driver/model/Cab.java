package com.driver.model;


import javax.persistence.*;

@Entity
public class Cab{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int cabId;

    int perKmRate;

    boolean available;

    @OneToOne
    @JoinColumn
    Driver driver;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Cab() {
    }

    public Cab(int cabId, int perKmRate, boolean available) {
        this.cabId = cabId;
        this.perKmRate = perKmRate;
        this.available = available;
    }

    public int getCabId() {
        return cabId;
    }

    public void setCabId(int cabId) {
        this.cabId = cabId;
    }

    public int getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(int perKmRate) {
        this.perKmRate = perKmRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}