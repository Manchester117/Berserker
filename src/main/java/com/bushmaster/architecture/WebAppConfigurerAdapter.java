package com.bushmaster.architecture;

import com.bushmaster.architecture.interceptor.PreventRepeatSubmitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebAppConfigurerAdapter extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PreventRepeatSubmitInterceptor()).addPathPatterns("/addScenarioInfo");
        registry.addInterceptor(new PreventRepeatSubmitInterceptor()).addPathPatterns("/modScenarioInfo");
    }
}
