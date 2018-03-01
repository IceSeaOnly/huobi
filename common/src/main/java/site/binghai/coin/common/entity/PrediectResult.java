package site.binghai.coin.common.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by binghai on 2018/1/9.
 *
 * @ huobi
 */
@Entity
@Data
public class PrediectResult extends DeleteAble{
    @Id
    @GeneratedValue
    private Long id;
    private String batchNumber;
    private String analysisLevel;
    private String baseCoin;
    private String quoteCoin;
    private String result;

    private int resultCode; // 预测结果，0 持平，负数下跌，正数上涨
    private int confidence; // 置信度
    private long nextStart; // 预测结果比对开始时间
    private long nextEnd; // 预测结果比对开始时间
    private boolean success;// 是否准确判断
    private long confirmTime; // 结果确认时间
}
