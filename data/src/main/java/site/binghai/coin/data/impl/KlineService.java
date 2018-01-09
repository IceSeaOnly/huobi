package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.data.dao.KlineDao;

import java.util.ArrayList;
import java.util.List;

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

    public List<Kline> getKlineBetween(Symbol symbol, Long startTime, Long endTime) {
        if (endTime <= startTime) {
            return new ArrayList<>();
        }
        return klineDao.getByCreatedBetweenAndCoinNameAndQuoteCoinName(
                startTime, endTime, symbol.getBaseCurrency(), symbol.getQuoteCurrency());
    }
}
