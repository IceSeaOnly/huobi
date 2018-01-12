package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.entity.PrediectResult;
import site.binghai.coin.data.dao.PrediectResultDao;

/**
 * Created by binghai on 2018/1/9.
 *
 * @ huobi
 */
@Service
public class PrediectResultService extends BaseService<PrediectResult> {
    @Autowired
    private PrediectResultDao dao;

    @Override
    JpaRepository<PrediectResult, Long> getDao() {
        return dao;
    }
}
