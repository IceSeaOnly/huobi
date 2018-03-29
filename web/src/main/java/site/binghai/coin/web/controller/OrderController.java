package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.common.utils.CommonUtils;
import site.binghai.coin.data.impl.MemberCacheService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static site.binghai.coin.common.utils.CommonUtils.doubleSubCut;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@RequestMapping("admin")
@RestController
public class OrderController extends BaseController {

    @Autowired
    private MemberCacheService memberCacheService;
    @Autowired
    private ApiClient apiClient;

    /**
     * listType :
     * null : for all
     * 0 : only not complete
     * 1 : only complete
     */
    @RequestMapping("orderList")
    public Object orderList(String listType) {
        List<JSONObject> list = (List<JSONObject>) memberCacheService.get(MemberCacheService.CacheKeys.LIST_ALL_ORDERS);
        if (listType == null || "all".equals(listType)) {
        } else if ("notComplete".equals(listType)) {
            list = list.stream().filter(v -> !isCompleteOrder(v.getString("state"))).collect(Collectors.toList());
        } else if ("complete".equals(listType)) {
            list = list.stream().filter(v -> isCompleteOrder(v.getString("state"))).collect(Collectors.toList());
        }
        return success(list, "ok");
    }

    private boolean isCompleteOrder(String state) {
        return "filled".equals(state) || "canceled".equals(state);
    }

    @RequestMapping("cancleOrder")
    public Object cancleOrder(@RequestParam Long orderId) {
        try {
            Long oid = apiClient.cancleOrder(orderId.toString());
            if (oid != null && orderId.equals(oid)) {
                return success("撤销成功,请等待列表刷新!");
            }
            return failed("撤销失败!原因未知!");
        } catch (IOException e) {
            e.printStackTrace();
            return failed("撤销失败!" + e.getMessage());
        }
//        System.out.println("撤销订单:" + orderId);
//        return success("success");
    }


    @RequestMapping("tradeHistoryItems")
    public Object tradeHistoryItems(@RequestParam Integer pageSize, @RequestParam Integer page) {
        List<JSONObject> list = (List<JSONObject>) memberCacheService.get(MemberCacheService.CacheKeys.LIST_ALL_ORDERS);

        List<JSONObject> buyList = new ArrayList<>();
        List<JSONObject> sellList = new ArrayList<>();

        list.stream()
                .filter(v -> isFilledOrder(v))
                .sorted((a, b) -> b.getLong("created-at") - a.getLong("created-at") > 0 ? 1 : 0)
                .forEach(v -> {
                    v.put("total", CommonUtils.removeZero(v.getDouble("field-cash-amount")));
                    v.put("fieldFees",String.format("%.4f",v.getDoubleValue("fieldFees")));
                    if(v.getString("type").contains("market")){
                        v.put("price","市价");
                    }
                    if (v.getString("type").startsWith("sell")) {
                        sellList.add(v);
                    } else {
                        buyList.add(v);
                    }
                });

        JSONObject buy = new JSONObject();
        JSONObject sell = new JSONObject();

        buy.put("name", "买入交易");
        buy.put("list", CommonUtils.subList(buyList, pageSize, page));
        sell.put("name", "卖出交易");
        sell.put("list", CommonUtils.subList(sellList, pageSize, page));

        JSONObject obj = new JSONObject();
        obj.put("totalSize", Math.max(buyList.size(), sellList.size()));
        obj.put("list", Arrays.asList(buy, sell));

        return success(obj, "SUCCESS");
    }

    /**
     * 历史交易单统计
     */
    @RequestMapping("tradeHistoryDisplay")
    public Object tradeHistoryDisplay() {
        List<JSONObject> list = (List<JSONObject>) memberCacheService.get(MemberCacheService.CacheKeys.LIST_ALL_ORDERS);
        if (CollectionUtils.isEmpty(list)) {
            return failed("获取失败!");
        }

        int buy = 0;
        int buyExpect = 0;
        int sell = 0;
        int sellExpect = 0;
        double buySum = 0.0;
        double buySumExpect = 0.0;
        double sellSum = 0.0;
        double sellSumExpect = 0.0;

        for (JSONObject v : list) {
            if (v.getString("state").equals("canceled")) {
                continue;
            }

            if (v.getString("type").startsWith("buy")) {
                buy++;
                buyExpect += isFilledOrder(v) ? 0 : 1;
                buySum += getTradeUSDT(v);
                buySumExpect += getExpectTradeUSDT(v);
            } else if (v.getString("type").startsWith("sell")) {
                sell++;
                sellExpect += isFilledOrder(v) ? 0 : 1;
                sellSum += getTradeUSDT(v);
                sellSumExpect += getExpectTradeUSDT(v);
            }
        }

        List<Object> objects = new ArrayList<>();

        objects.add(buildHistoryDisplayItem("买入交易", buy, buyExpect));
        objects.add(buildHistoryDisplayItem("卖出交易", sell, sellExpect));
        objects.add(buildHistoryDisplayItem("买入总额", doubleSubCut(buySum, 2), doubleSubCut(buySumExpect, 2)));
        objects.add(buildHistoryDisplayItem("卖出总额", doubleSubCut(sellSum, 2), doubleSubCut(sellSumExpect, 2)));
        objects.add(buildHistoryDisplayItem("利润收益", doubleSubCut(sellSum - buySum, 2), doubleSubCut(sellSumExpect - buySumExpect, 2)));

        return success(objects, "SUCCESS");
    }

    private double getExpectTradeUSDT(JSONObject v) {
        if (isFilledOrder(v)) {
            return 0.0;
        }

        return getTradeUSDT(v);
    }

    private double getTradeUSDT(JSONObject v) {
        if (v.getString("symbol").endsWith("USDT")) {
            return v.getDouble("field-cash-amount");
        }

        return 0.0;
    }

    /**
     * 当交易完成时，返回0，否则1
     */
    private boolean isFilledOrder(JSONObject v) {
        return v.getString("state").equals("filled");
    }

    private Object buildHistoryDisplayItem(String title, Object count, Object expect) {
        JSONObject obj = new JSONObject();
        obj.put("count", expect);
        obj.put("expect", count);
        obj.put("title", title);
        return obj;
    }
}
