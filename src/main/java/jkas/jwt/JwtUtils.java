package jkas.jwt;

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


    /**
     * Check if token expired
     * @param token
     * @return true if token is not expired, false if expired
     */
    public static Boolean checkIsExpired(String token){
        try{
            Date tokenExpireDate = Jwts.parser().setSigningKey(signature).parseClaimsJws(token).getBody().getExpiration();
            return tokenExpireDate.before(new Date());
        }
        catch (ExpiredJwtException | IllegalArgumentException e){
            return false;
        }
    }

    public static Map<String, Object> getClaims(String token){
        // check whether the token is expired

        //TODO: 添加一个token is valid 方法，检验是否valid。checkisexpired方法应该返回false如果没有expired，返回值与方法保持一致
        if (!checkIsExpired(token) || token.equals("") != false){

            Map<String, Object> returnInfo = new HashMap<>();
            returnInfo.put("Warning", "tokenExpired");
            return returnInfo;
        }


        // TODO: get the info from the token
        try {
            System.out.println(token);
            Map<String, Object> targetInfo = Jwts.parser().setSigningKey(signature).parseClaimsJws(token).getBody();
            return targetInfo;
        }
        catch (IllegalArgumentException | MalformedJwtException e){
            Map<String, Object> returnInfo = new HashMap<>();
            returnInfo.put("Warning", "tokenExpired");
            return returnInfo;
        }
    }
}
