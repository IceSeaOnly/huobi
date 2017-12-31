package site.binghai.coin.entity;

import lombok.Data;

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
public class Kline {
    private long id;
    private double amount;
    private int count;
    private double open;
    private double close;
    private double low;
    private double high;
    private double vol;
}
