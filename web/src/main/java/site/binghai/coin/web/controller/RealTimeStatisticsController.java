package site.binghai.coin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.data.impl.MemberCacheService;


/**
 * Created by IceSea on 2018/3/21.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("admin")
public class RealTimeStatisticsController extends BaseController {

    @Autowired
    private MemberCacheService memberCacheService;

    @RequestMapping("readTimeStatistics")
    public Object readTimeStatistics() {
        Object obj = memberCacheService.get(MemberCacheService.CacheKeys.REAL_TIME_STATISTICS);
        return obj == null ? success("EMPTY DATA!") : success(obj, "SUCCESS");
    }
}
