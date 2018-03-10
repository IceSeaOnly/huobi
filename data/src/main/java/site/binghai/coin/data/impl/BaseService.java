package site.binghai.coin.data.impl;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.coin.common.entity.DeleteAble;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by binghai on 2017/12/2.
 *
 * @ super_big_dumpling
 */
public abstract class BaseService<T extends DeleteAble> {
    abstract JpaRepository<T, Long> getDao();

    @Transactional
    public T save(T t) {
        return getDao().save(t);
    }

    /**
     * 更新不存在的记录会失败
     */
    @Transactional
    public T update(T t) {
        if (t.getId() > 0) {
            return save(t);
        }
        return t;
    }

    public T findById(Long id) {
        return getDao().findOne(id);
    }

    @Transactional
    public void delete(Long id) {
        getDao().delete(id);
    }

    @Transactional
    public boolean deleteAll(String confirm) {
        if (confirm.equals("confirm")) {
            getDao().deleteAll();
            return true;
        }
        return false;
    }

    /**
     * 覆盖保存
     */
    @Transactional
    public boolean coverSave(T t) {
        if (getDao().exists(t.getId())) {
            getDao().delete(t);
        }
        getDao().save(t);
        return true;
    }

    public List<T> findByIds(List<Long> ids) {
        return getDao().findAll(ids);
    }

    public List<T> findAll(int limit) {
        return getDao().findAll(new PageRequest(0, limit)).getContent();
    }

    public long count(){
        return getDao().count();
    }

    @Transactional
    public void batchSave(List<T> batch) {
        getDao().save(batch);
    }
}
