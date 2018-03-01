package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.data.impl.MemberCacheService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
//        try {
//            Long oid = apiClient.cancleOrder(orderId.toString());
//            if (oid != null && orderId.equals(oid)) {
//                return success("撤销成功,请等待列表刷新!");
//            }
//            return failed("撤销失败!原因未知!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return failed("撤销失败!" + e.getMessage());
//        }
        System.out.println("撤销订单:" + orderId);
        return success("success");
    }
}
