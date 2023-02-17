package org.jkas.jwt;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtils {

    private final static String signature = "iamcool";
    private final static long time = 1000L *60*60*24*30; // provide a time with 30 days expire

    public static String createToken(Map<String, Object> userInfo){
        JwtBuilder jwtBuilder = Jwts.builder();
        // Get the basic user info from the data
        String username = userInfo.get("username").toString();
        String password = userInfo.get("password").toString();
        String id = userInfo.get("id").toString();
        String userLevel = userInfo.get("auth").toString();

        // Generate the token for the user
        String targetToken = jwtBuilder.setHeaderParam("typ", "JWT")
                .setHeaderParam("alg","HS256")
                .claim("username", username)
                .claim("password", password)
                .claim("id", id)
                .claim("auth", userLevel)
                .setSubject("User login token")
                .setExpiration(new Date(System.currentTimeMillis() + time))
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, signature)
                .compact();

        // Generate the token and return
        return targetToken;
    }

    public static Boolean checkIsExpired(String token){
        try{
            Date tokenExpireDate = Jwts.parser().setSigningKey(signature).parseClaimsJws(token).getBody().getExpiration();
            return tokenExpireDate.before(new Date());
        }
        catch (ExpiredJwtException e){

            return false;

        }
    }

    public static Map<String, Object> getClaims(String token){
        // check whether the token is expired
        Boolean result = checkIsExpired(token);
        if (!result){
            // TODO: get the info from the token
            Map<String, Object> targetInfo = Jwts.parser().setSigningKey(signature).parseClaimsJws(token).getBody();
            return targetInfo;
        }
        else{
            Map<String, Object> returnInfo = new HashMap<>();
            returnInfo.put("Warning", "tokenExpired");
            return returnInfo;
        }
    }
}
