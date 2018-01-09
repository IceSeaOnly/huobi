package site.binghai.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by binghai on 2018/1/8.
 * 分析模块，定时分析
 * @ huobi
 */
@ComponentScan("site.binghai.coin.*")
@SpringBootApplication
@EnableScheduling
public class AnalysisApp {
    public static Set<String> setupArgs = new HashSet<>();

    public static void main(String[] args) {
        setupArgs.addAll(Arrays.asList(args));
        SpringApplication.run(AnalysisApp.class, args);
    }
}
