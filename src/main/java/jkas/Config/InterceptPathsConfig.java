package jkas.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterceptPathsConfig {

    // login interceptor paths configurations
    public static List<String> includePaths = Arrays.asList(
            "/user/**",
            "/passage/editor/util"
    );
    public static List<String> excludePaths = Arrays.asList(
            "/user/register",
            "/user/login"
    );

}
