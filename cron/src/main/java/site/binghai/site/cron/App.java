package site.binghai.site.cron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan("site.binghai.coin.*")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
