package site.binghai.coin.common.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by binghai on 2018/3/10.
 * 启动参数工具
 * @ huobi
 */

public class SetupUtils {
    private static Set<String> setupParams = new HashSet<>();

    // 系统启动时调用次方法传入参数
    public static void onSystemStart(String[] args){
        if(args != null && args.length > 0){
            onSystemStart(Arrays.asList(args));
        }
    }

    // 系统启动时调用次方法传入参数
    public static void onSystemStart(List<String> args){
        if(null != args){
            setupParams.addAll(args);
        }
    }

    // 判断启动参数是否含有某个值
    public static boolean envHasParam(String param){
        return setupParams.contains(param);
    }
}
