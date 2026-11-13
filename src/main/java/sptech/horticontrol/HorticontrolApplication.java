package sptech.horticontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class HorticontrolApplication {

	public static void main(String[] args) {
		SpringApplication.run(HorticontrolApplication.class, args);
	}

}
