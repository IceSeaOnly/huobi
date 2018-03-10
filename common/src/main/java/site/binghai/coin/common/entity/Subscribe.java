package site.binghai.coin.common.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by binghai on 2018/3/10.
 * 订阅者
 *
 * @ huobi
 */
@Entity
@Data
public class Subscribe extends DeleteAble {
    @Id
    @GeneratedValue
    private Long id;
    private String openid;
    private Integer type; // 订阅目标

    public Subscribe(String openid, Integer type) {
        this.openid = openid;
        this.type = type;
    }

    public Subscribe() {
    }
}
