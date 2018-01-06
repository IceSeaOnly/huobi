package site.binghai.coin.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import site.binghai.coin.common.response.Symbol;

import static org.junit.Assert.*;

/**
 * Created by binghai on 2018/1/6.
 *
 * @ huobi
 */
public class CoinUtilsTest {
    @Test
    public void getLastestKline() throws Exception {
        Symbol symbol = new Symbol();
        symbol.setBaseCurrency("eth");
        symbol.setQuoteCurrency("usdt");
        System.out.println(JSONObject.toJSONString(CoinUtils.getLastestKline(symbol)));
    }

}