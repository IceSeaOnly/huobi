package site.binghai.coin.common.utils;

import java.util.*;

/**
 * Created by binghai on 2018/3/10.
 * 启动参数工具
 *
 * @ huobi
 */

public class SetupUtils {
    private static List<String> setupParams = new ArrayList<>();

    // 系统启动时调用次方法传入参数
    public static void onSystemStart(String[] args, List<String> mustParams) throws Exception {
        if (args != null && args.length > 0) {
            onSystemStart(Arrays.asList(args));
        }
        if (mustParams == null) return;
        for (String mustParam : mustParams) {
            if (!envHasParam(mustParam)) {
                throw new Exception("must param '" + mustParam + "' not exist!");
            }
        }
    }

    // 系统启动时调用次方法传入参数
    public static void onSystemStart(List<String> args) {
        if (null != args) {
            setupParams.addAll(args);
        }
    }

    // 判断启动参数是否含有某个值
    public static boolean envHasParam(String param) {
        return setupParams.contains(param);
    }

    // 取某个值后面的参数
    public static String getParamAfter(String key) {
        boolean get = false;
        for (int i = 0; i < setupParams.size(); i++) {
            if (get) {
                return setupParams.get(i);
            }

            if (setupParams.get(i).equals(key)) {
                get = true;
            }
        }
        return null;
    }
}
