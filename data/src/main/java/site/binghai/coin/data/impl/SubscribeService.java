package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.entity.Subscribe;
import site.binghai.coin.common.enums.SubscribeType;
import site.binghai.coin.data.dao.SubscribeDao;

import java.util.List;

/**
 * Created by binghai on 2018/3/10.
 *
 * @ huobi
 */
@Service
public class SubscribeService extends BaseService<Subscribe> {
    @Autowired
    private SubscribeDao dao;


    @Override
    JpaRepository<Subscribe, Long> getDao() {
        return dao;
    }

    public Subscribe findByOpenIdAndType(String openId, SubscribeType type){
        List<Subscribe> list = dao.findByOpenidAndType(openId,type.getCode());

        if(list == null || list.size() == 0) return null;

        return list.get(0);
    }

    public List<Subscribe> findByType(SubscribeType type){
        return dao.findByType(type.getCode());
    }
}
