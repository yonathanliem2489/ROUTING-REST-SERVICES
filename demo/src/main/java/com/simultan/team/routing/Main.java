package com.simultan.team.routing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.simultan.team.routing", "com.simultan.team.libraries"})
public class Main {

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

}