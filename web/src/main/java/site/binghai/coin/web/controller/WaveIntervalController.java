package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.TimeFormat;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2018/1/16.
 *
 * @ huobi
 */
@RequestMapping("open")
@RestController
@CrossOrigin(origins = "*")
public class WaveIntervalController extends BaseController {
    @RequestMapping("waveInterval")
    public Object waveInterval(Symbol symbol, @RequestParam int size) {
        if (symbol == null) {
            return "symbol not set";
        }


        List<Kline> list = CoinUtils.getKlineList(symbol, KlineTime.MIN15, size);
        String startTime = TimeFormat.format(list.get(0).getId() * 1000);
        String endTime = TimeFormat.format(list.get(list.size() - 1).getId() * 1000);
        List<Double> prices = list.stream().map(v -> v.getClose()).collect(Collectors.toList());
        Collections.sort(prices);

        int yMin = prices.get(0).intValue();
        int yMax = prices.get(prices.size() - 1).intValue();

        TreeMap<Integer, Integer> maps = new TreeMap<>();

        prices.forEach(v -> {
            int p = (int) (v / 100);
            if (maps.containsKey(p)) {
                maps.put(p, maps.get(p) + 1);
            } else {
                maps.put(p, 1);
            }
        });

        List<Integer> dataAxis = new ArrayList<>();
        List<Object> data = new ArrayList<>();
        List<Integer> dataShadow = new ArrayList<>();

        int current = (int) (CoinUtils.getLastestKline(symbol).getClose() / 100);

        JSONObject cur = new JSONObject();
        JSONObject color = new JSONObject();
        color.put("color", "#ff0000");
        cur.put("value", maps.get(current));
        cur.put("itemStyle", color);

        maps.forEach((k, v) -> {
            dataAxis.add(k);
            data.add(k == current ? cur : v);
            dataShadow.add(0);
        });

        JSONObject resp = new JSONObject();
        resp.put("yMax", yMax);
        resp.put("yMin", yMin);
        resp.put("dataAxis", dataAxis);
        resp.put("data", data);
        resp.put("dataShadow", dataShadow);
        resp.put("msg", String.format("from %s to %s", endTime, startTime));

        return resp;
    }

    private String toLOGO(Integer v) {
        StringBuilder sb = new StringBuilder(" |");
        while (v-- >= 0) {
            sb.append("#");
        }
        return sb.toString();
    }


}
