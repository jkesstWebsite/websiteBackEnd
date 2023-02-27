package jkas.Passage;

import jkas.General.BaseConfig;
import jkas.jwt.JwtUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@Component
@Service
public class PassageController {

//    @Autowired
//    private static JdbcTemplate targetdb = new JdbcTemplate();
    // Get the information from the config file


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
        String sql = String.format("insert into passagedb (title, authorid, date, visible, content) values ('%s', %s, '%s', %s, '%s')", title, userid, LocalDateTime.now(), 1, content);
        System.out.println(sql);
        JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());
        int affectRowNum = targetdb.update(sql); // 这里报错
        return affectRowNum > 0;
    }

    public Boolean modifyPassage(String title, String content, String passageID){
        String sql = String.format("update passagedb set content='%s' title='%s' where passageid='%s'", content, title, passageID);
        JdbcTemplate targetdb = new JdbcTemplate(returnDataSource());
        int affectRowNum = targetdb.update(sql);
        return affectRowNum > 0;
    }

}
