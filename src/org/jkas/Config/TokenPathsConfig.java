package org.jkas.Config;

import java.util.ArrayList;

public class TokenPathsConfig {

    public static final ArrayList<String> includePaths = new ArrayList<>(){
        {

        }
    };
    public static final ArrayList<String> excludePaths = new ArrayList<>(){
        {
            excludePaths.add("/user/login");
            excludePaths.add("/user/register");
        }
    };


}
