package site.binghai.coin.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.client.CoreParams;
import site.binghai.coin.common.entity.Coin;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;

import java.util.List;

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
                        "http://api.huobi.pro/market/history/kline?period=1min&size=1&symbol=" + coin.getCurrency() + coin.getSettlementCoin(),
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
                        "http://api.huobi.pro/market/history/kline?period=1min&size=1&symbol=btcusdt",
                        null,
                        null);

        if (null != result && "ok".equals(result.getString("status"))) {
            return result.getJSONArray("data").getJSONObject(0).getDouble("close");
        }
        return 0;
    }


    public static List<Kline> getKlineList(Symbol symbol, KlineTime klineTime, int size) {
        JSONObject data = HttpUtils.sendJSONGet("http://api.huobi.pro/market/history/kline",
                String.format("symbol=%s&period=%s&size=%d", symbol.getBaseCurrency() + symbol.getQuoteCurrency(), klineTime.getTime(), size), null);

        if (data != null && "ok".equals(data.getString("status"))) {
            return data.getJSONArray("data").toJavaList(Kline.class);
        }

        return null;
    }

    /**
     * 获取所有交易对
     */
    public static List<Symbol> allSymbols() {
        JSONObject data = HttpUtils.sendJSONGet("http://api.huobi.pro/v1/common/symbols", null, null);
        if (data != null && "ok".equals(data.getString("status"))) {
            return data.getJSONArray("data").toJavaList(Symbol.class);
        }
        return null;
    }

    public static Kline getLastestKline(Symbol symbol) {
        List<Kline> rs = getKlineList(symbol, KlineTime.MIN1, 1);
        return CollectionUtils.isEmpty(rs) ? null : rs.get(0);
    }
}
