package site.binghai.coin.analysis.basic;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by binghai on 2018/1/9.
 *
 * @ huobi
 */
public class AnalysisMethodTest {
    @Test
    public void kline2Predicate() throws Exception {
        List<Kline> ls = CoinUtils.getKlineList(new Symbol("btc", "usdt"), KlineTime.MIN1, 5);
        ls.forEach(v -> {
            System.out.println(v.getClose() + " -> " + AnalysisMethod.Kline2Predicate(v));
        });
    }

}