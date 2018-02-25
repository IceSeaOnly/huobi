package site.binghai.coin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.entity.Admin;

import java.util.List;

/**
 * Created by binghai on 2018/2/24.
 *
 * @ huobi
 */
public interface AdminDao extends JpaRepository<Admin,Long>{

    List<Admin> findByUsernameAndPassword(String u,String p);
}
