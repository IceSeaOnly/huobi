package site.binghai.coin.common.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by binghai on 2018/1/8.
 *
 * @ huobi
 */
public class JSONPuter {
    private List<String> keys;
    private List<Object> values;

    public JSONPuter() {
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public JSONPuter put(String key, Object value) {
        keys.add(key);
        values.add(value);
        return this;
    }

    public JSONObject asResult() {
        JSONObject object = new JSONObject();
        for (int i = 0; i < keys.size(); i++) {
            object.put(keys.get(i), values.get(i));
        }
        return object;
    }

    public String asStringResult() {
        return asResult().toJSONString();
    }
}
