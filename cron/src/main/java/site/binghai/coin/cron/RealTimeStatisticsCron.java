package site.binghai.coin.cron;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.entity.JinSeNew;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.CommonUtils;
import site.binghai.coin.data.impl.JinSeNewService;
import site.binghai.coin.data.impl.MemberCacheService;

import java.io.IOException;
import java.util.List;

import static site.binghai.coin.data.impl.MemberCacheService.CacheKeys.REAL_TIME_STATISTICS;

/**
 * Created by IceSea on 2018/3/22.
 * GitHub: https://github.com/IceSeaOnly
 */
@Component
@Log4j
public class RealTimeStatisticsCron {

    @Autowired
    private MemberCacheService memberCacheService;
    @Autowired
    private JinSeNewService jinSeNewService;
    @Autowired
    private ApiClient apiClient;

    @Scheduled(cron = "0 * * * * ? ")
    public void start() {
        log.info("real time start");
        JSONObject resp = new JSONObject();

        List<Symbol> symbolList = CoinUtils.allSymbols();
        if (CollectionUtils.isEmpty(symbolList)) {
            return;
        }

        int risedCoinCounts = 0;
        int coints = 0;
        double maxRiseRange = 0.0;
        double sumRiseRange = 0.0;

        for (Symbol symbol : symbolList) {
            List<Kline> ls = CoinUtils.getKlineList(symbol, KlineTime.DAY, 1);
            if (!CollectionUtils.isEmpty(ls)) {
                Kline k = ls.get(0);
                coints++;
                risedCoinCounts += k.getClose() > k.getOpen() ? 1 : 0;
                double rise = k.getClose() / k.getOpen();
                sumRiseRange += rise;
                maxRiseRange = Math.max(rise, maxRiseRange);
            }
        }


        List<JinSeNew> news = jinSeNewService.findTodayNews();
        int evnGoodPoints = 0;
        int evnBadPoints = 0;

        for (JinSeNew jinSeNew : news) {
            evnGoodPoints += jinSeNew.getUp_counts();
            evnBadPoints += jinSeNew.getDown_counts();
        }
        resp.put("risedCoinCounts", risedCoinCounts);
        resp.put("allCoinCounts", coints);
        resp.put("avgRiseRange", String.format("%.4f", (sumRiseRange / coints - 1) * 100) + "%");
        resp.put("maxRiseRange", String.format("%.4f", (maxRiseRange - 1) * 100) + "%");
        double btcSum = getAccountBtcSum();
        resp.put("accountBtcSum", CommonUtils.doubleSubCut(btcSum, 5));
        resp.put("accountRmbSum", getAccountRmbSum(btcSum));
        resp.put("evnGoodPoints", evnGoodPoints);
        resp.put("evnBadPoints", evnBadPoints);

        memberCacheService.put(REAL_TIME_STATISTICS, resp);
        log.info("RealTimeStatisticsCron end.");
    }

    private String getAccountRmbSum(double btcSum) {
        if (btcSum < 0) return "0.0";
        return CommonUtils.doubleSubCut(CoinUtils.btc2rmb() * btcSum, 5);
    }

    private double getAccountBtcSum() {
        double rs = -1.0;
        try {
            rs = apiClient.getBtcBalance();
        } catch (IOException e) {
            log.error("getBtcBalance TimeOut Exception.");
        }
        return rs;
    }
}
