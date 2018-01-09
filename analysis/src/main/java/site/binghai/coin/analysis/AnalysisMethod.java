package site.binghai.coin.analysis;


import java.util.List;

/**
 * Created by binghai on 2018/1/8.
 * some method that often used
 *
 * @ huobi
 */
public class AnalysisMethod {
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
