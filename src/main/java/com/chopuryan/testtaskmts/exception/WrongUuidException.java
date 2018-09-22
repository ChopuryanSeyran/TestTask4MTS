package com.chopuryan.testtaskmts.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongUuidException extends RuntimeException {

    public WrongUuidException() {
        super("Wrong UUID");
    }
}
