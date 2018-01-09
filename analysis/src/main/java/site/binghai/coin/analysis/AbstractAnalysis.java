package site.binghai.coin.analysis;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.entity.KlineTime;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.results.AnalysisResult;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.JSONPuter;
import site.binghai.coin.common.utils.TimeFormat;
import site.binghai.coin.data.impl.AnalysisResultService;
import site.binghai.coin.data.impl.KlineService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by binghai on 2018/1/8.
 * 通用分析支持
 *
 * @ huobi
 */
public abstract class AbstractAnalysis extends AnalysisMethod implements InitializingBean {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected KlineTime klineTime;

    @Autowired
    protected KlineService klineService;
    @Autowired
    protected AnalysisResultService analysisResultService;

    /**
     * write your logic
     */
    protected abstract void analysis(List<Kline> list, Symbol symbol, JSONPuter jsonPuter);

    protected abstract KlineTime getKlineTime();

    protected abstract void initApp();


    /**
     * works start here
     */
    public final void startWork() {
        getAllSymbols().parallelStream()
                .forEach(this::analysisIfNotEmpty);
    }

    /**
     * inner function
     */
    private final void analysisIfNotEmpty(Symbol symbol) {
        List<Kline> list = getKlines(symbol);
        if (CollectionUtils.isEmpty(list)) {
            noRecordError(symbol);
        } else {
            long start = now();
            JSONPuter puter = new JSONPuter();
            puter.put("baseCoin", symbol.getBaseCurrency());
            puter.put("quoteCoin", symbol.getQuoteCurrency());
            puter.put("batchNumber", getBatchNo());
            puter.put("analysisLevel", klineTime.getTime());
            analysis(list, symbol, puter);
            insertAnalysisResult(symbol, start, now(), puter);
        }
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public String nowTime() {
        return TimeFormat.format(now());
    }

    public final List<Symbol> getAllSymbols() {
        List<Symbol> rs = CoinUtils.allSymbols();
        return rs == null ? new ArrayList<>() : rs;
    }

    public final List<Kline> getKlines(Symbol symbol, long tsBefore) {
        long start = System.currentTimeMillis() - new Double(tsBefore * 1.1).intValue();
        return klineService.getKlineBetween(symbol, start, now());
    }

    protected final void noRecordError(Symbol symbol) {
        logger.error("{} didn't has anything at {} when try to list {} records.",
                symbol.getBaseCurrency() + symbol.getQuoteCurrency(),
                TimeFormat.format(System.currentTimeMillis()), klineTime.getTime());
    }

    public final List<Kline> getKlines(Symbol symbol) {
        return getKlines(symbol, klineTime.getTs());
    }

    @Override
    public final void afterPropertiesSet() throws Exception {
        klineTime = getKlineTime();
        initApp();
    }

    /**
     * save results by this method
     */
    private final void insertAnalysisResult(Symbol symbol, long start, long end, JSONPuter puter) {
        puter.put("analysisStart", start);
        puter.put("analysisEnd", end);
        puter.put("timeConsuming", end - start);
        AnalysisResult result = puter.asResult().toJavaObject(AnalysisResult.class);
        analysisResultService.save(result);
    }

    public String getBatchNo() {
        return TimeFormat.format2yyyyMMdd(now());
    }
}
