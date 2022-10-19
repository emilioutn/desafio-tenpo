package com.exampletenpo.calculate.config;

import com.exampletenpo.calculate.interceptor.ContextInterceptor;
import com.exampletenpo.calculate.service.HistoryService;
import com.exampletenpo.calculate.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

/**
 * Configure the converters to use the ISO format for dates by default.
 */
@Configuration
public class WebMvcConfigureImpl implements WebMvcConfigurer {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private HistoryService historyService;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer problemObjectMapperModules() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.modules(
                new ProblemModule(),
                new ConstraintViolationProblemModule()
        );
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ContextInterceptor(securityService, historyService));
    }
}
