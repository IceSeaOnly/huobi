package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by binghai on 2018/1/13.
 *
 * @ huobi
 */
public abstract class BaseController {
    public Object jqueryBack(String callback, JSONArray arr) {
        return String.format("%s(%s)", callback, arr.toJSONString());
    }

    public Object jqueryBack(String callback, JSONObject arr) {
        return String.format("%s(%s)", callback, arr.toJSONString());
    }


    public JSONObject success(Object data, String msg) {
        return buildResult(true, data, msg);
    }

    public JSONObject success(String msg) {
        return buildResult(true, null, msg);
    }

    public JSONObject failed(Object data, String msg) {
        return buildResult(false, data, msg);
    }

    public JSONObject failed(String msg) {
        return buildResult(false, null, msg);
    }

    private JSONObject buildResult(boolean result, Object data, String msg) {
        JSONObject object = new JSONObject();
        object.put("msg", msg);
        object.put("data", data);
        object.put("result", result);
        object.put("status", result ? "SUCCESS" : "FAILED");

        return object;
    }
}
