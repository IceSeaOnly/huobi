package site.binghai.coin.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.binghai.coin.common.entity.JinSeNew;
import site.binghai.coin.common.entity.WaterLevelMonitor;

import java.util.List;

/**
 * Created by binghai on 2018/3/10.
 *
 * @ huobi
 */
@Service
public class WxNoticeService extends BaseWxNoticeService {
    public void wxNoticeWaterLevelMonitoring(WaterLevelMonitor v, String nowValue, String firstText, String remark) {
        if (null == v || null == v.getWxNotice() || "#".equals(v.getWxNotice())) {
            return;
        }

        JSONObject ds = new JSONObject();
        ds.put("type", "wxnotice");
        JSONArray arr = new JSONArray();
        String[] openids = v.getWxNotice().split(",");
        for (String openid : openids) {
            JSONObject data = new JSONObject();
            String coin = (v.getBaseCoin() + "/" + v.getQuoteCoin()).toUpperCase();
            String first = String.format("%s 监控值已经到达 %s !", coin, v.getTargetValue());
            data.put("first", newItem(first + firstText, "#EE0000"));
            data.put("keyword1", "火币Pro");
            data.put("keyword2", newItem(coin));
            data.put("keyword3", newItem(v.getTargetValue()));
            String now = TimeFormat.format(System.currentTimeMillis());
            data.put("keyword4", newItem(now));
            data.put("keyword5", newItem("已到达设定目标"));
            String remarks = String.format("您%s创建的%s 在 %s 的水位监控已于 %s 达到，当前值 %s", v.getCreatedTime(), coin, v.getTargetValue(), now, nowValue);
            data.put("remark", newItem(remarks + remark));
            arr.add(commonTPLMaker(mnsParams.getWxWaterMonitorTpl(), openid, "", data));
        }

        ds.put("datas", arr);
        sendToNoticeServer(ds);
    }

    /**
     * 日内新值
     */
    public void NewRecordInTheDay(List<String> openids, double max, double min, String value, String coinName, boolean isHigh) {
        if (StringUtils.isEmpty(openids) || openids.equals("#")) {
            return;
        }
        JSONObject ds = new JSONObject();
        ds.put("type", "wxnotice");
        JSONArray arr = new JSONArray();

        String coin = coinName.toUpperCase();
        for (String openid : openids) {
            String first = String.format("%s 达到日内新%s! %s !", coin, isHigh ? "高" : "低", value);
            String range = String.format(" 日内波动: %f ~ %f", min, max);
            arr.add(priceChangeWxNotice(openid, coin, first, value, null, first + range, ""));
        }
        ds.put("datas", arr);
        sendToNoticeServer(ds);
    }

    private JSONObject priceChangeWxNotice(String openid, String coinName, String first, String targetValue, String time, String remark, String url) {
        JSONObject data = new JSONObject();
        String coin = coinName.toUpperCase();
        data.put("first", newItem(first, "#EE0000"));
        data.put("keyword1", newItem(coin, "#00CD00"));
        data.put("keyword2", newItem(targetValue, "#EE0000"));
        String now = TimeFormat.format(System.currentTimeMillis());
        data.put("keyword3", newItem(time == null ? now : time));
        data.put("remark", newItem(remark));
        return commonTPLMaker(mnsParams.getWxTpl(), openid, url, data);
    }

    public void noticeEmergenceNews(JinSeNew v,List<String> openids) {
        JSONObject ds = new JSONObject();
        ds.put("type", "wxnotice");
        JSONArray arr = new JSONArray();

        openids.forEach(openid ->{
            JSONObject data = new JSONObject();

            data.put("first", newItem(v.getContent(), "#EE0000"));
            data.put("keyword1", newItem("金日财经突发事件", "#00CD00"));
            data.put("keyword2", newItem("金日财经", "#EE0000"));
            String now = TimeFormat.format(v.getCreated());
            data.put("keyword3", newItem(now));
            data.put("keyword4", newItem("请看正文"));
            data.put("remark", newItem(v.getContent()));

            arr.add(commonTPLMaker(mnsParams.getWxEmergenceNewsTpl(), openid, mnsParams.getServerHead()+"/open/jinseDetail?id="+v.getId(), data));
        });

        ds.put("datas", arr);
        sendToNoticeServer(ds);
    }
}
