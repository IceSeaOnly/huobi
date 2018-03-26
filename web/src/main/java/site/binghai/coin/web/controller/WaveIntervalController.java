package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;
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
@RequestMapping("admin")
@RestController
public class WaveIntervalController extends BaseController {

    /**
     * 10天内btc波动数据
     * chartData: [], 波动曲线，{ price: '8840', counts: 38 } jsonArray
     * statisticData: [], {name: '今日注册',value: '12678',} 6个标签 jsonArray
     * <p>
     * 连续涨,连续跌，最久连续涨，最久连续跌，平均涨幅，平均跌幅
     */
    @RequestMapping("btcDataStatistics")
    public Object waveInterval() {
        JSONObject finalResp = new JSONObject();

        Symbol symbol = new Symbol("btc", "usdt");
        List<Kline> list = CoinUtils.getKlineList(symbol, KlineTime.MIN15, 960);

        if (CollectionUtils.isEmpty(list)) {
            return failed("time out");
        }

        String trend = list.get(0).getClose() > list.get(1).getClose() ? "上涨" : "下跌";
        List<Integer> prices = list.stream().map(v -> v.getClose().intValue()).collect(Collectors.toList());

        TreeMap<Integer, Integer> maps = new TreeMap<>();

        prices.forEach(v -> {
            int p = v / 100;
            if (maps.containsKey(p)) {
                int newV = maps.get(p) + 1;
                maps.put(p, newV);
            } else {
                maps.put(p, 1);
            }
        });

        JSONArray chartData = new JSONArray();
        maps.forEach((k, v) -> {
            JSONObject item = new JSONObject();
            item.put("price", k);
            item.put("counts", v);
            chartData.add(item);
        });
        finalResp.put("chartData", chartData);

        List<Integer> riseList = new ArrayList<>();
        List<Integer> fallList = new ArrayList<>();
        List<Double> riseRange = new ArrayList<>();
        List<Double> fallRange = new ArrayList<>();
        int rise = 0;
        int fall = 0;
        int riseStart = prices.get(0);
        int fallStart = prices.get(0);


        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) >= prices.get(i - 1)) {
                rise++;
                if (fall != 0) {
                    fallList.add(fall);
                    fallRange.add(fallStart * 1.0 / prices.get(i - 1) - 1);
                }
                fall = 0;
                fallStart = prices.get(i);
            } else {
                fall++;
                if (rise != 0) {
                    riseList.add(rise);
                    riseRange.add(prices.get(i - 1) * 1.0 / riseStart - 1);
                }
                rise = 0;
                riseStart = prices.get(i);
            }
        }

        if (rise != 0) {
            riseList.add(rise);
            riseRange.add(prices.get(prices.size() - 1) * 1.0 / riseStart - 1);
        }

        if (fall != 0) {
            fallList.add(fall);
            fallRange.add(fallStart * 1.0 / prices.get(prices.size() - 1) - 1);
        }

        Collections.sort(riseList);
        Collections.sort(fallList);
        Collections.sort(riseRange);
        Collections.sort(fallRange);

        JSONArray statisticData = new JSONArray();
        statisticData.add(buildStatisticData("当前价格", CoinUtils.getLastestKline(symbol).getClose()));
        statisticData.add(buildStatisticData("当前趋势", trend));
        statisticData.add(buildStatisticData("连续久涨", riseList.get(riseList.size() - 1)));
        statisticData.add(buildStatisticData("连续久跌", fallList.get(fallList.size() - 1)));
        statisticData.add(buildStatisticData("平均涨幅", getAvg(riseRange)));
        statisticData.add(buildStatisticData("平均跌幅", getAvg(fallRange)));

        finalResp.put("statisticData", statisticData);
        return success(finalResp, "success");
    }

    private String getAvg(List<Double> list) {
        if (null == list || list.size() == 0) return "-%";
        double sum = 0;
        for (Double aDouble : list) {
            sum += aDouble;
        }
        return String.format("%.4f", sum / list.size()) + "%";
    }

    private Object buildStatisticData(String s, Object v) {
        JSONObject object = new JSONObject();
        object.put("name", s);
        object.put("value", v);
        return object;
    }

    private String toLOGO(Integer v) {
        StringBuilder sb = new StringBuilder(" |");
        while (v-- >= 0) {
            sb.append("#");
        }
        return sb.toString();
    }


}
