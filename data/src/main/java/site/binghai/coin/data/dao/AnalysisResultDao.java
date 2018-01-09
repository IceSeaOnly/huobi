package site.binghai.coin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.results.AnalysisResult;

/**
 * Created by binghai on 2018/1/8.
 *
 * @ huobi
 */
public interface AnalysisResultDao extends JpaRepository<AnalysisResult, Long> {
}
