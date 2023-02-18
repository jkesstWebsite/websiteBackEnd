package jkas.jwt;

import java.util.HashMap;
import java.util.Map;

public class JwtUtilsUnitTest {

    public static void main(String[] args) {
        Map<String, Object> userInfo = new HashMap<>(){
            {
                put("username", "kimsse");
                put("password", "12345678");
                put("id", 0);
                put("auth", 0);
            }
        };

        String token = JwtUtils.createToken(userInfo);
        System.out.println(token);

        // parse the token
        Map<String, Object> parsedUserInfo = new HashMap<>(){
            {}
        };

        parsedUserInfo = JwtUtils.getClaims(token);
        System.out.println(parsedUserInfo);
    }

}
