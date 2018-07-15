package zeus.live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@ComponentScan(basePackages="com.i5i58,zeus")
public class LiveServerApplication {	
    public static void main(String[] args) throws Exception {
    	SpringApplication.run(LiveServerApplication.class, args);
    }
}
