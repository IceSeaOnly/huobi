package site.binghai.coin.data.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.entity.JinSeNew;

import java.util.List;

/**
 * Created by binghai on 2018/3/10.
 *
 * @ huobi
 */
public interface JinSeDao extends JpaRepository<JinSeNew,Long> {
    List<JinSeNew> findByHashCode(String hash);
    List<JinSeNew> findAllByOrderByIdDesc(Pageable pageable);
    List<JinSeNew> findAllByCreatedAfter(long after);
}