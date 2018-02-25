package site.binghai.coin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.entity.TransactionMonitor;

/**
 * Created by binghai on 2018/2/25.
 *
 * @ huobi
 */
public interface TransactionMonitorDao extends JpaRepository<TransactionMonitor,Long> {
}
