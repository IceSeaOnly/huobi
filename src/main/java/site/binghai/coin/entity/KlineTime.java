package site.binghai.coin.entity;

/**
 * Created by binghai on 2017/12/31.
 *
 * @ huobi
 */
public enum KlineTime {
    MIN1("1min"),
    MIN5("5min"),
    MIN15("15min"),
    MIN30("30min"),
    MIN60("60min"),
    DAY("1day"),
    MONTH("1mon"),
    WEEK("1week"),
    YEAR("1year"),
    ;

    private String time;

    KlineTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
