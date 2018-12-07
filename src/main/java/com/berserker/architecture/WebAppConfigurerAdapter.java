package com.berserker.architecture;

import com.berserker.architecture.interceptor.PreventRepeatSubmitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebAppConfigurerAdapter extends WebMvcConfigurerAdapter {
    /**
     * @description         添加拦截器(针对于添加场景和修改场景)
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PreventRepeatSubmitInterceptor()).addPathPatterns("/addScenarioInfo");
        registry.addInterceptor(new PreventRepeatSubmitInterceptor()).addPathPatterns("/modScenarioInfo");
    }
}
