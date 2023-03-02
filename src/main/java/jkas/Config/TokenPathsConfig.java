package jkas.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenPathsConfig {

    public static List<String> includePaths = Arrays.asList(
            "",
            ""
    );
    public static List<String> excludePaths = Arrays.asList(
            "/user/login",
            "/user/register",
            "/passage/getPassage"
    );


}
