package site.binghai.coin.analysis;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.JSONPuter;
import site.binghai.coin.common.utils.TimeFormat;

import java.util.List;
import java.util.stream.Collectors;

import static site.binghai.coin.common.entity.ConstantWords.*;
import static site.binghai.coin.common.utils.CommonUtils.doubleSubCut;

/**
 * Created by binghai on 2018/1/8.
 * 分钟级分析机
 *
 * @ huobi
 */
@Component
public class MinuteAnalysis extends AbstractAnalysis {
    @Scheduled(cron = "0 * * * * ?")
    public void cronAnalysis() {
        startWork();
    }

    @Override
    protected void analysis(List<Kline> list, Symbol symbol, JSONPuter jsonPuter) {
        List<Double> doubles = list.stream().map(v -> v.getClose()).collect(Collectors.toList());
        double vc = variance(doubles.get(0), doubles) * 10000;

        jsonPuter.put(sampleNumber, doubles.size())
                .put(standard, doubles.get(0))
                .put(variance, (int) vc);
    }

    @Override
    protected KlineTime getKlineTime() {
        return KlineTime.MIN1;
    }

    @Override
    protected void initApp() {

    }
}
