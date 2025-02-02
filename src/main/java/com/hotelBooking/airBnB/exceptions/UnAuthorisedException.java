package com.hotelBooking.airBnB.exceptions;

public class UnAuthorisedException extends RuntimeException{

    public UnAuthorisedException(){}

    public UnAuthorisedException(String msg) {
        super(msg);
    }
}
