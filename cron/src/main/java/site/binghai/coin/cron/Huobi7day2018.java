package site.binghai.coin.cron;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.defination.CallBack;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.common.request.CreateOrderRequest;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.HttpUtils;

import java.io.IOException;

/**
 * Created by binghai on 2018/1/6.
 * 火币pro 2018 1.1 - 1.7 收割代码
 *
 * @ huobi
 */
//@Component
public class Huobi7day2018 {
    private final Logger logger = LoggerFactory.getLogger(Huobi7day2018.class);
    @Autowired
    private ApiClient apiClient;
    private static double coinAmount = 0.0;

    private static String lastCoin = null;

    /**
     * 线上每小时运行一次 @Scheduled(cron = "0 0 * * * ?")
     * 测试每分钟运行一次 @Scheduled(cron = "0 * * * * ?")
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void TimeWaiter() throws IOException {
        JSONObject resp = HttpUtils.sendJSONGet("https://www.huobi.com/p/api/activity/pro/yd_time", null, null);
        String currency = resp.getJSONObject("data").getString("currency");
        if (lastCoin != null && !lastCoin.equals(currency)) {
            makeDealOf(currency);
        } else {
            logger.info("未到开始时间,已备BTC:{}", apiClient.getSpotBtcBalance());
        }
        lastCoin = currency;
        logger.info("当前火币种类: {}", currency.toUpperCase());
    }

    /**
     * 交易此币大赚
     */
    private void makeDealOf(String coin) throws IOException {
        Symbol symbol = new Symbol(coin, "btc");
        Long orderId = null;//apiClient.allOnDealOf(symbol,0,0);
        if (orderId != null && orderId > 0) {
            logger.warn("创建订单成功! OrderId :{}", orderId);
            apiClient.waitOrderFilled(orderId, new CallBack<HuobiOrder>() {
                @Override
                public void onCallBack(HuobiOrder data) {
                    apiSell(data);
                }
            });
        } else {
            logger.error("创建订单失败 !");
        }
    }

    private void apiSell(HuobiOrder data) {
        try {
            Long sellOrderId = apiClient.sellOrder(data, 1.4);
            if (sellOrderId != null) {
                apiClient.waitOrderFilled(sellOrderId, sellOrder -> {
                    logger.info("交易完成: {}", sellOrder);
                });
            } else {
                logger.error("卖出时出现错误!");
            }
        } catch (IOException e) {
            logger.error("卖出时出现错误!", e);
        }
    }

}
