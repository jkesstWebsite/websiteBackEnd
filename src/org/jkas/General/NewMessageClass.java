package org.jkas.General;

import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;
import java.sql.Timestamp;

public class NewMessageClass {
    private HttpStatus code;
    private String message;
    private Timestamp timestamp;
    private Integer ttl;
    private Object data;

    // Constructors
    public NewMessageClass(HttpStatus code, String message){
        this.code = code;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.ttl = 1;
        this.data = new Object();
    }

}
