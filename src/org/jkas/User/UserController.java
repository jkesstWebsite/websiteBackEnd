package org.jkas.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @RequestMapping("/getAllUsers")
    public List<Map<String, Object>> getAllUsers(){
        return targetdb.queryForList("select * from userdb");
    }

    @RequestMapping("/register")
    public MessageClass register(@RequestParam("username") String username, @RequestParam("password") String password){
        // Check whether the username is occupied
        String sql = String.format("select * from userdb where username='%s'", username);
        List<Map<String, Object>> userList = targetdb.queryForList(sql);
        System.out.println(userList.size());
        MessageClass returnMessage = new MessageClass(false, "Not finished");
        if (userList.size() > 0){
            // means there is an existed user in the database
            returnMessage.result = false;
            returnMessage.message = "The user is already existed";
            return returnMessage;
        }

        // if not then continue adding the user into the database
        sql = String.format("INSERT INTO userdb (username, status, password) VALUES ('%s', %s, '%s')", username, 1, password);
        int affectRowNum = targetdb.update(sql);
        if (affectRowNum > 0){
            // success
            returnMessage.result = true;
            returnMessage.message = "User created successfully";
            return returnMessage;
        }
        else{
            returnMessage.result = false;
            returnMessage.message = "Technical issue happened when registering user.";
        }

        return returnMessage;
    }

    @RequestMapping("/login")
    public MessageClass login(@RequestParam("username") String username, @RequestParam("password") String password){
        // Check whether the user is in the database
        String sql = String.format("select * from userdb where username='%s'", username);
        List<Map<String, Object>> resultList = targetdb.queryForList(sql);
        if (resultList.size() != 0 && resultList.get(0).get("username") == username){
            // check the password for the user
            if (resultList.get(0).get("password") == password){
                return new MessageClass(true, "You have been logged in successfully");
            }
            else{
                return new MessageClass(false, "Username and password does not match");
            }
        }
        else{
            return new MessageClass(false, "User does not exist. Please sign up first");
        }

    }
}
