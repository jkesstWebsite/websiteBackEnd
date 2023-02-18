package jkas.User;

import jkas.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jkas.General.NewMessageClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private JdbcTemplate targetdb = new JdbcTemplate();

    private Boolean checkUserExistence(String username){
        String sql = String.format("select * from userdb where username='%s'", username);
        List<Map<String, Object>> result = targetdb.queryForList(sql);

        // check the target user is in the list
        if (result.size() != 0 && result.get(0).get("username").equals(username)){
            return true;
        }
        else{
            return false;
        }
    }

    private String getUsername(String token){
        Map<String, Object> userInfo = JwtUtils.getClaims(token);
        String username = userInfo.get("username").toString();
        return username;
    }

    @RequestMapping("/user/getAllUsers")
    public List<Map<String, Object>> getAllUsers(){
        return targetdb.queryForList("select * from userdb");
    }

    @RequestMapping(value = "/user/register")
    public NewMessageClass register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email){
        // Check whether the username is occupied
        String sql = String.format("select * from userdb where username='%s'", username);
        // Get the result list from the database
        List<Map<String, Object>> userList = targetdb.queryForList(sql);

        // Check if the user is existed. If yes then end the session
        if (userList.size() > 0){
            // means there is an existed user in the database

            // 问题已解决，GitHub issue #2
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "The user is already existed");
        }

        // if not then continue adding the user into the database
        sql = String.format("INSERT INTO userdb (username, status, password, email, auth) VALUES ('%s', %s, '%s', '%s', %s)", username, 1, password, email, 2);
        int affectRowNum = targetdb.update(sql);
        if (affectRowNum > 0){
            // success
            return new NewMessageClass(HttpStatus.OK, "User created successfully");
        }
        else{
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "Technical issue happened when registering user.");
        }

    }

    //TODO: 减少使用 if 和else， 改成下面的样子。全局的代码都应该这么优化保证简洁性。
    @RequestMapping("/user/login")
    public NewMessageClass login(@RequestParam("username") String username, @RequestParam("password") String password){
        // Check whether the user is in the database
        String sql = String.format("select * from userdb where username='%s'", username);
        List<Map<String, Object>> resultList = targetdb.queryForList(sql);

        if (resultList.size() == 0 || !resultList.get(0).get("username").equals(username)){
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "Error");
        }

        if (!resultList.get(0).get("password").equals(password) || (int)resultList.get(0).get("status") != 1){
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "Username and password does not match");

        }

        // create the user info map to generate token
        Map<String, Object> userInfo = resultList.get(0);
        return new NewMessageClass(HttpStatus.OK, "You have been logged in successfully", JwtUtils.createToken(userInfo));

    }

    @RequestMapping("/user/changeUserStatus")
    public NewMessageClass changeUserStatus(@RequestHeader("Authorization") String token, @RequestParam("status") Integer status){
        // Get the username from the token
        String username = getUsername(token);
        // find this user is in the database
        Boolean result = checkUserExistence(username);
        if (!result){
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User does not exist");
        }

        String sql = String.format("update userdb set status=%s where username='%s'", status, username);
        int processResult = targetdb.update(sql);

        if (processResult != 1){
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User status modification failed");
        }
        return new NewMessageClass(HttpStatus.OK, "User status updated successfully");
    }

    @RequestMapping("/user/deleteUser")
    public NewMessageClass deleteUser(@RequestHeader("Authorization") String token){
        // get the target username
        String username = getUsername(token);
        // check the user existence
        Boolean userIsExist = checkUserExistence(username);
        if (userIsExist){
            String sql = String.format("delete from userdb where username='%s'", username);
            int affectLineNum = targetdb.update(sql);
            // verify process
            if (affectLineNum != 0){
                return new NewMessageClass(HttpStatus.OK, "User deleted successfully");
            }
            else{
                return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User didn't deleted successfully");
            }
        }
        else{
            // return that the user does not exist
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User does not exist");
        }
    }

    @RequestMapping("/user/checkPassword")
    public NewMessageClass checkUserPassword(@RequestHeader("Authorization") String token, @RequestParam("password") String userInputPassword){
        // get the target username
        String username = getUsername(token);
        // check user existence
        Boolean userIsExisted = checkUserExistence(username);
        if (userIsExisted){
            // get the password from the database
            String sql = String.format("select * from userdb where username='%s'", username);
            List<Map<String, Object>> result = targetdb.queryForList(sql);
            // Compare the data and return the result
            if (userInputPassword.equals(result.get(0).get("password"))){
                return new NewMessageClass(HttpStatus.OK, "Password is correct");
            }
            else{
                return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "Password is incorrect");
            }
        }
        else{
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User does not exist");
        }
    }

    @RequestMapping("/user/getUserInfo")
    public Map<String, Object> returnUserInfo(@RequestHeader("Authorization") String token){
        // get the target username
        String username = getUsername(token);
        // check user existence
        Boolean existResult = checkUserExistence(username);
        if (existResult){
            // get the data from the database
            String sql = String.format("select * from userdb where username='%s'", username);
            List<Map<String, Object>> result = targetdb.queryForList(sql);
            result.get(0).remove("password");
            Map<String, Object> returnData = new HashMap<>();
            returnData.put("username", result.get(0).get("username"));
            returnData.put("id", result.get(0).get("id"));
            returnData.put("email", result.get(0).get("email"));
            returnData.put("status", result.get(0).get("status"));
            returnData.put("auth", result.get(0).get("auth"));
            // return the data
            return returnData;
        }
        else{
            return null;
        }
    }

    @RequestMapping("/user/modifyUsername")
    public NewMessageClass modifyUsername(@RequestHeader("Authorization") String token, @RequestParam("newUsername") String newUsername){
        // get the target username
        String username = getUsername(token);
        // check existence
        Boolean result = checkUserExistence(username);
        Boolean newNameExist = checkUserExistence(newUsername);
        if (result && !newNameExist){
            // try to modify the username
            String sql = String.format("update userdb set username='%s' where username='%s'", newUsername, username);
            int affectRows = targetdb.update(sql);
            // verify the process
            if (affectRows != 0){
                // if okay then return the result and the new token
                List<Map<String, Object>> userInfo = targetdb.queryForList(String.format("select * from userdb where username='%s'", newUsername));
                String newToken = JwtUtils.createToken(userInfo.get(0));
                return new NewMessageClass(HttpStatus.OK, "Username changed successfully", newToken);
            }
            else{
                return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "Username changed unsuccessfully");
            }
        }
        else{
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User not found or name has been taken");
        }
    }

    @RequestMapping("/user/modifyPassword")
    public NewMessageClass modifyPassword(@RequestHeader("Authorization") String token, @RequestParam("oldPassword") String password, @RequestParam("newPassword") String newPassword){
        // get the target username
        String username = getUsername(token);
        // check existence and password
        Boolean result = checkUserExistence(username);
        if (result){
            String passwdSql = String.format("select * from userdb where username='%s'", username);
            List<Map<String, Object>> targetResult = targetdb.queryForList(passwdSql);
            // verify password
            if (targetResult.get(0).get("password").toString().equals(password)){
                // modify the password
                passwdSql = String.format("update userdb set password='%s' where username='%s'", newPassword, username);
                int affectRows = targetdb.update(passwdSql);
                // verify process
                if (affectRows != 0){
                    // if success then generate a new token for the user
                    List<Map<String, Object>> userInfo = targetdb.queryForList(String.format("select * from userdb where username='%s'", username));
                    String newToken = JwtUtils.createToken(userInfo.get(0));
                    return new NewMessageClass(HttpStatus.OK, "Password modified successfully", newToken);
                }
                else{
                    return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "Password modified unsuccessfully");
                }
            }
            else{
                return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "Password verification failed");
            }
        }
        else{
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User does not exist");
        }
    }


}
