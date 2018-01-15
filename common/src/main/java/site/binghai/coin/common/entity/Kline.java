package site.binghai.coin.common.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by binghai on 2017/12/31.
     "id": K线id,
     "amount": 成交量,
     "count": 成交笔数,
     "open": 开盘价,
     "close": 收盘价,当K线为最晚的一根时，是最新成交价
     "low": 最低价,
     "high": 最高价,
     "vol": 成交额, 即 sum(每一笔成交价 * 该笔的成交量)
 * @ huobi
 */
@Data
//@Entity
public class Kline extends DeleteAble{
    @Id
    @GeneratedValue
    private long mainId;
    private long id; // 此处id是火币id，也是火币提供的时间戳
    private double amount;
    private int count;
    private double open;
    private double close;
    private double low;
    private double high;
    private double vol;

    private double bidPrice; // 买1价
    private double bidAmount; // 买1量
    private double askPrice; // 卖1价
    private double askAmount; // 卖1量

    private String coinName; // 币种名称
    private String quoteCoinName; // 计价币种
}
