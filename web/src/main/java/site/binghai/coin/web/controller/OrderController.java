package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.data.impl.MemberCacheService;

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


    /**
     * listType :
     * null : for all
     * 0 : only not complete
     * 1 : only complete
     */
    @RequestMapping("orderList")
    public Object orderList(Integer listType) {
        List<JSONObject> list = (List<JSONObject>) memberCacheService.get(MemberCacheService.CacheKeys.LIST_ALL_ORDERS);
        if (listType == null) {
        } else if (listType == 0) {
            list = list.stream().filter(v -> !isCompleteOrder(v.getString("state"))).collect(Collectors.toList());
        } else if (listType == 1) {
            list = list.stream().filter(v -> isCompleteOrder(v.getString("state"))).collect(Collectors.toList());
        }
        return success(list, "ok");
    }

    private boolean isCompleteOrder(String state) {
        return "filled".equals(state) || "canceled".equals(state);
    }
}
