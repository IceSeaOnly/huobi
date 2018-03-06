package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.entity.WaterLevelMonitor;
import site.binghai.coin.data.dao.WaterLevelMonitorDao;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@Service
public class WaterLevelMonitorService extends BaseService<WaterLevelMonitor> {

    @Autowired
    private WaterLevelMonitorDao dao;

    @Override
    JpaRepository<WaterLevelMonitor, Long> getDao() {
        return dao;
    }

    public List<WaterLevelMonitor> findAllNotComplete() {
        return dao.findAll()
                .stream()
                .filter(v -> !v.isComplete())
                .collect(Collectors.toList());
    }


    public List<WaterLevelMonitor> findByOpenId(String openid){
        return dao.findByWxNotice(openid);
    }

}
