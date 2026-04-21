package com.smartcampus.exception;


 // Part 5.3 - Thrown when a reading is POSTed to a sensor in MAINTENANCE status.
 // Mapped to HTTP 403 Forbidden by SensorUnavailableExceptionMapper.
 
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
