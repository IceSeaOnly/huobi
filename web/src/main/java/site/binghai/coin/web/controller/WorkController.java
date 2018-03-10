package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.data.impl.JinSeNewService;
import site.binghai.coin.data.impl.MemberCacheService;

/**
 * Created by binghai on 2018/2/24.
 *
 * @ huobi
 */
@RequestMapping("admin")
@RestController
public class WorkController extends BaseController{

    @Autowired
    private JinSeNewService jinSeNewService;
    @Autowired
    private MemberCacheService memberCacheService;


    @RequestMapping("floatTop10")
    public Object floatTop10(){
        return success(memberCacheService.get(MemberCacheService.CacheKeys.FLOAT_TOP_10),"success");
    }

    @RequestMapping("jinseNews")
    public Object jinseNews(@RequestParam Integer pageSize,@RequestParam Integer page){
        JSONObject object = new JSONObject();

        object.put("page",page);
        object.put("list",jinSeNewService.findPageAbleDesc(pageSize,page));
        object.put("total",jinSeNewService.count());
        object.put("pageSize",pageSize);

        return success(object,"success");
    }
}
