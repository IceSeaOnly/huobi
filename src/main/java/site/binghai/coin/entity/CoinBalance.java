package site.binghai.coin.entity;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2017/12/19.
 *
 * @ huobi
 */
@Data
public class CoinBalance {
    private long id; //account id
    private String state; // working
    private String type; // spot for 现货
    private List<Coin> list;

    /**
     * 隐藏资产为0的币种
     */
    public CoinBalance removeEmptyCoin() {
        list = list.stream().filter(v -> v.getBalance() > 0.0).collect(Collectors.toList());
        return this;
    }

    /**
     * 打印资产列表
     */
    public void printAllCoins() {
        list.forEach(v -> System.out.println(v.asString()));
        Double sumBtc = list.stream().map(v -> v.getSumPrice()).reduce(0.0, Double::sum);
        System.out.println("合计BTC : " + sumBtc);
    }
}
