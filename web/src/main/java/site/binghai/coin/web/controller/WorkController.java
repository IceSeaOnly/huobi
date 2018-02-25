package site.binghai.coin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.coin.data.impl.MemberCacheService;

/**
 * Created by binghai on 2018/2/24.
 *
 * @ huobi
 */
@RequestMapping("admin")
@Controller
public class WorkController extends BaseController{

    @Autowired
    private MemberCacheService memberCacheService;

    @RequestMapping("index")
    public String index(){

        return "works";
    }


    @RequestMapping("floatTop10")
    @ResponseBody
    public Object floatTop10(){
        return success(memberCacheService.get(MemberCacheService.CacheKeys.FLOAT_TOP_10),"success");
    }

}
