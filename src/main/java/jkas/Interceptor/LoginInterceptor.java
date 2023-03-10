package jkas.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jkas.Config.TokenPathsConfig;
import jkas.jwt.JwtUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.sql.Timestamp;
import java.util.List;

public class LoginInterceptor implements HandlerInterceptor {

    private String generateRespondBody(HttpStatus code, String message) {
        JSONObject targetBody = new JSONObject();
        targetBody.put("code", code);
        targetBody.put("message", message);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        targetBody.put("timestamp", currentTime);
        targetBody.put("ttl", 1);
        targetBody.put("data", "");
        String returnBody = targetBody.toString();
        return returnBody;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Check the target location needs the token to continue
        String uri = request.getRequestURI();
        List<String> needLoginPaths = TokenPathsConfig.includePaths;
        List<String> noNeedLoginPaths = TokenPathsConfig.excludePaths;

        // pass the POTION request
        if (request.getMethod().equals("OPTION")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // Verify token
        if (noNeedLoginPaths.contains(uri)) {
            System.out.println("A port do not need to login with the token");
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // check whether the user have the token
        String token = request.getHeader("Authorization");
        if (token == null) {

            // not allowed to have no token for token needed ports
            System.out.println(" System blocked 1 illegal request");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().append(generateRespondBody(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid token"));
            return false;
        }
        // verify the validation of the token
        Boolean result = JwtUtils.checkIsExpired(token);
        if (result) {
            // passed the check and continue
            // out dated token
            System.out.println(" System blocked 1 illegal request");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().append(generateRespondBody(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid token"));
            return false;
        }

        // verify the user status from the server
        result = JwtUtils.checkIsBanned(token);
        if (result){
            System.out.println(" System blocked 1 illegal request");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().append(generateRespondBody(HttpStatus.NOT_ACCEPTABLE, "Your account has already banned"));
            return false;
        }
        return true;
    }


}
