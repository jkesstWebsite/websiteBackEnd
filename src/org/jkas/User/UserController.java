package org.jkas.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private JdbcTemplate targetdb = new JdbcTemplate();

    @RequestMapping("/getAllUsers")
    public List<Map<String, Object>> getAllUsers(){
        return targetdb.queryForList("select * from userdb");
    }

}
