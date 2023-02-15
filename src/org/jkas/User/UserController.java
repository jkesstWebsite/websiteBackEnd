package org.jkas.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.jkas.General.MessageClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private JdbcTemplate targetdb = new JdbcTemplate();

    private Object getValue(List<Map<String, Object>> targetList, String targetKey){
        return targetList.get(0).get(targetKey);
    }

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

    @RequestMapping("/user/register")
    public MessageClass register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email){
        // Check whether the username is occupied
        String sql = String.format("select * from userdb where username='%s'", username);
        // Get the result list from the database
        List<Map<String, Object>> userList = targetdb.queryForList(sql);

        // Check if the user is existed. If yes then end the session
        if (userList.size() > 0){
            // means there is an existed user in the database
            return new MessageClass(false, "The user is already existed");
        }

        // if not then continue adding the user into the database
        sql = String.format("INSERT INTO userdb (username, status, password, email) VALUES ('%s', %s, '%s', '%s')", username, 1, password, email);
        int affectRowNum = targetdb.update(sql);
        if (affectRowNum > 0){
            // success
            return new MessageClass(true, "User created successfully");
        }
        else{
            return new MessageClass(false, "Technical issue happened when registering user.");
        }

    }

    @RequestMapping("/user/login")
    public MessageClass login(@RequestParam("username") String username, @RequestParam("password") String password){
        // Check whether the user is in the database
        String sql = String.format("select * from userdb where username='%s'", username);
        List<Map<String, Object>> resultList = targetdb.queryForList(sql);
        if (resultList.size() != 0 && resultList.get(0).get("username").equals(username)){
            // check the password for the user
            if (resultList.get(0).get("password").equals(password) && (int)resultList.get(0).get("status") == 1){
                return new MessageClass(true, "You have been logged in successfully");
            }
            else{
                return new MessageClass(false, "Username and password does not match");
            }
        }
        else{
            return new MessageClass(false, resultList.toString());
        }
    }

    @RequestMapping("/user/changeUserStatus")
    public MessageClass changeUserStatus(@RequestParam("username") String username, @RequestParam("status") Integer status){
        // find this user is in the database
        Boolean result = checkUserExistence(username);
        if (result){
            String sql = String.format("update userdb set status=%s where username='%s'", status, username);
            int processResult = targetdb.update(sql);
            if (processResult == 1){
                return new MessageClass(true, "User status updated successfully");
            }
            else{
                return new MessageClass(false, "User status modification failed");
            }
        }
        else{
            return new MessageClass(false, "User does not exist");
        }
    }


}
