package site.binghai.coin.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by binghai on 2018/3/10.
 *
 * @ huobi
 */

public enum SubscribeType {

    EmergenceNews("突发新闻",0,0)
    ;
    private String type;
    private int code;
    private int extra; // 预留控制位,无意义

    private static Map<Integer,SubscribeType> maps = null;

    static {
        maps = new HashMap<>();
        for (SubscribeType subscribeType : SubscribeType.values()) {
            maps.put(subscribeType.getCode(),subscribeType);
        }
    }

    SubscribeType(String type, int code, int extra) {
        this.type = type;
        this.code = code;
        this.extra = extra;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    public static SubscribeType codeOf(int code){
        return maps.get(code);
    }
}
