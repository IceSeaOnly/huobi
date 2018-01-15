package site.binghai.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("site.binghai.coin.*")
public class HuobiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuobiApplication.class, args);
    }
}
