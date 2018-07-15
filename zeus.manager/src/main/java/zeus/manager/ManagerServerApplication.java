package zeus.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.i5i58,zeus")
public class ManagerServerApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ManagerServerApplication.class, args);
	}
}
