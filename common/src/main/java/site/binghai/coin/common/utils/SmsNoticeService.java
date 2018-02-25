package site.binghai.coin.common.utils;

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

    public void WaterLevelMonitoring(List<String> phones, String setTime, String coinName, String standard, String nowValue) {
        phones.forEach(v -> WaterLevelMonitoring(v, setTime, coinName, standard, nowValue));
    }

    public void WaterLevelMonitoring(String phone, String setTime, String coinName, String standard, String nowValue) {
        if(StringUtils.isEmpty(phone)){return;}

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

        Message message = new Message();
        message.setMessageBody(object.toJSONString());
        CloudQueue queue = client.getQueueRef(mnsParams.getQueueName());
        queue.putMessage(message);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        account = new CloudAccount(mnsParams.getAccesskeyid(), mnsParams.getAccesskeysecret(), mnsParams.getAccountendpoint());
        client = account.getMNSClient();
    }

    public void WaterLevelMonitoring(WaterLevelMonitor v,String nowValue) {
        WaterLevelMonitoring(v.getPhoneList(),v.getCreatedTime(),(v.getBaseCoin()+v.getQuoteCoin()).toLowerCase(),v.getTargetValue().toString(),nowValue);
    }
}
