package site.binghai.coin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.entity.WxSpy;

import java.util.List;

/**
 * Created by binghai on 2018/3/2.
 *
 * @ huobi
 */
public interface WxSpyDao extends JpaRepository<WxSpy,Long> {
    List<WxSpy> findByOpenId(String openid);
}
