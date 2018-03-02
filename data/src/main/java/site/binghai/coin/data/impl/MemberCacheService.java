package site.binghai.coin.data.impl;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by binghai on 2018/2/25.
 * 内存缓存服务
 * @ huobi
 */
@Service
public class MemberCacheService {
    private static ConcurrentHashMap<String,Object> cache = new ConcurrentHashMap<>();

    public enum CacheKeys{
        LIST_ALL_ORDERS("LIST_ALL_ORDERS"),
        FLOAT_TOP_10("FLOAT_TOP_10"),
        WX_SPY_CACHE("WX_SPY_CACHE"),


        ;
        private String key;

        CacheKeys(String key) {
            this.key = key;
        }
    }

    public void put(CacheKeys key,Object value){
        cache.put(key.key,value);
    }

    public Object get(CacheKeys key){
        return cache.get(key.key);
    }
}
