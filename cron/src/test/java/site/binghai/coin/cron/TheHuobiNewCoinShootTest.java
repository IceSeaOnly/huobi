package site.binghai.coin.cron;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by binghai on 2018/1/7.
 *
 * @ huobi
 */
public class TheHuobiNewCoinShootTest {
    private TheHuobiNewCoinShoot huobiNewCoinShoot;

    @Before
    public void setUp() throws Exception {
        huobiNewCoinShoot = new TheHuobiNewCoinShoot();
    }

    @Test
    public void gunShoot() throws Exception {
        huobiNewCoinShoot.gunShoot();
    }

}