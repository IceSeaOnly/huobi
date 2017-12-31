package site.binghai.coin.client;

/**
 * Created by binghai on 2017/12/19.
 *
 * @ huobi
 */


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.coin.entity.AccountBalance;
import site.binghai.coin.request.CreateOrderRequest;
import site.binghai.coin.response.Account;
import site.binghai.coin.utils.HttpUtils;
import site.binghai.coin.utils.JsonUtil;

/**
 * API client.
 *
 * @author liaoxuefeng
 */
@Component
public class ApiClient {
    @Autowired
    private AuthParams authParams;

    static final String API_HOST = "api.huobi.pro";

    static final String API_URL = "https://" + API_HOST;

    private enum HttpType {
        GET,
        POST,;
    }

    /**
     * 查询所有账户信息
     *
     * @return List of accounts.
     */
    public List<Account> getAccounts() throws IOException {
        JSONObject object = jsonGet("/v1/account/accounts", null);
        JSONArray arr = object.getJSONArray("data");
        return arr.toJavaList(Account.class);
    }

    /**
     * 创建订单（未执行)
     *
     * @param request CreateOrderRequest object.
     * @return Order id.
     */
    public Long createOrder(CreateOrderRequest request) throws IOException {
        post("/v1/order/orders", request, null, String.class);
        return 1L;
    }

    /**
     * 执行订单
     *
     * @param orderId The id of created order.
     * @return Order id.
     */
    public String placeOrder(long orderId) throws IOException {
        return post("/v1/order/orders/" + orderId + "/place", null, null, String.class);
    }

    public AccountBalance accountBlance(long accountId) {
        JSONObject data = jsonGet("/v1/account/accounts/" + accountId + "/balance", null);
        return data.getObject("data", AccountBalance.class);
    }

    private JSONObject jsonGet(String uri, Map<String, Object> params) {
        return jsonCall(HttpType.GET, uri, null, params == null ? new HashMap<>() : params);
    }

    <T> T post(String uri, Object data, Map<String, Object> params, Class<T> clazz) throws IOException {
        return call(HttpType.POST, uri, data, params, clazz);
    }

    <T> T get(String uri, Map<String, Object> params, Class<T> clazz) throws IOException {
        return call(HttpType.GET, uri, null, params, clazz);
    }


    private JSONObject jsonCall(HttpType method, String uri, Object object, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        ApiSignature sign = new ApiSignature();
        sign.createSignature(authParams.getAccessKeyId(), authParams.getAccessKeySecret(), method == HttpType.GET ? "GET" : "POST", API_HOST, uri, params);
        String url = API_URL + uri;
        if (method == HttpType.POST) {
            params.putAll(JSONObject.parseObject(JSONObject.toJSONString(object)));
            return HttpUtils.sendJSONPost(url, toQueryString(params), null);
        } else {
            return HttpUtils.sendJSONGet(url, toQueryString(params), null);
        }
    }

    private <T> T call(HttpType method, String uri, Object object, Map<String, Object> params, Class<T> clazz) throws IOException {
        JSONObject jsonObject = jsonCall(method, uri, object, params);
        return JSONObject.parseObject(jsonObject.toJSONString(), clazz);
    }


    String authData() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(authParams.getAssetPassword().getBytes(StandardCharsets.UTF_8));
        md.update("hello, moto".getBytes(StandardCharsets.UTF_8));
        Map<String, String> map = new HashMap<>();
        map.put("assetPwd", DatatypeConverter.printHexBinary(md.digest()).toLowerCase());
        try {
            return ApiSignature.urlEncode(JsonUtil.writeValue(map));
        } catch (IOException e) {
            throw new RuntimeException("Get json failed: " + e.getMessage());
        }
    }

    String toQueryString(Map<String, Object> params) {
        return String.join("&", params.entrySet().stream().map((entry) -> {
            return entry.getKey() + "=" + ApiSignature.urlEncode(entry.getValue().toString());
        }).collect(Collectors.toList()));
    }
}





