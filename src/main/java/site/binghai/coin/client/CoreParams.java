package site.binghai.coin.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by binghai on 2017/12/20.
 *
 * @ huobi
 */
public class CoreParams {
    public static double usdt2rmb = 6.55;

    private static Set<Long> btcAccounts = new HashSet();

    static {
        btcAccounts.addAll(Arrays.asList(463593L, 964661L));
    }

    public static boolean isBtcAccount(long accountId) {
        return btcAccounts.contains(accountId);
    }
}
