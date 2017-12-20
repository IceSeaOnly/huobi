package site.binghai.coin.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import site.binghai.coin.client.ApiClient;

import java.text.DecimalFormat;

/**
 * Created by binghai on 2017/12/19.
 *
 * @ huobi
 */
@Data
public class Coin {
    private double balance;
    private String currency; //币种
    private String type; //trade or frozen
    private String market; // otc 法币交易 margin 杠杆交易 spot 币币交易
    private double sumPrice; // 转换成btc


    public String asString() {
        DecimalFormat df = new DecimalFormat("###############0.0000000000 ");
        Double v = getCurrentPriceValuation();

        return getMarketType() + (type.equals("trade") ? "交易中的" : "被冻结的") +
                currency.toUpperCase() +
                " : " + df.format(balance) +
                (v == null ? "" : " ,估值BTC " + df.format(v));
    }

    public Double getCurrentPriceValuation() {
        JSONObject data = ApiClient.commonClient.currentPriceValuation(this);
        if (data == null) {
            if (currency.equals("btc")) {
                sumPrice = balance; // btc本币
            } else {
                sumPrice = 0; // 不参与计算
            }
            return null;
        }
        double low = data.getDouble("low");
        double high = data.getDouble("high");
        double avg = (low + high) / 2;
        return sumPrice = avg * balance / (market.equals("margin") ? ApiClient.commonCoreParams.getMarginRate() : 1);
    }

    public String getMarketType() {
        String rs = " | ";
        switch (market) {
            case "otc":
                rs = "法币" + rs;
                break;
            case "margin":
                rs = "杠杆" + rs;
                break;
            default:
                rs = "币币" + rs;
        }
        return rs;
    }
}
