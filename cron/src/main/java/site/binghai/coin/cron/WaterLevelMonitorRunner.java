package site.binghai.coin.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.entity.WaterLevelMonitor;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.CommonUtils;
import site.binghai.coin.common.utils.SmsNoticeService;
import site.binghai.coin.common.utils.TimeFormat;
import site.binghai.coin.data.impl.WaterLevelMonitorService;

import java.util.List;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@Component
@EnableScheduling
public class WaterLevelMonitorRunner {
    private final Logger logger = LoggerFactory.getLogger(WaterLevelMonitorRunner.class);

    @Autowired
    private WaterLevelMonitorService waterLevelMonitorService;
    @Autowired
    private SmsNoticeService noticeService;

    @Scheduled(cron = "0 * * * * ?")
    public void run() {
        List<WaterLevelMonitor> monitorList = waterLevelMonitorService.findAllNotComplete();
        monitorList.forEach(v -> {
            Symbol symbol = new Symbol(v.getBaseCoin(), v.getQuoteCoin());
            Long howLong = System.currentTimeMillis() - v.getCreated();
            howLong = howLong / (1000 * 60 * 15);
            List<Kline> klines =
                    CoinUtils.getKlineList(symbol, KlineTime.MIN15, howLong.intValue() + 1);

            if (klines == null) {
                return;
            }
            double cur = CoinUtils.getLastestKline(symbol).getClose();
            for (int i = 0; i < klines.size(); i++) {
                if (klines.get(i).getLow() <= v.getTargetValue()
                        && klines.get(i).getHigh() >= v.getTargetValue()) {

                    noticeService.WaterLevelMonitoring(v, CommonUtils.removeZero(cur));
                    noticeService.wxNoticeWaterLevelMonitoring(v, CommonUtils.removeZero(cur), "", "");
                    v.setComplete(true);
                    v.setCompleteTime(TimeFormat.format(System.currentTimeMillis()));
                    waterLevelMonitorService.update(v);
                    return;
                }
            }

            logger.info(v.getBaseCoin() + "/" + v.getQuoteCoin() + " 没有到达设定水位{}。当前:{}", v.getTargetValue(), cur);
        });
    }
}
