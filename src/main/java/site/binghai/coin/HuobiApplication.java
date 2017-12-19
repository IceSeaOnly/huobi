package site.binghai.coin;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import site.binghai.coin.client.ApiClient;
import site.binghai.coin.entity.CoinBalance;
import site.binghai.coin.response.Account;

import java.util.List;

import static java.lang.Thread.sleep;

@SpringBootApplication
public class HuobiApplication implements CommandLineRunner {
    @Autowired
    private ApiClient client;

    public static void main(String[] args) {
        SpringApplication.run(HuobiApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        Runnable runnable = () -> {
            try {
                while (true) {
                    Account myAccount = client.getAccounts().get(0);
                    CoinBalance balance = client.allMyBlance(myAccount.getId());
                    balance.removeEmptyCoin().printAllCoins();
                    sleep(10000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }
}
