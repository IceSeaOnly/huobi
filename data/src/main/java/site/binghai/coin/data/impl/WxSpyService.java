package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.entity.WxSpy;
import site.binghai.coin.data.dao.WxSpyDao;

/**
 * Created by binghai on 2018/3/2.
 *
 * @ huobi
 */
@Service
public class WxSpyService extends BaseService<WxSpy> {
    @Autowired
    private WxSpyDao dao;

    @Override
    JpaRepository<WxSpy, Long> getDao() {
        return dao;
    }
}
