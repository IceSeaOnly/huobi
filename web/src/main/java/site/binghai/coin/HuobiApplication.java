package site.binghai.coin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.data.impl.KlineService;

@SpringBootApplication
@ComponentScan("site.binghai.coin.*")
public class HuobiApplication {

    @Autowired
    private KlineService klineService;

    public static void main(String[] args) {
        SpringApplication.run(HuobiApplication.class, args);
    }


}
