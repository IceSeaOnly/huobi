package site.binghai.coin.cron;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.coin.common.entity.Kline;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.common.utils.IoUtils;
import site.binghai.coin.common.utils.SetupUtils;
import site.binghai.coin.common.utils.TimeFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by IceSea on 2018/4/13.
 * GitHub: https://github.com/IceSeaOnly
 */
@Component
@Log4j
public class VcBook {
    private long lastBegin = -1;
    private long interval = -1;
    private String rootPath = null;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void vcBook() {
        if (lastBegin == -1) {
            lastBegin = System.currentTimeMillis();
            rootPath = SetupUtils.getParamAfter("-rootPath");
            interval = Long.parseLong(SetupUtils.getParamAfter("-interval"));
        }

        log.info("working... " + TimeFormat.format(System.currentTimeMillis()));
        List<Symbol> symbolList = CoinUtils.allSymbols();
        symbolList
                .parallelStream()
                .forEach(v -> {
                    Kline kline = CoinUtils.getLastestKline(v, false);
                    if (kline != null) {
                        File dic = new File(rootPath + "/" + v.toStringName());
                        if (!dic.exists()) {
                            dic.mkdir();
                        }
                        String file = String.format("%s/%s.txt", dic.getAbsolutePath(), TimeFormat.format2yyyyMMdd(System.currentTimeMillis()));
                        IoUtils.AppendWriteCH(file, JSONObject.toJSONString(kline) + "\n");
                    }
                });
        log.info("done.");

        if (System.currentTimeMillis() - lastBegin > interval * 1000) {
            doShell();
        }
    }

    private void doShell() {
        try {
            String shpath = SetupUtils.getParamAfter("-gitScript");
            if (null == shpath) {
                throw new Exception("shell script not exist!");
            }

            Process ps = Runtime.getRuntime().exec(shpath + " " + TimeFormat.format2yyyyMMdd(System.currentTimeMillis()));

            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            log.info("shell result :" + result);
        } catch (Exception e) {
            log.error("error when do shell!", e);
        }
    }
}
