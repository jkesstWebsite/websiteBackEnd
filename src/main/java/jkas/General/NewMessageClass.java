package jkas.General;

import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;
import java.sql.Timestamp;

public class NewMessageClass {
    public HttpStatus code;
    public String message;
    public Timestamp timestamp;
    public Integer ttl;
    public Object data;

    // Constructors
    public NewMessageClass(HttpStatus code, String message){
        this.code = code;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.ttl = 1;
        this.data = "";
    }

    public NewMessageClass(HttpStatus code, String message, Object data){
        this.code = code;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.ttl = 1;
        this.data = data;
    }

}
