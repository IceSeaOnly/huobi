package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.TimeFormat;
import site.binghai.coin.data.impl.KlineService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2018/1/13.
 * data from 3rd website
 *
 * @ huobi
 */
@RequestMapping("open")
@RestController
@CrossOrigin(origins = "*")
public class OutApi extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(OutApi.class);

    @Autowired
    private KlineService klineService;

    @RequestMapping("kline")
    public Object kline(@RequestParam Integer size, @RequestParam String coin, @RequestParam String qcoin, String callback) {
        Symbol symbol = new Symbol(coin, qcoin);

        Kline zero = klineService.getZeroPointPrice(symbol);
        if (zero == null) {
            logger.error("zero can't be null!");
            return null;
        }

        long end = System.currentTimeMillis();
        long start = System.currentTimeMillis() - size * 60000;

        List<Kline> rs = klineService.getKlineBetween(symbol, start, end);

        JSONObject result = new JSONObject();
        JSONObject info = new JSONObject();
        double currentPrice = rs.get(0).getClose();
        info.put("marketPanel_time", TimeFormat.format(rs.get(0).getId() * 1000));
        if (currentPrice >= zero.getClose()) {
            info.put("marketPanel_riserate", "+" + String.format("%.2f", (currentPrice / zero.getClose() - 1.0) * 100) + "%");
            info.put("marketPanel_fallrate", "");
        } else {
            info.put("marketPanel_riserate", "");
            info.put("marketPanel_fallrate", "-" + String.format("%.2f", (zero.getClose() / currentPrice - 1.0) * 100) + "%");
        }
        info.put("marketPanel_cur_price", currentPrice);

        JSONArray arr = new JSONArray();
        rs.forEach(v -> {
            JSONArray item = new JSONArray();
            item.add(TimeFormat.format(v.getCreated()));
            item.add(v.getOpen());
            item.add(v.getClose());
            item.add(v.getLow());
            item.add(v.getHigh());
            item.add(v.getVol());

            arr.add(0, item);
        });

        result.put("info", info);
        result.put("list", arr);
        return callback == null ? arr : jqueryBack(callback, result);
    }

    @RequestMapping("symbols")
    public Object symbols(@RequestParam String qcoin, String callback) {
        List<Symbol> symbols = CoinUtils.allSymbols();
        qcoin.toLowerCase();
        JSONArray arr = new JSONArray();
        symbols.stream().filter(v -> v.getQuoteCurrency().equals(qcoin)).forEach(v -> {
            JSONObject item = new JSONObject();
            item.put("coin", v.getBaseCurrency().toUpperCase());
            item.put("baseCoin", v.getBaseCurrency().toLowerCase());
            arr.add(item);
        });

        return callback == null ? arr : jqueryBack(callback, arr);
    }

    /**
     * @param items BTC/USDT,QTUM/BTC...
     */
    @RequestMapping("relationShip")
    public Object relationShip(@RequestParam String items, @RequestParam Integer size, String callback) {
        String[] coins = items.split(",");
        JSONObject result = new JSONObject();
        List<String> xaxis = new ArrayList<>();
        List<JSONObject> seriesData = new ArrayList<>();


        List<Symbol> symbols = new ArrayList<>();
        for (String coin : coins) {
            symbols.add(new Symbol(coin.split("/")[0].toLowerCase(), coin.split("/")[1].toLowerCase()));
        }

        double avg = getAvgClose(symbols);

        for (Symbol coin : symbols) {
            List<Kline> ls = CoinUtils.getKlineList(coin, KlineTime.MIN15, size);
            double coefficient = avg / ls.get(0).getClose();

            if (xaxis.isEmpty()) {
                List<String> tmp = ls.stream().map(v -> TimeFormat.format(v.getId() * 1000)).collect(Collectors.toList());
                tmp.forEach(v -> xaxis.add(0, v));
            }

            JSONObject one = new JSONObject();
            one.put("name", coin.getBaseCurrency().toUpperCase() + "/" + coin.getQuoteCurrency().toUpperCase());
            one.put("type", "line");
            one.put("areaStyle", "{normal: {}}");
            one.put("data", ls.stream().map(v -> v.getClose() * coefficient).collect(Collectors.toList()));

            seriesData.add(0, one);
        }

        result.put("items", coins);
        result.put("xaxis", xaxis);
        result.put("seriesData", seriesData);
        return callback == null ? result : jqueryBack(callback, result);
    }

    /**
     * 计算均值
     */
    private double getAvgClose(List<Symbol> symbols) {
        double close = 0.0;
        for (Symbol symbol : symbols) {
            close += klineService.getLastestKline(symbol).getClose();
        }
        return close / symbols.size();
    }
}
