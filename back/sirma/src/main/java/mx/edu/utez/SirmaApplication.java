package mx.edu.utez;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SirmaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SirmaApplication.class, args);
    }

}

