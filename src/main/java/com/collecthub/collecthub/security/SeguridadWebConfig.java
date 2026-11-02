package com.collecthub.collecthub.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Aparte de ProjectConfig para no tocar el archivo compartido
@Configuration
public class SeguridadWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new EmpresaAccesoInterceptor())
                .addPathPatterns("/tienda/*/producto/**");
    }
}