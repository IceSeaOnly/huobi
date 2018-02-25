package site.binghai.coin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.data.impl.MemberCacheService;

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


    @RequestMapping("orderList")
    public Object orderList(Integer listType){
        if(listType == null){
            listType = 0;
        }


        return success(memberCacheService.get(MemberCacheService.CacheKeys.LIST_ALL_ORDERS),"ok");
    }
}
