package site.binghai.coin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.binghai.coin.common.entity.Kline;

import java.util.List;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
public interface KlineDao extends JpaRepository<Kline, Long> {
    List<Kline> getByIdBetweenAndCoinNameAndQuoteCoinName(long start, long end, String baseName, String quoteName);
    List<Kline> getByCreatedBetweenAndCoinNameAndQuoteCoinName(long start, long end, String baseName, String quoteName);
    @Query(value = "select * from kline where coin_name=:bcoin and quote_coin_name=:qcoin order by main_id desc limit 1",nativeQuery = true)
    List<Kline> getLastestKline(@Param("bcoin") String baseName,@Param("qcoin") String quoteName);
}
