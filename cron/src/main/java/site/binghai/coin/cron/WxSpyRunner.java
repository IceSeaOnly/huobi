package site.binghai.coin.cron;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.entity.WxSpy;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.SmsNoticeService;
import site.binghai.coin.common.utils.WxNoticeService;
import site.binghai.coin.data.impl.MemberCacheService;
import site.binghai.coin.data.impl.WxSpyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2018/3/2.
 *
 * @ huobi
 */
@Component
public class WxSpyRunner {
    private final Logger logger = LoggerFactory.getLogger(WxSpyRunner.class);

    @Autowired
    private WxNoticeService wxNoticeService;
    @Autowired
    private SmsNoticeService smsNoticeService;
    @Autowired
    private WxSpyService spyService;
    @Autowired
    private MemberCacheService memberCacheService;
    // 缓存当天各类币种的最大最小值
    private ConcurrentHashMap<String, DataBundle> cacheMap = new ConcurrentHashMap<>();


    @Scheduled(cron = "0 * * * * ?")
    public void scan() {
        List<WxSpy> list = spyService.findAll(10000);
        Map<String, List<WxSpy>> spyMap = new HashMap<>();

        list.forEach(v -> {
            String coin = v.getBaseCoin() + "/" + v.getQuoteCoin();
            List<WxSpy> ls = spyMap.get(coin);
            if (CollectionUtils.isEmpty(ls)) {
                ls = new ArrayList<>();
            }
            ls.add(v);
            spyMap.put(coin, ls);
        });

//        memberCacheService.put(MemberCacheService.CacheKeys.WX_SPY_CACHE, spyMap);

        spyMap.forEach((k, v) -> {
            Symbol symbol = new Symbol(v.get(0).getBaseCoin(), v.get(0).getQuoteCoin());
            Kline day = CoinUtils.getKlineList(symbol, KlineTime.DAY, 1).get(0);
            day.setCoinName(symbol.getBaseCurrency());
            day.setQuoteCoinName(symbol.getQuoteCurrency());
            DataBundle today = getTodayDataBundle(day);

            if (today.getStatus() == 0) {
                List<String> openIds = v.stream().map(p -> p.getOpenId()).collect(Collectors.toList());
                wxNoticeService.NewRecordInTheDay(openIds, today.getMax(),today.getMin(),String.valueOf(day.getClose()), k.toUpperCase(), true);
            } else if (today.getStatus() == 1) {
                List<String> openIds = v.stream().map(p -> p.getOpenId()).collect(Collectors.toList());
                wxNoticeService.NewRecordInTheDay(openIds, today.getMax(),today.getMin(),String.valueOf(day.getClose()), k.toUpperCase(), false);
            }
        });

        logger.info("WxSpyRunner complete.");
    }

    private DataBundle getTodayDataBundle(Kline kline) {
        String coinName = kline.getCoinName() + kline.getQuoteCoinName();
        DataBundle dataBundle = cacheMap.get(coinName);
        if (dataBundle == null || dataBundle.getDay() != kline.getId() % 86400) {
            dataBundle = new DataBundle(coinName, kline.getId() % 86400, kline.getHigh(), kline.getLow(),-1);
            cacheMap.put(coinName, dataBundle);
        }

        if (dataBundle.getMax() < kline.getHigh()) {
            dataBundle.setStatus(0);
            dataBundle.setMax(kline.getHigh());
        } else if (dataBundle.getMin() > kline.getLow()) {
            dataBundle.setStatus(1);
            dataBundle.setMin(kline.getLow());
        }else{
            dataBundle.setStatus(-1);
        }

        return dataBundle;
    }
}

@Data
@AllArgsConstructor
class DataBundle {
    private String coinName;
    private long day;
    private double max;
    private double min;
    private int status;
}
