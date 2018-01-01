package site.binghai.coin.common.entity;

import lombok.Data;
import site.binghai.coin.common.client.CoreParams;
import site.binghai.coin.common.utils.CoinUtils;

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
    private String settlementCoin = "btc";
    private String type; //trade or frozen
    private String market; // otc 法币交易 margin 杠杆交易 spot 币币交易
    private double sumPrice; // 转换成rmb

    public double getSumPrice() {
        sumPrice = getCurrentPriceValuation() * balance;
        System.out.println(asString());
        return sumPrice;
    }

    public String asString() {
        DecimalFormat df = new DecimalFormat("###############0.0000000000 ");
        return String.format("%6s %6s   %s  %16s %s", currency.toUpperCase(), type.equals("trade") ? "交易中" : "冻结中", settlementCoin.toUpperCase(), df.format(balance), df.format(sumPrice));
    }

    /**
     * 获取当前单个币价值
     * 计算单位 RMB
     */
    public Double getCurrentPriceValuation() {
        if (currency.equals("btc")) {
            return CoinUtils.btc2rmb(); // btc本币
        } else if (currency.equals("usdt")) {
            return CoreParams.usdt2rmb;
        }
        return CoinUtils.currentPrice(this) *
                ("btc".equals(getSettlementCoin()) ? CoinUtils.btc2rmb() : CoreParams.usdt2rmb);
    }
}
