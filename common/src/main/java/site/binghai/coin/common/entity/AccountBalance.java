package site.binghai.coin.common.entity;

import lombok.Data;
import site.binghai.coin.common.client.CoreParams;
import site.binghai.coin.common.utils.TimeFormat;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2017/12/19.
 *
 * @ huobi
 */
@Data
public class AccountBalance {
    private long id; //account id
    private String state; // working
    private String type; // spot for 现货
    private String settlementCoin = "btc";
    private List<Coin> list;

    /**
     * 隐藏资产为0的币种
     */
    public AccountBalance removeEmptyCoin() {
        list = list.stream().filter(v -> v.getBalance() > 0.0).collect(Collectors.toList());
        return this;
    }

    /**
     * 判断本账户使用哪一种币结算,btc or usdt
     */
    public AccountBalance decideSettlementCoin() {
        if (CoreParams.isBtcAccount(id)) {
            setSettlementCoin("btc");
        } else {
            setSettlementCoin("usdt");
        }

        list.forEach(v -> v.setSettlementCoin(getSettlementCoin()));
        return this;
    }

    /**
     * 打印该账户内资产列表
     */
    public double printAllCoins() {
        decideSettlementCoin();
        list.forEach(v -> v.setMarket(type));
        System.out.println(asString());
        System.out.println("   币种    状态    结算          总量        折合RMB");
        double rmbSum = 0;
        for (Coin coin : list) {
            rmbSum += coin.getSumPrice();
        }
        System.out.println("约合人民: " + rmbSum + "\n");
        return rmbSum;
    }

    public String asString() {
        String time = TimeFormat.format(System.currentTimeMillis());
        return String.format("************* %s %s账户 *************", time, getMarketType());
    }

    public String getMarketType() {
        switch (type) {
            case "otc":
                return "法币";
            case "margin":
                return "杠杆";
            default:
                return "币币";
        }
    }
}
