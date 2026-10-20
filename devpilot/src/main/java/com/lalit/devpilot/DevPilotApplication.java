package com.lalit.devpilot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DevPilotApplication {

    @Bean
CommandLineRunner printEntities(
        jakarta.persistence.EntityManagerFactory emf) {

    return args -> emf.getMetamodel()
            .getEntities()
            .forEach(entity ->
                    System.out.println(
                            "ENTITY FOUND: " + entity.getName()
                    ));
}
    public static void main(String[] args) {
        SpringApplication.run(DevPilotApplication.class, args);
    }
}
