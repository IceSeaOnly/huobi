package site.binghai.coin.common.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by binghai on 2018/3/2.
 * 微信日内新值监控
 * @ huobi
 */
@Data
@Entity
public class WxSpy extends DeleteAble{
    @Id
    @GeneratedValue
    private Long id;
    private String baseCoin;
    private String quoteCoin;
    private String openId;
}
