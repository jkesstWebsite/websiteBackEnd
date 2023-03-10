package jkas.jwt;

import io.jsonwebtoken.*;
import jkas.General.BaseConfig;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

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
            return false;
        }
        catch (ExpiredJwtException | IllegalArgumentException e){
            return true;
        }
    }

    public static Boolean checkIsValid(String token){
        try{
            Claims content = Jwts.parser().setSigningKey(signature).parseClaimsJws(token).getBody();
            return true;
        }
        catch (MalformedJwtException e){
            return false;
        }
    }

    public static Boolean checkIsBanned(String token){
        Boolean isValid = checkIsValid(token);
        if (!isValid){
            return true;
        }
        Map<String, Object> tokenInfo = getClaims(token);
        String sql = String.format("select status from userdb where username='%s'", tokenInfo.get("username").toString());
        JdbcTemplate targetdb = new JdbcTemplate(BaseConfig.returnDataSource());
        List<Map<String, Object>> userInfo = targetdb.queryForList(sql);
        if (userInfo.get(0).get("status").toString().equals("0")){
            return true;
        }
        return false;
    }

    public static Map<String, Object> getClaims(String token){
        // check whether the token is expired

        //TODO: ????????????token is valid ?????????????????????valid???checkisexpired??????????????????false????????????expired?????????????????????????????????
        if (checkIsExpired(token) || !checkIsValid(token)){

            Map<String, Object> returnInfo = new HashMap<>();
            returnInfo.put("Warning", "tokenExpired");
            return returnInfo;
        }


        // TODO: get the info from the token
        try {
            // System.out.println(token);
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
