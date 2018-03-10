package site.binghai.coin.common.entity;

import lombok.Data;
import site.binghai.coin.common.utils.MD5;
import site.binghai.coin.common.utils.TimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by binghai on 2018/3/10.
 * 金色财经
 *
 * @ huobi
 */
@Data
@Entity
public class JinSeNew extends DeleteAble {
    @Id
    @GeneratedValue
    private Long id;
    private String content;
    private int grade;
    private long created_at;
    private int up_counts; // 利好观点书
    private int down_counts; // 利空观点数
    private String hashCode; // content hash结果
    private boolean noticed; // 如果是5星事件，需要通知

    public JinSeNew(String content, int grade, long created_at, int up_counts, int down_counts) {
        this.content = content;
        this.grade = grade;
        this.created_at = created_at;
        this.up_counts = up_counts;
        this.down_counts = down_counts;
        created = created_at * 1000;
        createdTime = TimeFormat.format(created);
        this.noticed = false;
        this.hashCode = MD5.encryption(content);
    }

    public JinSeNew() {
    }
}
