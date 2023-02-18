package main.jkas.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterceptPathsConfig {

    public static List<String> includePaths = Arrays.asList(
            "/user/**",
            ""
    );
    public static List<String> excludePaths = Arrays.asList(
            "/user/register",
            "/user/login"
    );

}
