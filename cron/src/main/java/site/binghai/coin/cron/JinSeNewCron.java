package site.binghai.coin.cron;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.coin.common.entity.JinSeNew;
import site.binghai.coin.common.entity.Subscribe;
import site.binghai.coin.common.enums.SubscribeType;
import site.binghai.coin.common.utils.HttpUtils;
import site.binghai.coin.common.utils.MD5;
import site.binghai.coin.common.utils.WxNoticeService;
import site.binghai.coin.data.impl.JinSeNewService;
import site.binghai.coin.data.impl.SubscribeService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2018/3/10.
 *
 * @ huobi
 */
@Component
public class JinSeNewCron {
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private JinSeNewService jinSeNewService;
    @Autowired
    private WxNoticeService wxNoticeService;

    @Scheduled(cron = "0 * * * * ?")
    public void scan() {
        JSONObject resp = HttpUtils.sendJSONGet("http://api.jinse.com/v3/live/list", "limit=20&flag=up", null);
        if (resp == null) {
            return;
        }

        JSONArray array = resp.getJSONArray("list").getJSONObject(0).getJSONArray("lives");
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String hash = MD5.encryption(obj.getString("content"));
            JinSeNew it = jinSeNewService.findByHash(hash);
            if (it == null) {
                JinSeNew one = new JinSeNew(
                        obj.getString("content"),
                        obj.getInteger("grade"),
                        obj.getLong("created_at"),
                        obj.getInteger("up_counts"),
                        obj.getInteger("down_counts"));

                jinSeNewService.save(one);
            } else {
                it.setUp_counts(obj.getInteger("up_counts"));
                it.setDown_counts(obj.getInteger("down_counts"));
                it.setGrade(obj.getInteger("grade"));
                jinSeNewService.update(it);
            }
        }

        checkAndNotice();
    }

    private void checkAndNotice() {
        List<JinSeNew> lasted = jinSeNewService.lasted50();
        if (null == lasted) return;

        List<Subscribe> subscribes = subscribeService.findByType(SubscribeType.EmergenceNews);
        if(null == subscribes){
            return;
        }
        List<String> openids = subscribes.stream().map(v -> v.getOpenid()).collect(Collectors.toList());

        lasted.forEach(v -> {
            if(v.getGrade() > 4 && !v.isNoticed()){
                wxNoticeService.noticeEmergenceNews(v,openids);
                v.setNoticed(true);
                jinSeNewService.update(v);
            }
        });
    }
}
