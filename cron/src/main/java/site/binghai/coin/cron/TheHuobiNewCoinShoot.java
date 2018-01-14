package site.binghai.coin.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.TimeFormat;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * Created by binghai on 2018/1/7.
 * 火币pro 新币快枪手
 * 玩法有毒，谨慎操作
 *
 * @ huobi
 */
@Component
public class TheHuobiNewCoinShoot {
    private final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    @Autowired
    private ApiClient apiClient;
    private static Boolean STOP = Boolean.FALSE; // 仅交易一次，防止反复交易
    private static final String newCoinName = "chat"; // 新币名称
    private static final Long startTimeStamp = 1515909600000L; // 开始时间
    private static final Double saleRate = 1.8; // 限价涨幅

    /**
     * 准备拔枪
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void gunShoot() throws IOException {
        if (STOP) {
            return;
        }

        Symbol symbol = new Symbol(newCoinName, "btc");

        double btcBalance = apiClient.getBtcBalance();
//        double btcBalance = apiClient.getUsdtBalance() * 0.5;
//        double btcBalance = 9999;
        long accoutId = apiClient.getBtcAccountId();

        Double lowestPrice = 0.0;
        do {

            logger.info("账户Id: {}, 已备BTC:{} @ {},x {}", accoutId, btcBalance, TimeFormat.format(startTimeStamp), saleRate);
            lowestPrice = CoinUtils.getLastestPrice4NewShoot(symbol);
        } while (lowestPrice == null || lowestPrice <= 0);

        STOP = Boolean.TRUE;

        logger.info("Price:{}", lowestPrice);
        if (System.currentTimeMillis() < startTimeStamp) {
            return;
        }
//        // 已经上线交易
        Long orderId = apiClient.allOnDealOf(symbol,lowestPrice, btcBalance, accoutId);
        if (orderId != null) {
            saleWhenReady(orderId);
        } else {
            logger.error("梭哈买入订单失败:{}", symbol);
        }
    }

    /**
     * 卖个精光
     */
    private static Boolean QuickSale = Boolean.TRUE;

    private void saleWhenReady(Long orderId) throws IOException {
        apiClient.waitOrderFilled(orderId, order -> {
            try {
                while (QuickSale) {
                    Long sellOrderId = apiClient.sellOrder(order, saleRate);
                    if (sellOrderId != null) { // 创建卖出单成功，退出循环
                        QuickSale = Boolean.FALSE;
                        lookSale(sellOrderId);
                    } else {
                        logger.error("创建卖出订单出错,order :{}", order);
                    }
                }
            } catch (IOException e) {
                logger.error("创建卖出订单出错,order :{}", order, e);
            }
        });
    }

    /**
     * 等待卖出交易完成
     */
    private void lookSale(Long data) {
        try {
            apiClient.waitOrderFilled(data, order -> {
                logger.info("交易完成,orderId:{}", order);
            });
        } catch (IOException e) {
            logger.error("卖出交易出现异常,orderId:{}", data);
        }
    }
}
