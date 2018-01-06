package site.binghai.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@ComponentScan("site.binghai.coin.*")
@SpringBootApplication
@EnableScheduling
public class CronApp {
    public static Set<String> setupArgs = new HashSet<>();

    public static void main(String[] args) {
        setupArgs.addAll(Arrays.asList(args));
        SpringApplication.run(CronApp.class, args);
    }
}
