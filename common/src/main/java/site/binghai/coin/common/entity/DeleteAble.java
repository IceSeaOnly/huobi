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
    public Long created;
    public String createdTime;

    public abstract Long getId();

    public DeleteAble() {
        created = System.currentTimeMillis();
        createdTime = TimeFormat.format(created);
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
