package site.binghai.coin.analysis;

import org.springframework.stereotype.Component;
import site.binghai.coin.analysis.basic.AbstractAnalysis;
import site.binghai.coin.analysis.basic.AnalysisMethod;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.JSONPuter;
import site.binghai.coin.common.utils.TimeFormat;

import java.util.List;

import static site.binghai.coin.common.entity.ConstantWords.*;

/**
 * Created by binghai on 2018/1/9.
 * 超短线预测
 *
 * @ huobi
 */
//@Component
public class VeryShortTermPredicate extends AbstractAnalysis {

    @Override
    protected void analysis(List<Kline> list, Symbol symbol, JSONPuter jsonPuter) {
        Long[] start_end = TimeFormat.getThisMinute(list.get(0).getCreated());
//        int predicate = list.stream().map(AnalysisMethod::Kline2Predicate).reduce(0, Integer::sum);
//        jsonPuter.put(resultCode, predicate);
        jsonPuter.put(success, false);
        jsonPuter.put(confirmTime, 0);
        jsonPuter.put(nextStart, start_end[0] + 60000);
        jsonPuter.put(nextEnd, start_end[1] + 60000);
    }

    @Override
    protected KlineTime getKlineTime() {
        return KlineTime.MIN1;
    }

    @Override
    protected int getSaveMethod() {
        return 1;
    }

    @Override
    protected void initApp() {

    }
}
