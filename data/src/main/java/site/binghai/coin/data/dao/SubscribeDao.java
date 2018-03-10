package site.binghai.coin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.entity.Subscribe;

import java.util.List;

/**
 * Created by binghai on 2018/3/10.
 *
 * @ huobi
 */
public interface SubscribeDao extends JpaRepository<Subscribe,Long> {
    List<Subscribe> findByOpenidAndType(String openId, int type);
    List<Subscribe> findByType(int type);
}
