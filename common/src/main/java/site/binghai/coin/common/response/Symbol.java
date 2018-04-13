package site.binghai.coin.common.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

/**
 * Created by binghai on 2017/12/19.
 * base-currency 基础币种
 * quote-currency 计价币种
 * price-precision 价格精度位数（0为个位）
 * amount-precision 数量精度位数（0为个位）
 * symbol-partition main主区，innovation创新区，bifurcation分叉区
 *
 * @ huobi
 */
@Data
@ToString
public class Symbol {
    @JSONField(name = "base-currency")
    private String baseCurrency;
    @JSONField(name = "quote-currency")
    private String quoteCurrency;
    @JSONField(name = "symbol-partition")
    private String symbolPartition;
    @JSONField(name = "price-precision")
    private int pricePrecision;
    @JSONField(name = "amount-precision")
    private int amountPrecision;

    private double rise;//涨幅

    public Symbol() {
    }

    public Symbol(String baseCurrency, String quoteCurrency) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

    public String getSimpleName() {
        return (baseCurrency + "/" + quoteCurrency).toUpperCase();
    }

    public String toStringName() {
        return (baseCurrency + quoteCurrency).toUpperCase();
    }
}
