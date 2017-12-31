package site.binghai.coin.analysis;

import org.junit.Test;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.entity.Kline;
import site.binghai.coin.response.Symbol;
import site.binghai.coin.utils.TimeFormat;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by binghai on 2017/12/31.
 *
 * @ huobi
 */
public class CronAnalysisTest {
    @Test
    public void getMaxRise() throws Exception {
        List<Symbol> symbolList = CronAnalysis.getRisedBtcCoins();
        Kline kline = CronAnalysis.getMaxRise(symbolList.get(0));
        System.out.println(String.format("%s at %s", TimeFormat.format(kline.getId() * 1000), kline.getHigh()));
    }

    @Test
    public void getRisedBtcCoins() throws Exception {
        List<Symbol> symbolList = CronAnalysis.getRisedBtcCoins();
        assert !CollectionUtils.isEmpty(symbolList);
        symbolList.forEach(v -> System.out.println(v.getBaseCurrency() + " rised " + v.getRise() + " at " + v.getSymbolPartition()));
    }

    @Test
    public void getRisedUsdtCoins() throws Exception {
        List<Symbol> symbolList = CronAnalysis.getRisedUsdtCoins();
        assert !CollectionUtils.isEmpty(symbolList);
        symbolList.forEach(v -> System.out.println(v.getBaseCurrency() + " rised " + v.getRise() + " at " + v.getSymbolPartition()));
    }

    @Test
    public void getRisedEthCoins() throws Exception {
        List<Symbol> symbolList = CronAnalysis.getRisedEthCoins();
        assert !CollectionUtils.isEmpty(symbolList);
        symbolList.forEach(v -> System.out.println(v.getBaseCurrency() + " rised " + v.getRise() + " at " + v.getSymbolPartition()));
    }

    @Test
    public void getRiseCoinsByCurrent() throws Exception {
        List<Symbol> symbolList = CronAnalysis.getRiseCoinsByCurrent();
        assert !CollectionUtils.isEmpty(symbolList);
        symbolList.forEach(v -> System.out.println(v.getBaseCurrency() + " by " + v.getQuoteCurrency() + " at " + v.getSymbolPartition()));
    }

}