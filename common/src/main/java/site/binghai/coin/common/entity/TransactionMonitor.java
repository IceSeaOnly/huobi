package site.binghai.coin.common.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@Data
@Entity
public class TransactionMonitor extends DeleteAble{
    @Id
    @GeneratedValue
    private Long id;
    private String market;
    private String orderNo;
    private String targetStatus; // 目标状态
    private String targetValue; // 目标值
    private String expectedEarning; // 预期收益，USDT
    private String addType; // 手动添加、自动添加
    private boolean completed; // 是否完成
    private String startMonitor;
    private String endMonitor;
    @Column(columnDefinition = "TEXT")
    private String extra;

    @Override
    public Long getId() {
        return id;
    }
}
