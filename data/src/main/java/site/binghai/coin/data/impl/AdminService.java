package site.binghai.coin.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import site.binghai.coin.common.entity.Admin;
import site.binghai.coin.common.utils.MD5;
import site.binghai.coin.data.dao.AdminDao;

import java.util.List;

/**
 * Created by binghai on 2018/2/24.
 *
 * @ huobi
 */
@Service
public class AdminService extends BaseService<Admin> {
    @Autowired
    private AdminDao adminDao;

    @Override
    JpaRepository<Admin, Long> getDao() {
        return adminDao;
    }

    public Admin findByUserNameAndMd5Password(String username, String password) {
        password = MD5.encryption(password);
        List<Admin> admins = adminDao.findByUsernameAndPassword(username,password);

        if(CollectionUtils.isEmpty(admins)){
            return null;
        }

        return admins.get(0);
    }
}
