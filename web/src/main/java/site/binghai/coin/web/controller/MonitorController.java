package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.coin.common.entity.WaterLevelMonitor;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.data.impl.WaterLevelMonitorService;

import java.util.List;

/**
 * Created by binghai on 2018/3/1.
 *
 * @ huobi
 */
@RestController
@RequestMapping("admin")
public class MonitorController extends BaseController {
    @Autowired
    private WaterLevelMonitorService waterLevelMonitorService;

    @RequestMapping("monitorList")
    public Object monitorList() {
        List<WaterLevelMonitor> monitorList = waterLevelMonitorService.findAll(1000);
        JSONArray array = new JSONArray();
        monitorList.forEach(v -> {
            JSONObject object = new JSONObject();
            object.put("id",v.getId());
            object.put("coin", (v.getBaseCoin() + "/" + v.getQuoteCoin()).toUpperCase());
            object.put("target", v.getTargetValue());
            if (v.isComplete()) {
                object.put("current", "#");
            } else {
                object.put("current", CoinUtils.getLastestKline(
                        new Symbol(v.getBaseCoin(), v.getQuoteCoin())).getClose());
            }

            object.put("createTime", v.getCreatedTime());
            object.put("finishTime", v.isComplete() ? v.getCompleteTime() : "#");
            object.put("noticePep", v.getNotice());
            object.put("status", v.isComplete() ? "finish" : "waiting");

            array.add(object);
        });
        return success(array, "success");
    }

    @RequestMapping("deleteMonitor")
    public Object deleteMonitor(@RequestParam Long id) {
        waterLevelMonitorService.delete(id);
        return success("success");
    }

    @RequestMapping(value = "addMonitor",method = RequestMethod.POST)
    public Object addMonitor(@RequestParam String baseCoin,
                             @RequestParam String quoteCoin,
                             @RequestParam String notice,
                             @RequestParam String wxNotice,
                             @RequestParam Double targetValue) {
        WaterLevelMonitor monitor = new WaterLevelMonitor();
        monitor.setBaseCoin(baseCoin);
        monitor.setQuoteCoin(quoteCoin);
        monitor.setNotice(notice.trim());
        monitor.setTargetValue(targetValue);
        monitor.setWxNotice(wxNotice.trim());
        waterLevelMonitorService.save(monitor);
        return success("success");
    }
}
