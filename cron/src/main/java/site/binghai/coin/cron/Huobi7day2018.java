package site.binghai.coin.cron;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.client.AuthParams;
import site.binghai.coin.common.request.CreateOrderRequest;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.CommonUtils;
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

    private static String lastCoin = null;

    /**
     * 线上每小时运行一次 @Scheduled(cron = "0 0 * * * ?")
     * 测试每分钟运行一次 @Scheduled(cron = "0 * * * * ?")
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void TimeWaiter() throws IOException {
        JSONObject resp = HttpUtils.sendJSONGet("https://www.huobi.com/p/api/activity/pro/yd_time", null, null);
        String currency = resp.getJSONObject("data").getString("currency");
        if (lastCoin != null && !lastCoin.equals(currency)) {
            makeDealOf(currency);
        } else {
            logger.info("未到开始时间");
        }
        lastCoin = currency;
    }

    /**
     * 交易此币大赚
     */
    private void makeDealOf(String coin) throws IOException {
        double btcBalance = apiClient.getBtcBalance();
        long accountId = apiClient.getBtcAccountId();
        if (btcBalance > 0) {
            CreateOrderRequest orderRequest = new CreateOrderRequest();
            orderRequest.setAccountId(String.valueOf(accountId));
            orderRequest.setSymbol(coin + "btc");
            orderRequest.setType(CreateOrderRequest.OrderType.BUY_MARKET);
            orderRequest.setAmount(String.format("%.2f", btcBalance * 0.98));
            Long orderId = apiClient.createOrder(orderRequest);

            if (orderId != null && orderId > 0) {
                logger.warn("创建订单成功! CreateOrderRequest:{} ,\n订单Id:{}", orderRequest, orderId);
            } else {
                logger.error("创建订单失败 ! CreateOrderRequest:{}", orderRequest);
            }
        }
    }
}
