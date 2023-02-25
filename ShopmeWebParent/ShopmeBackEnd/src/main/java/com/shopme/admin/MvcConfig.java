package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String dirName = "user-photos";
		Path userPhotosDir = Paths.get(dirName);

		String catDirName = "categories-images";
		Path categoryPhotosDir = Paths.get(catDirName);

		String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();
		String categoryPhotosPath = categoryPhotosDir.toFile().getAbsolutePath();
		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotosPath + "/");
		registry.addResourceHandler("/"+catDirName+"/**").addResourceLocations("file:/" + categoryPhotosPath + "/");
	}



}
