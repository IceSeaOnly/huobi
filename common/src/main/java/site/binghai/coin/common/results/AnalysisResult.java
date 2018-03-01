package site.binghai.coin.common.results;

import lombok.Data;
import site.binghai.coin.common.entity.DeleteAble;
import site.binghai.coin.common.response.Symbol;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by binghai on 2018/1/8.
 * 分析结果
 *
 * @ huobi
 */
@Entity
@Data
public class AnalysisResult extends DeleteAble {
    @Id
    @GeneratedValue
    private Long id;
    /**
     * 固定字段 begin
     */
    private String baseCoin;
    private String quoteCoin;
    private int batchNumber; // 日期批次号,如20180101
    private String analysisLevel; // 分析时间级别
    private long analysisStart; // 分析开始时间
    private long analysisEnd; // 分析结束时间
    private long timeConsuming; // 分析耗时
    /**
     * 固定字段 end
     */

    // 以下为扩展字段
    @Column(columnDefinition = "text")
    private String extra; // 额外描述
    private double standard; // 方差基准
    private double variance; // 方差
}
