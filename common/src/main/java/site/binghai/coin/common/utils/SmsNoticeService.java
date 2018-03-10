package site.binghai.coin.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.binghai.coin.common.entity.WaterLevelMonitor;

import java.util.List;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@Service
public class SmsNoticeService extends BaseWxNoticeService {

    private void WaterLevelMonitoring(List<String> phones, String setTime, String coinName, String standard, String nowValue) {
        if (phones == null || phones.size() == 0) return;
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

    public void WaterLevelMonitoring(WaterLevelMonitor v, String nowValue) {
        WaterLevelMonitoring(v.getPhoneList(), v.getCreatedTime(), (v.getBaseCoin() + v.getQuoteCoin()).toLowerCase(), v.getTargetValue().toString(), nowValue);
    }
}
