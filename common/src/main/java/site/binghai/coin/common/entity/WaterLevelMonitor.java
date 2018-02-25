package site.binghai.coin.common.entity;

import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@Entity
@Data
public class WaterLevelMonitor extends DeleteAble {
    @Id
    @GeneratedValue
    private Long id;
    private Double targetValue;
    private String baseCoin;
    private String quoteCoin;
    private String completeTime;
    private boolean complete;
    private String notice; // 手机号，逗号分隔

    @Override
    public long getId() {
        return id;
    }

    public List<String> getPhoneList() {
        if (!StringUtils.isEmpty(notice)) {
            return Arrays.asList(notice.split(","));
        }
        return null;
    }
}
