package site.binghai.coin.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import site.binghai.coin.common.client.MnsParams;

/**
 * Created by binghai on 2018/3/10.
 *
 * @ huobi
 */
public abstract class BaseWxNoticeService  implements InitializingBean {
    protected static CloudAccount account = null;
    protected static MNSClient client = null;
    @Autowired
    protected MnsParams mnsParams;

    protected void sendToNoticeServer(JSONObject object) {
        Message message = new Message();
        message.setMessageBody(object.toJSONString());
        CloudQueue queue = client.getQueueRef(mnsParams.getQueueName());
        queue.putMessage(message);
    }

    protected JSONObject commonTPLMaker(String tpl, String openid, String url, JSONObject data) {
        JSONObject json = new JSONObject();
        json.put("touser", openid);//OPENID
        json.put("template_id", tpl);
        json.put("url", url);
        json.put("data", data);
        return json;
    }

    protected JSONObject newItem(Object val, String color) {
        JSONObject it = new JSONObject();
        it.put("value", val);
        it.put("color", color);
        return it;
    }

    protected JSONObject newItem(Object val) {
        JSONObject it = new JSONObject();
        it.put("value", val);
        return it;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        account = new CloudAccount(mnsParams.getAccesskeyid(), mnsParams.getAccesskeysecret(), mnsParams.getAccountendpoint());
        client = account.getMNSClient();
    }
}
