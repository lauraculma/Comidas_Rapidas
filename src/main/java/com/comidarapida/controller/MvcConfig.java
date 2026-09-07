package com.comidarapida.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/admin/usuarios").setViewName("usuarios");
        registry.addViewController("/admin/productos").setViewName("productos");
        registry.addViewController("/vendedor/productos").setViewName("disponibilidad");
        registry.addViewController("/vendedor/venta/nueva").setViewName("nueva-venta");
        registry.addViewController("/perfil").setViewName("perfil");
    }
}
