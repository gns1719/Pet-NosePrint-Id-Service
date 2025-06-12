package com.example.pet_noseprint_id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
		exclude = {
				DataSourceAutoConfiguration.class}
)public class PetNoseprintIdServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PetNoseprintIdServiceApplication.class, args);
	}

}
