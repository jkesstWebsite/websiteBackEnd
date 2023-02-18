package jkas.User;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class ports {

    @RequestMapping("/login1")
    public String userLogin(String username, String passwd){
        return "[Login] Username is: " + username + " and the password is " + passwd;
    }

    @RequestMapping("/signup1")
    public String userSignUp(String username, String passwd){
        return "[SignUp] Username is: " + username + " and the password is " + passwd;
    }

}
