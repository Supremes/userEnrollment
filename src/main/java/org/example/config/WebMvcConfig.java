package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.PKCS7MessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new PKCS7MessageConverter());
        log.info("PKCS7MessageConverter registgered: {}", converters);
    }
}
