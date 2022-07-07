package com.song.example.web.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RG {

    public static <T> ResponseEntity<T> Response(HttpStatus statucCode, T body) {
        var response = ResponseEntity.status(statucCode);
        return response.body(body);
    }

    public static <T> ResponseEntity<T> Success(T body) {
        var response = ResponseEntity.status(HttpStatus.OK);
        return response.body(body);
    }
    
}
