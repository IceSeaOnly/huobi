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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.entity.Coin;
import site.binghai.coin.entity.CoinBalance;
import site.binghai.coin.request.CreateOrderRequest;
import site.binghai.coin.response.Account;
import site.binghai.coin.response.ApiResponse;
import site.binghai.coin.response.Symbol;
import site.binghai.coin.utils.JsonUtil;

/**
 * API client.
 *
 * @author liaoxuefeng
 */
@Component
public class ApiClient implements InitializingBean {
    public static ApiClient commonClient;
    public static CoreParams commonCoreParams;
    @Autowired
    private AuthParams authParams;
    @Autowired
    private CoreParams coreParams;

    static final int CONN_TIMEOUT = 5;
    static final int READ_TIMEOUT = 5;
    static final int WRITE_TIMEOUT = 5;

    static final String API_HOST = "api.huobi.pro";

    static final String API_URL = "https://" + API_HOST;
    static final MediaType JSON = MediaType.parse("application/json");
    static final OkHttpClient client = createOkHttpClient();

    /**
     * 查询交易对
     *
     * @return List of symbols.
     */
    public List<Symbol> getSymbols() {
        ApiResponse<List<Symbol>> resp =
                get("/v1/common/symbols", null, new TypeReference<ApiResponse<List<Symbol>>>() {
                });
        return resp.checkAndReturn();
    }

    /**
     * 查询所有账户信息
     *
     * @return List of accounts.
     */
    public List<Account> getAccounts() {
        ApiResponse<List<Account>> resp =
                get("/v1/account/accounts", null, new TypeReference<ApiResponse<List<Account>>>() {
                });
        return resp.checkAndReturn();
    }

    /**
     * 创建订单（未执行)
     *
     * @param request CreateOrderRequest object.
     * @return Order id.
     */
    public Long createOrder(CreateOrderRequest request) {
        ApiResponse<Long> resp =
                post("/v1/order/orders", request, new TypeReference<ApiResponse<Long>>() {
                });
        return resp.checkAndReturn();
    }

    /**
     * 获取此币最近一分钟的估值
     */
    public JSONObject currentPriceValuation(Coin coin) {
        Map params = new HashMap();
        params.put("period", "1min");
        params.put("size", "1");
        params.put("symbol", coin.getCurrency().toLowerCase() + "btc");
        JSONObject obj = jsonGet("/market/history/kline", params);
        if (obj.getString("status").equals("ok")) {
            if (!CollectionUtils.isEmpty(obj.getJSONArray("data"))) {
                return obj.getJSONArray("data").getJSONObject(0);
            }
        }

        return null;
    }

    /**
     * 执行订单
     *
     * @param orderId The id of created order.
     * @return Order id.
     */
    public String placeOrder(long orderId) {
        ApiResponse<String> resp = post("/v1/order/orders/" + orderId + "/place", null,
                new TypeReference<ApiResponse<String>>() {
                });
        return resp.checkAndReturn();
    }

    public CoinBalance allMyBlance(long accountId) {
        JSONObject data = jsonGet("/v1/account/accounts/" + accountId + "/balance", null);
        return JSONObject.parseObject(data.getJSONObject("data").toJSONString(), CoinBalance.class);
    }

    private JSONObject jsonGet(String uri, Map<String, String> params) {
        return jsonCall("GET", uri, null, params == null ? new HashMap<>() : params);
    }

    <T> T get(String uri, Map<String, String> params, TypeReference<T> ref) {
        if (params == null) {
            params = new HashMap<>();
        }
        return call("GET", uri, null, params, ref);
    }

    // send a POST request.
    <T> T post(String uri, Object object, TypeReference<T> ref) {
        return call("POST", uri, object, new HashMap<String, String>(), ref);
    }


    private JSONObject jsonCall(String method, String uri, Object object, Map<String, String> params) {
        return JSONObject.parseObject(rawCall(method, uri, object, params));
    }


    private String rawCall(String method, String uri, Object object, Map<String, String> params) {
        ApiSignature sign = new ApiSignature();
        sign.createSignature(authParams.getAccessKeyId(), authParams.getAccessKeySecret(), method, API_HOST, uri, params);
        try {
            Request.Builder builder = null;
            if ("POST".equals(method)) {
                RequestBody body = RequestBody.create(JSON, JsonUtil.writeValue(object));
                builder = new Request.Builder().url(API_URL + uri + "?" + toQueryString(params)).post(body);
            } else {
                builder = new Request.Builder().url(API_URL + uri + "?" + toQueryString(params)).get();
            }
            if (authParams.getAssetPassword() != null) {
                builder.addHeader("AuthData", authData());
            }
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    private <T> T call(String method, String uri, Object object, Map<String, String> params,
                       TypeReference<T> ref) {
        try {
            return JsonUtil.readValue(rawCall(method, uri, object, params), ref);
        } catch (IOException e) {
            throw new ApiException(e);
        }
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

    // Encode as "a=1&b=%20&c=&d=AAA"
    String toQueryString(Map<String, String> params) {
        return String.join("&", params.entrySet().stream().map((entry) -> {
            return entry.getKey() + "=" + ApiSignature.urlEncode(entry.getValue());
        }).collect(Collectors.toList()));
    }

    // create OkHttpClient:
    static OkHttpClient createOkHttpClient() {
        return new Builder().connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS).writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        commonClient = this;
        commonCoreParams = this.coreParams;
    }
}





