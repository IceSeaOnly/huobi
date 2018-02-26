package site.binghai.coin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import site.binghai.coin.common.entity.WaterLevelMonitor;
import site.binghai.coin.cron.A_FistfulOfDollars;
import site.binghai.coin.cron.RefreshHistoryOrders;
import site.binghai.coin.cron.WaterLevelMonitorRunner;

@SpringBootApplication
@EnableScheduling
@ComponentScan("site.binghai.coin.*")
public class HuobiApplication implements CommandLineRunner{

    @Autowired
    private WaterLevelMonitorRunner waterLevelMonitorRunner;
    @Autowired
    private RefreshHistoryOrders refreshHistoryOrders;
    @Autowired
    private A_FistfulOfDollars a_fistfulOfDollars;

    public static void main(String[] args) {
        SpringApplication.run(HuobiApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        waterLevelMonitorRunner.run();
        refreshHistoryOrders.refresh();
        a_fistfulOfDollars.work();
    }
}
