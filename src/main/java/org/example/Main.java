package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyStore;

@EnableAutoConfiguration
@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    public static void loadKeyStore() throws Exception {
        char[] password = "nY$CZe7A@KkQpe".toCharArray(); // 替换为你的密码
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ClassPathResource("xmdev.pfx").getInputStream(), password);
        System.out.println("Keystore loaded successfully!");
    }
}