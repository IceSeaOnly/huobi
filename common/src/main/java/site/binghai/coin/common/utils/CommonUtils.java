package site.binghai.coin.common.utils;

/**
 * Created by binghai on 2017/12/31.
 *
 * @ huobi
 */
public class CommonUtils {
    /**
     * 排序专用
     * */
    public static int cmpDouble2int(double v) {
        if (v > 0) {
            return 1;
        } else if (v < 0) {
            return -1;
        }
        return 0;
    }
}
