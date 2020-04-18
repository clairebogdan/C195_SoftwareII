package model;

public class Customer {
    
    String id, name, address, address2, city, zip, country, phone;
    
    public Customer() {}


    //Used for Add Customer (includes all required Strings)
    public Customer(String id, String name, String address, String address2, String city, String zip, String country, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.zip = zip;
        this.country = country;
        this.phone = phone;
    }
    
    
    //Used for Add Appointment table to show ids and names
    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }    
    
    
    
    //Getters and Setters
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getAddress2() {
        return address2;
    }
    
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
