package org.jkas.Config;

import java.util.ArrayList;

public class InterceptPathsConfig {

    public static final ArrayList<String> includePaths = new ArrayList<>(){
        {
            includePaths.add("/user/**");
        }
    };
    public static final ArrayList<String> excludePaths = new ArrayList<>(){
        {
            excludePaths.add("/user/register");
            excludePaths.add("/user/login");
        }
    };

}
