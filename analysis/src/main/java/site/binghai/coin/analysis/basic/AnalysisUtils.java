package site.binghai.coin.analysis.basic;

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

import static site.binghai.coin.common.utils.CommonUtils.cmpDouble2int;

/**
 * Created by binghai on 2017/12/31.
 * 定时分析
 *
 * @ huobi
 */
public class AnalysisUtils {
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

        rises.sort((a, b) -> cmpDouble2int(b.getRise() - a.getRise()));
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
                .max((a, b) -> cmpDouble2int(a.getClose() - b.getClose()))
                .orElseGet(null);

        return max;
    }

    private static List getRisedCoins(String coin) {
        return getRiseCoinsByCurrent(coin)
                .stream()
                .filter(v -> v.getQuoteCurrency().toUpperCase().equals(coin))
                .collect(Collectors.toList());
    }

    private static int compare(String str, String target) {
        int d[][];              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) {                       // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {                       // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) {                       // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两字符串的相似度
     */

    public static float getSimilarityRatio(String str, String target) {
        return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
    }
}
