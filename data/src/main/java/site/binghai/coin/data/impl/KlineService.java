package site.binghai.coin.data.impl;

import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.TimeFormat;
import site.binghai.coin.data.dao.KlineDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@Service
public class KlineService extends BaseService<Kline> {
    @Autowired
    private KlineDao klineDao;
    @Autowired
    private EntityManager entityManager;

    private static Set<String> existedTableName = new HashSet<>();

    private static final String createQueryPart1 = "CREATE TABLE ";
    private static final String createQueryPart2 = " (`main_id` bigint(20) NOT NULL AUTO_INCREMENT,`created` bigint(20) NOT NULL,`created_time` varchar(255) DEFAULT NULL,`amount` double NOT NULL,`close` double NOT NULL,`coin_name` varchar(255) DEFAULT NULL,`count` int(11) NOT NULL,`high` double NOT NULL,`id` bigint(20) NOT NULL,`low` double NOT NULL,`open` double NOT NULL,`quote_coin_name` varchar(255) DEFAULT NULL,`vol` double NOT NULL,`ask_amount` double NOT NULL,`ask_price` double NOT NULL,`bid_amount` double NOT NULL,`bid_price` double NOT NULL,PRIMARY KEY (`main_id`))";

    @Override
    JpaRepository<Kline, Long> getDao() {
        return klineDao;
    }

    public List<Kline> getKlineBetween(Symbol symbol, Long startTime, Long endTime) {
        if (endTime <= startTime) {
            return new ArrayList<>();
        }
        return klineDao.getByCreatedBetweenAndCoinNameAndQuoteCoinName(
                startTime, endTime, symbol.getBaseCurrency(), symbol.getQuoteCurrency());
    }

    /**
     * 获取该币零点的价格
     */
    public Kline getZeroPointPrice(Symbol symbol) {
        long start = TimeFormat.getTimesmorning();
        long end = start + 60000;
        List<Kline> list = getKlineBetween(symbol, start, end);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    public Kline getLastestKline(Symbol symbol) {
        return klineDao.getLastestKline(symbol.getBaseCurrency(), symbol.getQuoteCurrency()).get(0);
    }

    @Override
    @Transactional
    public Kline save(Kline kline) {
        String tname = kline.getCoinName() + kline.getQuoteCoinName();

        String rawSql = "INSERT INTO `%s` (`created`, `created_time`, `amount`, `ask_amount`, `ask_price`, `bid_amount`, `bid_price`, `close`, `coin_name`, `count`, `high`, `id`, `low`, `open`, `quote_coin_name`, `vol`) VALUES (%d,'%s',%f,%f,%f,%f,%f,%f,'%s',%d,%f,%d,%f,%f,'%s',%f)";

        String sql = String.format(rawSql,
                tname,
                kline.getCreated(),
                kline.getCreatedTime(),
                kline.getAmount(),
                kline.getAskAmount(),
                kline.getAskPrice(),
                kline.getBidAmount(),
                kline.getBidPrice(),
                kline.getClose(),
                kline.getCoinName(),
                kline.getCount(),
                kline.getHigh(),
                kline.getId(),
                kline.getLow(),
                kline.getOpen(),
                kline.getQuoteCoinName(),
                kline.getVol());

        if (!existedTableName.contains(tname.toLowerCase())) {
            createIfNotExist(tname);
        }
        int rs = entityManager.createNativeQuery(sql).executeUpdate();
        System.out.println("ID=" + rs);
        return kline;
    }

    private String createIfNotExist(String tableName) {
        try {
            if (!tableExist(tableName)) {
                createTable(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        existedTableName.add(tableName);
        return tableName;
    }

    private synchronized boolean createTable(String tableName) throws SQLException {
        if (tableExist(tableName)) {
            return true;
        }

        String sql = createQueryPart1 + tableName + createQueryPart2;
        org.hibernate.internal.SessionImpl manager = (org.hibernate.internal.SessionImpl) entityManager.getDelegate();
        Connection connection = manager.getJdbcConnectionAccess().obtainConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            return stmt.executeUpdate(sql) > -1;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public boolean tableExist(String tableName) throws SQLException {
        org.hibernate.internal.SessionImpl manager = (org.hibernate.internal.SessionImpl) entityManager.getDelegate();
        Connection conn = manager.getJdbcConnectionAccess().obtainConnection();
        boolean flag = true;
        String sql = "SELECT * FROM " + tableName + " limit 1";
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
        } catch (Exception e) {
            flag = false;
        } finally {
            if (st != null) {
                st.close();
            }
        }
        return flag;
    }
}
