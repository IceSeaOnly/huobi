package site.binghai.coin.cron;

import org.junit.Test;

import static site.binghai.coin.common.utils.CommonUtils.doubleSubCut;

/**
 * Created by binghai on 2018/1/8.
 *
 * @ huobi
 */
public class PriceTest {
    @Test
    public void priceTest() {
        double p = 0.02815;
        System.out.println(doubleSubCut(p, -1));
        System.out.println(doubleSubCut(p, -2));
        System.out.println(doubleSubCut(p, 0));
        System.out.println(doubleSubCut(p, 1));
        System.out.println(doubleSubCut(p, 2));
        System.out.println(doubleSubCut(p, 3));
        System.out.println(doubleSubCut(p, 4));
        System.out.println(doubleSubCut(p, 5));
        System.out.println(doubleSubCut(p, 6));
    }
}
