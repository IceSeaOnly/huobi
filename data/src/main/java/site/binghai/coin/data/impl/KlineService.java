package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.data.dao.KlineDao;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@Service
public class KlineService extends BaseService<Kline> {
    @Autowired
    private KlineDao klineDao;

    @Override
    JpaRepository<Kline, Long> getDao() {
        return klineDao;
    }
}
