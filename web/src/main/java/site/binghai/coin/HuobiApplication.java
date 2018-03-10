package site.binghai.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import site.binghai.coin.common.utils.SetupUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
@ComponentScan("site.binghai.coin.*")
public class HuobiApplication {
    public static void main(String[] args) {
        SetupUtils.onSystemStart(args);
        if (SetupUtils.envHasParam("-use:proxy")) {
            System.out.println("Net Proxy Switch Active.");

            String proxyHost = "127.0.0.1";
            String proxyPort = "1080";

            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", proxyPort);
        }
        SpringApplication.run(HuobiApplication.class, args);
    }
}
