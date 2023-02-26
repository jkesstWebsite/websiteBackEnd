package jkas.Passage;

import jkas.General.NewMessageClass;
import jkas.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class PassageController {

    private JdbcTemplate targetdb;

    private String getUsername(String token){
        if (JwtUtils.checkIsExpired(token) || !JwtUtils.checkIsValid(token)){
            return "Invalid token";
        }
        Map<String, Object> userInfo = JwtUtils.getClaims(token);
        return userInfo.get("username").toString();
    }

    private String getUserID(String token){
        if (JwtUtils.checkIsExpired(token) || !JwtUtils.checkIsValid(token)){
            return "Invalid token";
        }
        Map<String, Object> userInfo = JwtUtils.getClaims(token);
        return userInfo.get("id").toString();
    }


    public Boolean createPassage(String userid, String title, String content){
        targetdb = new JdbcTemplate();
        String sql = String.format("insert into passagedb (title, authorid, date, visible, content) values ('%s', %s, '%s', %s, '%s')", title, userid, LocalDateTime.now(), 1, content);
        System.out.println(sql);
        int affectRowNum = targetdb.update(sql); // 这里报错
        return affectRowNum > 0;
    }

    public Boolean modifyPassage(String title, String content, String passageID){
        String sql = String.format("update passagedb set content='%s' title='%s' where passageid='%s'", content, title, passageID);
        int affectRowNum = targetdb.update(sql);
        return affectRowNum > 0;
    }

}
