package site.binghai.coin.common.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by binghai on 2018/2/24.
 *
 * @ huobi
 */
@Entity
@Data
public class Admin extends DeleteAble {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;

    @Override
    public long getId() {
        return id;
    }
}
