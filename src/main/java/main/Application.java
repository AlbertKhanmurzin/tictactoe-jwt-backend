package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"main", "di", "domain", "datasource", "web"})
@EnableJpaRepositories(basePackages = "datasource.repositoryimpl")
@EntityScan(basePackages = "datasource.entity")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}