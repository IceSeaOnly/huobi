package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.entity.TransactionMonitor;
import site.binghai.coin.data.dao.TransactionMonitorDao;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
@Service
public class TransactionMonitorService extends BaseService<TransactionMonitor> {

    @Autowired
    private TransactionMonitorDao dao;

    @Override
    JpaRepository<TransactionMonitor, Long> getDao() {
        return dao;
    }
}
