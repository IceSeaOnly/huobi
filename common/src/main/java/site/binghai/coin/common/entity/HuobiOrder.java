package site.binghai.coin.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by binghai on 2018/1/6.
 * 订单
 * @ huobi
 */
@Data
public class HuobiOrder {
    private Long id;
    private String symbol;
    @JSONField(name = "account-id")
    private String accountId;
    private String amount;
    private String price;
    @JSONField(name = "created-at")
    private Long createdAt;
    private String type;
    @JSONField(name = "field-amount")
    private String fieldAmount;
    @JSONField(name = "field-cash-amount")
    private String fieldCashAmount;
    @JSONField(name = "field-fees")
    private String fieldFees;
    @JSONField(name = "finished-at")
    private Long finishedAt;
    @JSONField(name = "user-id")
    private Integer userId;
    private String source;
    private String state;
    @JSONField(name = "canceled-at")
    private Long canceledAt;
    private String exchange;
    private String batch;
}
