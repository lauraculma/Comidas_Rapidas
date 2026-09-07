package com.comidarapida.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.dir:src/main/resources/static/img}")
    private String uploadDir;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/admin/usuarios").setViewName("usuarios");
        registry.addViewController("/admin/productos").setViewName("productos");
        registry.addViewController("/perfil").setViewName("perfil");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        String absolute = java.nio.file.Paths.get(path).toAbsolutePath().toString() + "/";
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + absolute)
                .setCachePeriod(0);
    }
}
