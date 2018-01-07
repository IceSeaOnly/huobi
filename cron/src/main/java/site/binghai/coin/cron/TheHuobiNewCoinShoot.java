package site.binghai.coin.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.defination.CallBack;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by binghai on 2018/1/7.
 * 火币pro 新币快枪手
 *
 * @ huobi
 */
public class TheHuobiNewCoinShoot {
    private final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    @Autowired
    private ApiClient apiClient;

    /**
     * 准备拔枪
     */
    @Scheduled(cron = "0 * * * * ?")
    public void gunShoot() throws IOException {
        Symbol symbol = new Symbol("powr", "btc");

        List<Kline> result = null;
        do {
            result = CoinUtils.getKlineList(symbol, KlineTime.MIN1, 1);
        } while (CollectionUtils.isEmpty(result));

        // 已经上线交易
        Long orderId = apiClient.allOnDealOf(symbol);
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
                Long sellOrderId = apiClient.sellOrder(order, 1.5);
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
            apiClient.waitOrderFilled(data, new CallBack<HuobiOrder>() {
                @Override
                public void onCallBack(HuobiOrder data) {
                    logger.info("交易完成,orderId:{}", data);
                }
            });
        } catch (IOException e) {
            logger.error("卖出交易出现异常,orderId:{}", data);
        }
    }
}
