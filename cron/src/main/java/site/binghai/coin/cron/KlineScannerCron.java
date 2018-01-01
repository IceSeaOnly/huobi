package site.binghai.coin.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.CronApp;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.data.impl.KlineService;

import java.util.Arrays;
import java.util.List;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@Component
public class KlineScannerCron implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(KlineScannerCron.class);

    private static List<String> coinFilter = Arrays.asList("bt1", "bt2");
    private static boolean InitAllSymbolKline = false; // 系统第一次运行时批量拉取2000条历史数据

    @Autowired
    private KlineService klineService;

    @Scheduled(cron = "0/20 * * * * ?")
    public void scan() {
        List<Symbol> allSymbols = CoinUtils.allSymbols();
        allSymbols.stream()
                .filter(v -> !coinFilter.contains(v.getBaseCurrency()))
                .forEach(v -> {
                    boolean rs = false;
                    while (!rs) {
                        if (InitAllSymbolKline) {
                            rs = load2K(v);
                        } else {
                            rs = loadLastKline(v);
                        }
                    }
                });
        InitAllSymbolKline = false;
    }

    /**
     * 批量拉取2000个记录
     */
    private boolean load2K(Symbol symbol) {
        logger.info("get 2k record of {} {}", symbol.getBaseCurrency(), symbol.getQuoteCurrency());

        try {
            List<Kline> rs = CoinUtils.getKlineList(symbol, KlineTime.MIN1, 2000);
            if (!CollectionUtils.isEmpty(rs)) {
                rs.forEach(v -> {
                    v.setCoinName(symbol.getBaseCurrency());
                    v.setQuoteCoinName(symbol.getQuoteCurrency());
                });
                klineService.batchSave(rs);
                logger.info("{} records for {} {} has written to db successfully.", rs.size(), symbol.getBaseCurrency(), symbol.getQuoteCurrency());
            }
        } catch (Exception e) {
            logger.error("get 2k record of {} {} has errors!", symbol.getBaseCurrency(), symbol.getQuoteCurrency(), e);
            return false;
        }
        return true;
    }

    /**
     * 获取最新k线并写入db
     */
    private boolean loadLastKline(Symbol symbol) {
        logger.info("get lastest record of {} {}", symbol.getBaseCurrency(), symbol.getQuoteCurrency());

        try {
            Kline kline = CoinUtils.getLastestKline(symbol);
            if (kline != null) {
                kline.setCoinName(symbol.getBaseCurrency());
                kline.setQuoteCoinName(symbol.getQuoteCurrency());

                logger.info("new record for {} {} is {}", symbol.getBaseCurrency(), symbol.getQuoteCurrency(), kline);
                klineService.save(kline);
            }
        } catch (Exception e) {
            logger.error("get lastest record of {} {} has errors!", symbol.getBaseCurrency(), symbol.getQuoteCurrency(), e);
            return false;
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CronApp.setupArgs.contains("-ClearKline")) {
            logger.warn("clear db command confirmed!");
            klineService.deleteAll("confirm");
        }

        if (klineService.count() <= 0) {
            InitAllSymbolKline = true;
            logger.warn("first time to start, get 2k record for every coin!");
        }
    }
}
