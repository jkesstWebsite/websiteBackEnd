package jkas.Passage;

import jakarta.websocket.server.PathParam;
import jkas.General.BaseConfig;
import jkas.General.NewMessageClass;
import jkas.jwt.JwtUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Component
@Service
public class PassageController {

    @Autowired
    JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());

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

    @RequestMapping("/passage/createPassage")
    public NewMessageClass createPassage(@RequestHeader("Authorization") String token, @RequestParam("title") String title, @RequestParam("content") String content){
        try{
            String passageAuthorID = getUserID(token);
            Date currentDate = new Date();
            String sql = String.format("insert into passagedb (title, authorid, date, visible, content) values ('%s', %s, '%s', 1, '%s')", title, passageAuthorID, LocalDateTime.now(), content);
            int affectRow = targetdb.update(sql);
            if (affectRow > 0){
                // get the passage id
                return new NewMessageClass(HttpStatus.OK, "Passage upload successfully!");
            }
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
        }
        catch (Exception e){
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e.getMessage());
        }
    }

    @RequestMapping("/passage/modifyPassage")
    public NewMessageClass modifyPassage(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("passageID") String passageID){
        try{
            String sql = String.format("update passagedb set title='%s', content='%s' where id=%s", title, content, passageID);
            int affectRows = targetdb.update(sql);
            if (affectRows > 0){
                return new NewMessageClass(HttpStatus.OK, "Passage updated successfully!");
            }
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Database Error");
        }
        catch (Exception e){
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @RequestMapping("/passage/deletePassage")
    public NewMessageClass deletePassage(@RequestParam("passageID") String id){
        try{
            String sql = String.format("delete from passagedb where id=%s", id);
            int affectRows = targetdb.update(sql);
            if (affectRows > 0){
                return new NewMessageClass(HttpStatus.OK, "Passage deleted successfully");
            }
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
        }
        catch (Exception e){
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @RequestMapping("/passage/changePassageStatus")
    public NewMessageClass changePassageStatus(@RequestParam("passageID") String id, @RequestParam("status") Integer status){
        try{
            String sql = String.format("update passagedb set visible=%s where id=%s", status, id);
            int affectRows = targetdb.update(sql);
            if (affectRows > 0){
                return new NewMessageClass(HttpStatus.OK, "Passage status changed successfully");
            }
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Database Error");
        }
        catch (Exception e){
            return new NewMessageClass(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
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

    @RequestMapping("/passage/getPassage/{passageID}")
    public NewMessageClass getTargetPassage(@PathVariable("passageID") String passageID){
        try{
            String sql = String.format("select * from passagedb where id=%s", passageID);
            JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());
            List<Map<String, Object>> result = targetdb.queryForList(sql);
            // verify the validation
            if (result.get(0).get("visible").toString().equals("0")){
                return new NewMessageClass(HttpStatus.NOT_ACCEPTABLE, "This passage is not visible currently.");
            }
            return new NewMessageClass(HttpStatus.OK, "Passage found", result.get(0));
        }
        catch (Exception e){
            return new NewMessageClass(HttpStatus.NOT_FOUND, "Passage Not Found");
        }
    }

}
