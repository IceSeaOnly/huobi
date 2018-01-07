package site.binghai.coin.common.client;


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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.coin.common.defination.CallBack;
import site.binghai.coin.common.entity.AccountBalance;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.common.request.CreateOrderRequest;
import site.binghai.coin.common.response.Account;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.HttpUtils;
import site.binghai.coin.common.utils.JsonUtil;

/**
 * API client.
 *
 * @author liaoxuefeng
 */
@Component
public class ApiClient {
    private final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    @Autowired
    private AuthParams authParams;

    static final String API_HOST = "api.huobi.pro";

    static final String API_URL = "https://" + API_HOST;

    /**
     * ! 慎重操作
     * ! 市价梭哈
     */
    public Long allOnDealOf(Symbol symbol,double btcBalance,long accountId) throws IOException {
        if (btcBalance > 0) {
            CreateOrderRequest orderRequest = new CreateOrderRequest();
            orderRequest.setAccountId(String.valueOf(accountId));
            orderRequest.setSymbol(symbol.getBaseCurrency() + symbol.getQuoteCurrency());
            orderRequest.setType(CreateOrderRequest.OrderType.BUY_MARKET);
            orderRequest.setAmount(String.format("%.2f", btcBalance * 0.98));
            Long orderId = createOrder(orderRequest);

            if (orderId != null && orderId > 0) {
                logger.warn("创建订单成功! CreateOrderRequest:{} ,\n订单Id:{}", orderRequest, orderId);
                return orderId;
            } else {
                logger.error("创建订单失败 ! CreateOrderRequest:{}", orderRequest);
            }
        }
        return null;
    }

    /**
     * 坐等成交
     */
    public void waitOrderFilled(Long orderId, CallBack<HuobiOrder> callBack) throws IOException {
        int maxErrorRetry = 200;
        HuobiOrder huobiOrder = null;
        do {
            huobiOrder = queryOrder(orderId);
            if (huobiOrder != null) {
                logger.info("订单状态:{}", huobiOrder.getState());
            } else {
                logger.error("查询出错! orderId: {}", orderId);
                if (maxErrorRetry-- < 0) {
                    break;
                }
            }
        } while (huobiOrder == null || !huobiOrder.getState().equals("filled"));

        if (huobiOrder != null && huobiOrder.getType().equals("sell-limit")) {
            logger.info("交易完成!");
        } else if (huobiOrder != null) {
            callBack.onCallBack(huobiOrder);
        }
        logger.error("坐等成交出错...");
    }

    /**
     * 坐等高价卖出
     */
    public Long sellOrder(HuobiOrder order, double rate) throws IOException {
        if (rate < 1) {
            rate = 1.1;
        }

        double salePrice = Double.parseDouble(order.getPrice()) * rate;

        if (salePrice <= 0 || salePrice < Double.parseDouble(order.getPrice())) {
            logger.error("卖出价格不得低于买入价格!order: {}", order);
            return null;
        }

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setAccountId(String.valueOf(order.getAccountId()));
        orderRequest.setSymbol(order.getSymbol());
        orderRequest.setType(CreateOrderRequest.OrderType.SELL_LIMIT);
        orderRequest.setPrice(String.valueOf(salePrice));
        orderRequest.setAmount(order.getAmount());
        Long orderId = createOrder(orderRequest);

        if (orderId != null && orderId > 0) {
            logger.warn("卖出订单创建成功! CreateOrderRequest:{} ,\n订单Id:{}", orderRequest, orderId);
            return orderId;
        } else {
            logger.error("卖出订单创建失败 ! CreateOrderRequest:{}", orderRequest);
        }

        return null;
    }

    public double getBtcBalance() throws IOException {
        double[] rs = {0.0};
        List<Account> accounts = getAccounts();
        accounts.forEach(account -> {
            if (account.getType().equals("spot")) {
                AccountBalance balance = accountBlance(account.getId());
                balance.getList().forEach(cc -> {
                    if (cc.getCurrency().toUpperCase().equals("BTC")) {
                        rs[0] += cc.getBalance();
                    }
                });
            }
        });
        return rs[0];
    }

    public long getBtcAccountId() throws IOException {
        long[] rs = {0};
        List<Account> accounts = getAccounts();
        accounts.forEach(account -> {
            if (account.getType().equals("spot")) {
                AccountBalance balance = accountBlance(account.getId());
                balance.getList().forEach(cc -> {
                    if (cc.getCurrency().toUpperCase().equals("BTC") && cc.getBalance() > 0) {
                        rs[0] = account.getId();
                    }
                });
            }
        });
        return rs[0];
    }

    private enum HttpType {
        GET,
        POST,;
    }

    /**
     * 查询订单
     */
    public HuobiOrder queryOrder(Long orderId) {
        JSONObject data = jsonGet("/v1/order/orders/" + orderId, null);
        if (data != null && "ok".equals(data.getString("status"))) {
            return data.getObject("data", HuobiOrder.class);
        }
        return null;
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
        JSONObject json = jsonCall(HttpType.POST, "/v1/order/orders/place", request, null);
        if (json != null && "ok".equals(json.getString("status"))) {
            return Long.parseLong(json.getString("data"));
        }
        logger.error("创建订单失败,request:{}, response:{}", request, json);
        return -1L;
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
            return HttpUtils.sendJSONPost(url, toQueryString(params), JSONObject.toJSONString(object), null);
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





