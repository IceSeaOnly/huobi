package site.binghai.coin.common.entity;

import site.binghai.coin.common.utils.TimeFormat;

import javax.persistence.MappedSuperclass;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@MappedSuperclass
public abstract class DeleteAble {
    public long created;
    public String createdTime;

    public abstract long getId();

    public DeleteAble() {
        created = System.currentTimeMillis();
        createdTime = TimeFormat.format(created);
    }
}
