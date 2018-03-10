package site.binghai.coin.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.binghai.coin.common.client.MnsParams;
import site.binghai.coin.common.entity.WaterLevelMonitor;

import java.util.List;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@Service
public class SmsNoticeService implements InitializingBean {
    private static CloudAccount account = null;
    private static MNSClient client = null;
    @Autowired
    private MnsParams mnsParams;

    private void WaterLevelMonitoring(List<String> phones, String setTime, String coinName, String standard, String nowValue) {
        if(phones == null || phones.size() == 0) return;
        phones.forEach(v -> WaterLevelMonitoring(v, setTime, coinName, standard, nowValue));
    }

    private void WaterLevelMonitoring(String phone, String setTime, String coinName, String standard, String nowValue) {
        if (StringUtils.isEmpty(phone)) {
            return;
        }
        if (phone.length() != 11) {
            return;
        }

        JSONObject object = new JSONObject();
        object.put("phone", phone);

        JSONObject params = new JSONObject();
        params.put("setTime", setTime);
        params.put("type", coinName);
        params.put("stand", standard);
        params.put("value", nowValue);
        params.put("now", TimeFormat.format(System.currentTimeMillis()));

        object.put("param", params);
        object.put("type", "sms");
        object.put("tpl", mnsParams.getTpl());

        sendToNoticeServer(object);
    }

    private void sendToNoticeServer(JSONObject object) {
        Message message = new Message();
        message.setMessageBody(object.toJSONString());
        CloudQueue queue = client.getQueueRef(mnsParams.getQueueName());
        queue.putMessage(message);
    }

    private JSONObject commonTPLMaker(String tpl, String openid, String url, JSONObject data) {
        JSONObject json = new JSONObject();
        json.put("touser", openid);//OPENID
        json.put("template_id", tpl);
        json.put("url", url);
        json.put("data", data);
        return json;
    }

    private JSONObject newItem(Object val, String color) {
        JSONObject it = new JSONObject();
        it.put("value", val);
        it.put("color", color);
        return it;
    }

    private JSONObject newItem(Object val) {
        JSONObject it = new JSONObject();
        it.put("value", val);
        return it;
    }

    public void wxNoticeWaterLevelMonitoring(WaterLevelMonitor v, String nowValue, String firstText, String remark) {
        if (null == v || null == v.getWxNotice()|| "#".equals(v.getWxNotice())) {
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
            data.put("keyword1", newItem(coin));
            data.put("keyword2", newItem(v.getTargetValue()));
            String now = TimeFormat.format(System.currentTimeMillis());
            data.put("keyword3", newItem(now));
            String remarks = String.format("您%s创建的%s 在 %s 的水位监控已于 %s 达到，当前值 %s", v.getCreatedTime(), coin, v.getTargetValue(), now, nowValue);
            data.put("remark", newItem(remarks + remark));
            arr.add(commonTPLMaker(mnsParams.getWxTpl(), openid, "", data));
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

    @Override
    public void afterPropertiesSet() throws Exception {
        account = new CloudAccount(mnsParams.getAccesskeyid(), mnsParams.getAccesskeysecret(), mnsParams.getAccountendpoint());
        client = account.getMNSClient();
    }

    public void WaterLevelMonitoring(WaterLevelMonitor v, String nowValue) {
        WaterLevelMonitoring(v.getPhoneList(), v.getCreatedTime(), (v.getBaseCoin() + v.getQuoteCoin()).toLowerCase(), v.getTargetValue().toString(), nowValue);
    }
}
