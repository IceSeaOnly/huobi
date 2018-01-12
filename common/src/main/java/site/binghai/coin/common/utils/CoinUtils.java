package site.binghai.coin.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.client.CoreParams;
import site.binghai.coin.common.entity.Coin;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static site.binghai.coin.common.entity.KlineTime.MIN1;

/**
 * Created by binghai on 2017/12/31.
 *
 * @ huobi
 */
public class CoinUtils {
    /**
     * 获取该币种的当前价格
     */
    public static double currentPrice(Coin coin) {
        JSONObject result =
                HttpUtils.sendJSONGet(
                        "/market/history/kline?period=1min&size=1&symbol=" + coin.getCurrency() + coin.getSettlementCoin(),
                        null,
                        null);

        if (null != result && "ok".equals(result.getString("status"))) {
            return result.getJSONArray("data").getJSONObject(0).getDouble("close");
        }
        return 0;
    }

    public static double btc2rmb() {
        return btc2usdt() * CoreParams.usdt2rmb;
    }

    public static double btc2usdt() {
        JSONObject result =
                HttpUtils.sendJSONGet(
                        "/market/history/kline?period=1min&size=1&symbol=btcusdt",
                        null,
                        null);

        if (null != result && "ok".equals(result.getString("status"))) {
            return result.getJSONArray("data").getJSONObject(0).getDouble("close");
        }
        return 0;
    }


    /**
     * 获取K线数据
     * 不含 买卖量、价
     */
    public static List<Kline> getKlineList(Symbol symbol, KlineTime klineTime, int size) {
        JSONObject data = HttpUtils.sendJSONGet("/market/history/kline",
                String.format("symbol=%s&period=%s&size=%d", symbol.getBaseCurrency() + symbol.getQuoteCurrency(), klineTime.getTime(), size), null);

        if (data != null && "ok".equals(data.getString("status"))) {
            return data.getJSONArray("data").toJavaList(Kline.class);
        }

        return null;
    }

    /**
     * 获取所有交易对
     */
    private static List<String> coinFilter = Arrays.asList("bt1", "bt2");

    public static List<Symbol> allSymbols() {
        JSONObject data = HttpUtils.sendJSONGet("/v1/common/symbols", null, null);
        if (data != null && "ok".equals(data.getString("status"))) {
            List<Symbol> list = data.getJSONArray("data").toJavaList(Symbol.class);
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            return list.stream().filter(v -> !coinFilter.contains(v.getBaseCurrency())).collect(Collectors.toList());
        }
        return null;
    }

    public static Long getServerTimestamp(Symbol symbol) {
        JSONObject data = HttpUtils.sendJSONGet("/market/history/kline",
                String.format("symbol=%s&period=%s&size=%d", symbol.getBaseCurrency() + symbol.getQuoteCurrency(), MIN1.getTime(), 1), null);

        if (data == null) {
            return null;
        }

        if (data.getJSONArray("data").isEmpty()) {
            return null;
        }
        return data.getJSONArray("data").getJSONObject(0).getLong("close");
    }

    /**
     * 获取最新交易聚合详情
     * 包含 买卖量、价
     */
    public static Kline getLastestKline(Symbol symbol) {
        JSONObject data = HttpUtils.sendJSONGet("/market/detail/merged", "symbol=" + symbol.getBaseCurrency() + symbol.getQuoteCurrency(), null);
        if (data != null && "ok".equals(data.getString("status"))) {
            Kline kline = data.getJSONObject("tick").toJavaObject(Kline.class);
            String bid = data.getJSONObject("tick").getString("bid");
            String ask = data.getJSONObject("tick").getString("ask");

            double[] bidParams = parseDoubles(bid);
            double[] askParams = parseDoubles(ask);

            kline.setBidPrice(bidParams[0]);
            kline.setBidAmount(bidParams[1]);
            kline.setAskPrice(askParams[0]);
            kline.setAskAmount(askParams[1]);
            return kline;
        }
        return null;
    }

    private static double[] parseDoubles(String ori) {
        String strs = ori.trim();
        String trim = strs.substring(1, strs.length() - 1);
        String[] params = trim.split(",");
        return new double[]{Double.parseDouble(params[0]), Double.parseDouble(params[1])};
    }
}
