package site.binghai.coin.common.utils;

import org.junit.Test;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;

import java.util.List;

/**
 * Created by binghai on 2017/12/31.
 *
 * @ huobi
 */
public class CoinUtilsTest {
    @Test
    public void currentPrice() throws Exception {
    }

    @Test
    public void btc2rmb() throws Exception {
    }

    @Test
    public void btc2usdt() throws Exception {
    }

    @Test
    public void getKlineList() throws Exception {
        List<Kline> ls = CoinUtils.getKlineList(null, KlineTime.DAY, 30);

        Kline max = ls.stream()
                .max((a, b) -> double2int(a.getClose() - b.getClose()))
                .orElseGet(null);

        if (max != null) {
            System.out.println(TimeFormat.format(max.getId() * 1000));
        }
    }

    @Test
    public void allSymbols() throws Exception {
        List<Symbol> symbols = CoinUtils.allSymbols();
        assert symbols != null && symbols.size() > 0;
    }

    private static int double2int(double v) {
        if (v > 0) {
            return 1;
        } else if (v < 0) {
            return -1;
        }
        return 0;
    }
}