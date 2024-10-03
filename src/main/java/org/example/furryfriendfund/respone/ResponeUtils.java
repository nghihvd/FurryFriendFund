package org.example.furryfriendfund.respone;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponeUtils {
    public static ResponseEntity<BaseResponse> createRespone(String message, Object data, HttpStatus status) {

        return new ResponseEntity<>(BaseResponse.builder()
                .data(data)
                .message(message)
                .status(status.value()).build(),status);

    }

    public static ResponseEntity<BaseResponse> createSuccessRespone(String message, Object data) {
        return createRespone(message, data, HttpStatus.OK);
    }
    public static ResponseEntity<BaseResponse> createErrorRespone(String message, Object data, HttpStatus status) {
        return createRespone(message, data, status);
    }
}
