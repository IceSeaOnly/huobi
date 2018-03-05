package site.binghai.coin.cron;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.entity.HuobiOrder;
import site.binghai.coin.common.utils.TimeFormat;
import site.binghai.coin.data.impl.MemberCacheService;

import java.util.ArrayList;
import java.util.List;

import static site.binghai.coin.common.utils.CommonUtils.cmpLong2int;
import static site.binghai.coin.common.utils.CommonUtils.removeZero;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@Component
public class RefreshHistoryOrders implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(RefreshHistoryOrders.class);

    @Autowired
    private MemberCacheService memberCacheService;
    @Autowired
    private ApiClient apiClient;


    @Scheduled(cron = "0 * * * * ?")
    public void refresh() {
        List<JSONObject> datas = new ArrayList<>();

        List<HuobiOrder> all = new ArrayList<>();

        List<HuobiOrder> tmp = apiClient.listOrder("usdt");
        if (!CollectionUtils.isEmpty(tmp)) {
            all.addAll(tmp);
        }

        tmp = apiClient.listOrder("btc");
        if (!CollectionUtils.isEmpty(tmp)) {
            all.addAll(tmp);
        }

        all.sort((a, b) -> cmpLong2int(b.getCreatedAt() - a.getCreatedAt()));
        all.forEach(v -> {
            JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(v));
            obj.put("createdAt", TimeFormat.format(obj.getLong("created-at")));
            obj.put("canceledAt", TimeFormat.format(obj.getLong("canceled-at")));
            obj.put("finishedAt", TimeFormat.format(obj.getLong("finished-at")));
            obj.put("accountId", obj.getString("account-id"));
            obj.put("symbol", obj.getString("symbol").toUpperCase());
            obj.put("amount", removeZero(obj.getString("amount")));
            obj.put("price", removeZero(obj.getString("price")));
            obj.put("fieldAmount", removeZero(obj.getString("field-amount")));
            obj.put("fieldCashAmount", removeZero(obj.getString("field-cash-amount")));
            obj.put("fieldFees", removeZero(obj.getString("field-fees")));

            datas.add(obj);
        });


        logger.info("历史订单列表刷新,size = {}", all.size());
        memberCacheService.put(MemberCacheService.CacheKeys.LIST_ALL_ORDERS, datas);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        refresh();
    }
}
