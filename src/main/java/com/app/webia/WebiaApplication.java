package com.app.webia;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebiaApplication {
    public static void main(String[] args) {
        Dotenv.configure().ignoreIfMissing().load().entries().forEach(WebiaApplication::loadProperty);
        SpringApplication.run(WebiaApplication.class, args);
    }

    private static void loadProperty(DotenvEntry entry) {
        if (System.getenv(entry.getKey()) == null && System.getProperty(entry.getKey()) == null) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
