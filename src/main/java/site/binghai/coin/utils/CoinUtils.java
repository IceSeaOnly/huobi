package site.binghai.coin.utils;

import com.alibaba.fastjson.JSONObject;
import site.binghai.coin.client.CoreParams;
import site.binghai.coin.entity.Coin;

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

    public static void main(String[] args) {
        System.out.println(btc2rmb());
    }
}
