package zeus.gateserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GateServerApplication {

    public static void main(String[] args) throws Exception {
    	ConfigurableApplicationContext context = SpringApplication.run(GateServerApplication.class, args);    	
    }
}
