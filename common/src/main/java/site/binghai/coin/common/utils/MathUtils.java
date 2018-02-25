package site.binghai.coin.common.utils;

import java.util.List;

/**
 * Created by binghai on 2018/2/22.
 *
 * @ huobi
 */
public class MathUtils {
    /**
     * to get variance
     * 求方差
     */
    public static double variance(double base, List<Double> values) {
        return values.parallelStream()
                .map(v -> Math.pow(v - base, 2))
                .reduce(0.0, Double::sum);
    }
}
