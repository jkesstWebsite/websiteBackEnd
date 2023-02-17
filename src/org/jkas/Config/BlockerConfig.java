package org.jkas.Config;

import org.jkas.Interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BlockerConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry){
        // Create instantiates of the interceptors here
        LoginInterceptor loginInterceptor = new LoginInterceptor();

        // Register paths here
        List<String> includePaths = InterceptPathsConfig.includePaths;
        List<String> excludePaths = InterceptPathsConfig.excludePaths;


        // Register interceptors here
        interceptorRegistry.addInterceptor(loginInterceptor)
                .addPathPatterns(includePaths)
                .excludePathPatterns(excludePaths);
    }

}
