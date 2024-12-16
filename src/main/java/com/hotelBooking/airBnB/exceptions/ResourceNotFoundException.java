package com.hotelBooking.airBnB.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(){}

    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
