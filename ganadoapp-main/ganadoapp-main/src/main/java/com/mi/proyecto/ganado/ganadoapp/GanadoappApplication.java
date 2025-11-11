package com.mi.proyecto.ganado.ganadoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GanadoappApplication {

    public static void main(String[] args) {
        SpringApplication.run(GanadoappApplication.class, args);
    }

}

