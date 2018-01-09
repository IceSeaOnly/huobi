package site.binghai.coin.common.entity;

/**
 * Created by binghai on 2017/12/31.
 *
 * @ huobi
 */
public enum KlineTime {
    MIN1("1min", 60000l),
    MIN5("5min", 300000l),
    MIN15("15min", 900000l),
    MIN30("30min", 1800000l),
    MIN60("60min", 3600000l),
    DAY("1day", 86400000l),
    MONTH("1mon", 2592000000l),
    WEEK("1week", 604800000l),
    YEAR("1year", 31536000000l),;

    private String time;
    private long ts; // 折换成时间戳是多少

    KlineTime(String time, long ts) {
        this.time = time;
        this.ts = ts;
    }

    public String getTime() {
        return time;
    }

    public long getTs() {
        return ts;
    }
}
