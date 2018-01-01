package site.binghai.coin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.entity.Kline;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
public interface KlineDao extends JpaRepository<Kline, Long> {

}
