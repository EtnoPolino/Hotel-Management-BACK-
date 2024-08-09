package com.etnopolino.HotelServer.exceptions;

public class SpringHotelException extends RuntimeException{
    public SpringHotelException(String exceptionMessage){
        super(exceptionMessage);
    }

    public SpringHotelException(String exMessage, Exception exception){
        super(exMessage, exception);
    }
}
