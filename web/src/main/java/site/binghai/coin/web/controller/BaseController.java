package site.binghai.coin.web.controller;

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
}
