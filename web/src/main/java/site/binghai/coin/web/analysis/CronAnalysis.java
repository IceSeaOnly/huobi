package site.binghai.coin.web.analysis;

import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.TimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static site.binghai.coin.common.utils.CommonUtils.double2int;

/**
 * Created by binghai on 2017/12/31.
 * 定时分析
 *
 * @ huobi
 */
public class CronAnalysis {
    private static List<String> coinFilter = Arrays.asList("bt1", "bt2");

    /**
     * 获取到目前为止当天上涨的币种
     */
    public static List<Symbol> getRiseCoinsByCurrent() {
        return getRiseCoinsByCurrent(null);
    }

    public static List<Symbol> getRiseCoinsByCurrent(String coin) {
        coin = coin == null ? "BTC" : coin.toUpperCase();
        if (!Arrays.asList("BTC", "USDT", "ETH").contains(coin)) {
            coin = "BTC";
        }

        final String filter = coin;
        List<Symbol> symbols = CoinUtils.allSymbols();
        List<Symbol> rises = new ArrayList<>();

        symbols.stream()
                .filter(v -> !coinFilter.contains(v.getBaseCurrency()))
                .filter(v -> filter == null || v.getQuoteCurrency().toUpperCase().equals(filter))
                .forEach(symbol -> {
                    List<Kline> klines = CoinUtils.getKlineList(symbol, KlineTime.MIN60, 30);
                    if (!CollectionUtils.isEmpty(klines)) {
                        Long zeroTs = TimeFormat.getTimesmorning() / 1000;
                        double current = klines.get(0).getClose();
                        Kline zeroValue = klines.stream().filter(v -> zeroTs.equals(v.getId())).findAny().orElseGet(null);
                        if (zeroValue != null && current > zeroValue.getOpen()) {
                            symbol.setRise((current - zeroValue.getOpen()) / zeroValue.getOpen() * 100);
                            rises.add(symbol);
                        }
                    }
                });

        rises.sort((a, b) -> double2int(b.getRise() - a.getRise()));
        return rises;
    }

    public static List getRisedBtcCoins() {
        return getRisedCoins("BTC");
    }

    public static List getRisedUsdtCoins() {
        return getRisedCoins("USDT");
    }

    public static List getRisedEthCoins() {
        return getRisedCoins("ETH");
    }

    /**
     * 获取最大涨幅处
     */
    public static Kline getMaxRise(Symbol symbol) {
        Long minuts = (System.currentTimeMillis() - TimeFormat.getTimesmorning()) / 60000;
        List<Kline> ls = CoinUtils.getKlineList(symbol, KlineTime.MIN1, minuts.intValue() + 2);

        Kline max = ls.stream()
                .max((a, b) -> double2int(a.getClose() - b.getClose()))
                .orElseGet(null);

        return max;
    }

    private static List getRisedCoins(String coin) {
        return getRiseCoinsByCurrent(coin)
                .stream()
                .filter(v -> v.getQuoteCurrency().toUpperCase().equals(coin))
                .collect(Collectors.toList());
    }
}
