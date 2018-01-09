package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.results.AnalysisResult;
import site.binghai.coin.data.dao.AnalysisResultDao;

/**
 * Created by binghai on 2018/1/8.
 *
 * @ huobi
 */
@Service
public class AnalysisResultService extends BaseService<AnalysisResult> {
    @Autowired
    private AnalysisResultDao dao;

    @Override
    JpaRepository<AnalysisResult, Long> getDao() {
        return dao;
    }
}
