package site.binghai.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import site.binghai.coin.common.utils.SetupUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@ComponentScan("site.binghai.coin.*")
@EnableScheduling
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class CronApp {
    public static Set<String> setupArgs = new HashSet<>();

    public static void main(String[] args) throws Exception {
        SetupUtils.onSystemStart(args, Arrays.asList("-interval", "-gitScript","-rootPath"));

        if (SetupUtils.envHasParam("-use:proxy")) {
            System.out.println("Net Proxy Switch Active.");

            String proxyHost = "127.0.0.1";
            String proxyPort = "1080";

            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", proxyPort);
        }
        SpringApplication.run(CronApp.class, args);
    }

}
