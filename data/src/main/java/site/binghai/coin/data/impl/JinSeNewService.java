package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.coin.common.entity.JinSeNew;
import site.binghai.coin.data.dao.JinSeDao;

import java.util.List;

/**
 * Created by binghai on 2018/3/10.
 *
 * @ huobi
 */
@Service
public class JinSeNewService extends BaseService<JinSeNew>{
    @Autowired
    private JinSeDao jinSeDao;

    @Override
    JpaRepository<JinSeNew, Long> getDao() {
        return jinSeDao;
    }

    public JinSeNew findByHash(String hash){
        List<JinSeNew> res = jinSeDao.findByHashCode(hash);
        if(null != res && res.size() > 0)
            return res.get(0);

        return null;
    }

    public List<JinSeNew> lasted50() {
        return jinSeDao.findAllByOrderByIdDesc(new PageRequest(0,50));
    }

    public List<JinSeNew> findPageAbleDesc(Integer pageSize, Integer page) {
        return jinSeDao.findAllByOrderByIdDesc(new PageRequest(page,pageSize));
    }
}
