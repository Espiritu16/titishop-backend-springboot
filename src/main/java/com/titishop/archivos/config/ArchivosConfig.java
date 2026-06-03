package com.titishop.archivos.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(ArchivosProperties.class)
public class ArchivosConfig implements WebMvcConfigurer {

	private final ArchivosProperties properties;

	public ArchivosConfig(ArchivosProperties properties) {
		this.properties = properties;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String uploadLocation = properties.dir().toAbsolutePath().normalize().toUri().toString();
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(uploadLocation.endsWith("/") ? uploadLocation : uploadLocation + "/");
	}
}
