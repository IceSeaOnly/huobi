package site.binghai.coin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.entity.WaterLevelMonitor;

import java.util.List;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
public interface WaterLevelMonitorDao extends JpaRepository<WaterLevelMonitor,Long> {
    List<WaterLevelMonitor> findByWxNotice(String openid);
}
