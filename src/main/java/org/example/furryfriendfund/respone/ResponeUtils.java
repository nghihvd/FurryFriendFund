package org.example.furryfriendfund.respone;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponeUtils {
    public static ResponseEntity<BaseRespone> createRespone(String message, Object data, HttpStatus status) {

        return new ResponseEntity<>(BaseRespone.builder()
                .data(data)
                .message(message)
                .status(status.value()).build(),status);
    }

    public static ResponseEntity<BaseRespone> createSuccessRespone(String message, Object data) {
        return createRespone(message, data, HttpStatus.OK);
    }
    public static ResponseEntity<BaseRespone> createErrorRespone(String message, Object data, HttpStatus status) {
        return createRespone(message, data, status);
    }
}
