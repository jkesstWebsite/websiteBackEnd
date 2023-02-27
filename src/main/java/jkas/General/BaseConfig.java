package jkas.General;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// GLOBAL CONFIGURATION DATA COLLECTOR

@Slf4j
@Component
public class BaseConfig implements ApplicationRunner {

    @Autowired
    private Environment environment;

    public static String dbUrl;
    public static String username;
    public static String password;

    @Override
    public void run(ApplicationArguments args){
        dbUrl = environment.getProperty("spring.datasource.url");
        username = environment.getProperty("spring.datasource.username");
        password = environment.getProperty("spring.datasource.password");
    }

}
