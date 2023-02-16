package org.jkas.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.jkas.General.NewMessageClass;

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

    @RequestMapping("/user/login")
    public NewMessageClass login(@RequestParam("username") String username, @RequestParam("password") String password){
        // Check whether the user is in the database
        String sql = String.format("select * from userdb where username='%s'", username);
        List<Map<String, Object>> resultList = targetdb.queryForList(sql);
        if (resultList.size() != 0 && resultList.get(0).get("username").equals(username)){
            // check the password for the user
            if (resultList.get(0).get("password").equals(password) && (int)resultList.get(0).get("status") == 1){
                return new NewMessageClass(HttpStatus.OK, "You have been logged in successfully");
            }
            else{
                return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "Username and password does not match");
            }
        }
        else{
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, resultList.toString());
        }
    }

    @RequestMapping("/user/changeUserStatus")
    public NewMessageClass changeUserStatus(@RequestParam("username") String username, @RequestParam("status") Integer status){
        // find this user is in the database
        Boolean result = checkUserExistence(username);
        if (result){
            String sql = String.format("update userdb set status=%s where username='%s'", status, username);
            int processResult = targetdb.update(sql);
            if (processResult == 1){
                return new NewMessageClass(HttpStatus.OK, "User status updated successfully");
            }
            else{
                return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User status modification failed");
            }
        }
        else{
            return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "User does not exist");
        }
    }

    @RequestMapping("/user/deleteUser")
    public NewMessageClass deleteUser(@RequestParam("username") String username){
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
    public NewMessageClass checkUserPassword(@RequestParam("username") String username, @RequestParam("password") String userInputPassword){
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
    public Map<String, Object> returnUserInfo(@RequestParam("username") String username){
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
    public NewMessageClass modifyUsername(@RequestParam("oldUsername") String username, @RequestParam("newUsername") String newUsername){
        // check existence
        Boolean result = checkUserExistence(username);
        Boolean newNameExist = checkUserExistence(newUsername);
        if (result && !newNameExist){
            // try to modify the username
            String sql = String.format("update userdb set username='%s' where username='%s'", newUsername, username);
            int affectRows = targetdb.update(sql);
            // verify the process
            if (affectRows != 0){
                return new NewMessageClass(HttpStatus.OK, "Username changed successfully");
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
    public NewMessageClass modifyPassword(@RequestParam("username") String username, @RequestParam("oldPassword") String password, @RequestParam("newPassword") String newPassword){
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
                    return new NewMessageClass(HttpStatus.OK, "Password modified successfully");
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
