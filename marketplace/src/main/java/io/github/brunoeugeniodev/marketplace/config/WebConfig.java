package io.github.brunoeugeniodev.marketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files salvos no diretório "uploads" do filesystem em /uploads/**
        String userDir = System.getProperty("user.dir");
        Path uploadsPath = Paths.get(userDir, "uploads").toAbsolutePath();
        String uploadsLocation = "file:" + uploadsPath.toString() + "/"; // precisa terminar com '/'

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsLocation);
    }
}
