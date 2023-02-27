package jkas.Passage;

import jakarta.websocket.server.PathParam;
import jkas.General.BaseConfig;
import jkas.General.NewMessageClass;
import jkas.jwt.JwtUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@Component
@Service
public class PassageController {


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

    private BasicDataSource returnDataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(BaseConfig.dbUrl);
        dataSource.setUsername(BaseConfig.username);
        dataSource.setPassword(BaseConfig.password);
        System.out.println(BaseConfig.dbUrl);
        return dataSource;
    }


    public Boolean createPassage(String userid, String title, String content){
        try{
            String sql = String.format("insert into passagedb (title, authorid, date, visible, content) values ('%s', %s, '%s', %s, '%s')", title, userid, LocalDateTime.now(), 1, content);
            JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());
            int affectRowNum = targetdb.update(sql);
            return affectRowNum > 0;
        }
        catch (Exception e){
            return false;
        }

    }

    public Boolean modifyPassage(String title, String content, String passageID){
        try{
            String sql = String.format("update passagedb set content='%s' title='%s' where passageid='%s'", content, title, passageID);
            JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());
            int affectRowNum = targetdb.update(sql);
            return affectRowNum > 0;
        }
        catch (Exception e){
            return false;
        }

    }

    public Boolean deletePassage(String id){
        try{
            String sql = String.format("delete from passagedb where id='%s'", id);
            JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());
            int affectRowNum = targetdb.update(sql);
            return affectRowNum > 0;
        }
        catch (Exception e){
            return false;
        }

    }

    // Normal POST methods handlers
    @RequestMapping("/passage/getAllByUser")
    public NewMessageClass getAllPassageByUser(@RequestHeader("Authorization") String token){
        try{
            String userID = JwtUtils.getClaims(token).get("id").toString();
            String sql = String.format("select * from passagedb where authorid=%s", userID);
            JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());
            List<Map<String, Object>> result = targetdb.queryForList(sql);
            return new NewMessageClass(HttpStatus.OK, "Passage found", result);
        }
        catch (Exception e){
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @RequestMapping("/passage/read/{passageID}")
    public NewMessageClass getTargetPassage(@PathParam("passageID") String passageID){
        try{
            String sql = String.format("select * from passagedb where id=%s", passageID);
            JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());
            List<Map<String, Object>> result = targetdb.queryForList(sql);
            return new NewMessageClass(HttpStatus.OK, "Passage found", result.get(0));
        }
        catch (Exception e){
            return new NewMessageClass(HttpStatus.NOT_FOUND, "Passage Not Found");
        }
    }

}
