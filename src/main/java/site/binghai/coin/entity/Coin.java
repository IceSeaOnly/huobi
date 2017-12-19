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
    private double sumPrice;


    public String asString() {
        DecimalFormat df = new DecimalFormat("###############0.0000000000 ");
        Double v = getCurrentPriceValuation();
        return (type.equals("trade") ? "交易中的" : "被冻结的") +
                currency.toUpperCase() +
                " : " + df.format(balance) +
                (v == null ? "" : " ,估值BTC " + df.format(balance));
    }

    public Double getCurrentPriceValuation() {
        JSONObject data = ApiClient.commonClient.currentPriceValuation(this);
        if (data == null) {
            sumPrice = balance; // btc本币
            return null;
        }
        double low = data.getDouble("low");
        double high = data.getDouble("high");
        double avg = (low + high) / 2;
        return sumPrice = avg * balance;
    }
}
