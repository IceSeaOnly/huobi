package site.binghai.coin.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.defination.CallBack;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.TimeFormat;

import java.io.IOException;
import java.util.List;

/**
 * Created by binghai on 2018/1/7.
 * 火币pro 新币快枪手
 *
 * @ huobi
 */
@Component
public class TheHuobiNewCoinShoot {
    private final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    @Autowired
    private ApiClient apiClient;
    private static Boolean STOP = Boolean.FALSE; // 仅交易一次，防止反复交易
    private static final String newCoinName = "swftc"; // 新币名称
    private static final Long startTimeStamp = 1515391200000L; // 开始时间
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
        long accoutId = apiClient.getBtcAccountId();

        Long ts = 0L;
        do {
            logger.info("未开始,账户Id: {}, 已备BTC:{} ,开始时间: {},限价涨幅: {}",
                    accoutId, btcBalance, TimeFormat.format(startTimeStamp), saleRate);
            ts = CoinUtils.getServerTimestamp(symbol);
        } while (ts != null  && ts < startTimeStamp);

        STOP = Boolean.TRUE;

        // 已经上线交易
        Long orderId = apiClient.allOnDealOf(symbol, btcBalance, accoutId);
        if (orderId != null) {
            saleWhenReady(orderId);
        } else {
            logger.error("梭哈买入订单失败:{}", symbol);
        }
    }

    /**
     * 卖个精光
     */
    private void saleWhenReady(Long orderId) throws IOException {
        apiClient.waitOrderFilled(orderId, order -> {
            try {
                Long sellOrderId = apiClient.sellOrder(order, saleRate);
                if (sellOrderId != null) {
                    lookSale(sellOrderId);
                } else {
                    logger.error("创建卖出订单出错,order :{}", order);
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
