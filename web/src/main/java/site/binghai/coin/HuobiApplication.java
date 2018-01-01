package site.binghai.coin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.entity.AccountBalance;
import site.binghai.coin.common.response.Account;

import java.util.List;

import static java.lang.Thread.sleep;

@SpringBootApplication
@EnableScheduling
@ComponentScan("site.binghai.coin.*")
public class HuobiApplication {
    @Autowired
    private ApiClient client;

    public static void main(String[] args) {
        SpringApplication.run(HuobiApplication.class, args);
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void lookAccounts() throws Exception {
        double rmb = 0;
        List<Account> accounts = client.getAccounts();
        for (Account account : accounts) {
            AccountBalance balance = client.accountBlance(account.getId());
            rmb += balance.removeEmptyCoin().printAllCoins();
        }
        System.out.println("【实时】总价值RMB约: " + rmb);
        System.out.println("-----------------------------------------------------------");
        sleep(10000);
    }
}
