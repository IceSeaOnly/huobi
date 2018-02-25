package site.binghai.coin.common.utils;

/**
 * Created by binghai on 2017/12/31.
 *
 * @ huobi
 */
public class CommonUtils {
    /**
     * 排序专用
     */
    public static int cmpDouble2int(double v) {
        if (v > 0) {
            return 1;
        } else if (v < 0) {
            return -1;
        }
        return 0;
    }

    public static int cmpLong2int(long v){
        if (v > 0) {
            return 1;
        } else if (v < 0) {
            return -1;
        }
        return 0;
    }

    /**
     * double类型截断成String
     *
     * @param accuracy 截断精度，即小数点后几位
     */
    public static String doubleSubCut(double value, int accuracy) {
        if (accuracy < 0) {
            accuracy = 2;
        }

        String v = String.valueOf(value);
        int len = v.length();
        int index = v.indexOf(".");
        if (index == -1 || index >= len) {
            return v;
        }
        if (accuracy == 0) {
            return v.substring(0, index);
        }

        if (index + accuracy + 1 >= len) {
            return v;
        }
        return v.substring(0, index + accuracy + 1);
    }

    /**
     * 去除字符串上多余的0
     * */
    public static String removeZero(Object obj) {
        String str = obj.toString();
        if (str == null || str.length() == 0) {
            return "";
        }
        if (!str.endsWith("0")) {
            return str;
        }

        int idx = str.length() - 1;
        for (; idx > 0; idx--) {
            if (str.charAt(idx) != '0' || str.charAt(idx) == '.') {
                break;
            }
        }
        return str.substring(0, idx);
    }
}
