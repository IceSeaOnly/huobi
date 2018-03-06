package site.binghai.coin.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.coin.common.entity.WaterLevelMonitor;
import site.binghai.coin.common.entity.WxSpy;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.data.impl.WaterLevelMonitorService;
import site.binghai.coin.data.impl.WxSpyService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2018/3/4.
 *
 * @ huobi
 */
@Controller
@RequestMapping("wx")
public class WxContoller extends BaseController {

    @Autowired
    private WxSpyService wxSpyService;
    @Autowired
    private WaterLevelMonitorService waterLevelMonitorService;

    @RequestMapping("index")
    public String wxWatch(String openid, ModelMap map) {
        if (openid == null || openid.equals("null")) {
            return "redirect:http://weixin.qdxiaogutou.com/login.php?backUrl=http://btc.nanayun.cn/wx/index";
        }

        List<WxSpy> my = wxSpyService.findByOpenId(openid);
        List<Symbol> all = CoinUtils.allSymbols();

        all = all.stream().filter(v -> {
            for (WxSpy wxSpy : my) {
                if (wxSpy.getBaseCoin().equals(v.getBaseCurrency()) && wxSpy.getQuoteCoin().equals(v.getQuoteCurrency()))
                    return false;
            }
            return true;
        }).collect(Collectors.toList());

        List<Symbol> btc = new ArrayList<>();
        List<Symbol> usdt = new ArrayList<>();
        List<Symbol> eth = new ArrayList<>();

        for (Symbol symbol : all) {
            switch (symbol.getQuoteCurrency()) {
                case "usdt":
                    usdt.add(symbol);
                    break;
                case "btc":
                    btc.add(symbol);
                    break;
                default:
                    eth.add(symbol);
            }
        }
        map.put("myList", my);
        map.put("bt", all);
        map.put("allList", all);
        map.put("allList", all);
        map.put("openid", openid);
        map.put("monitorList", waterLevelMonitorService.findByOpenId(openid));
        return "wxWatch";
    }

    @RequestMapping("addWatch")
    @ResponseBody
    public Object addWatch(@RequestParam String openid, @RequestParam String b, @RequestParam String q) {
        if (openid == null || openid.equals("null")) {
            return failed("openid不正确!");
        }

        List<WxSpy> my = wxSpyService.findByOpenId(openid);

        for (WxSpy wxSpy : my) {
            if (wxSpy.getBaseCoin().equals(b) && wxSpy.getQuoteCoin().equals(q))
                return failed("你已经订阅过了!");
        }

        WxSpy wxSpy = new WxSpy();
        wxSpy.setBaseCoin(b);
        wxSpy.setQuoteCoin(q);
        wxSpy.setOpenId(openid);

        wxSpyService.save(wxSpy);

        return success("success");
    }

    @RequestMapping("delWatch")
    @ResponseBody
    public Object delWatch(@RequestParam String openid, @RequestParam Long id) {
        if (openid == null || openid.equals("null")) {
            return failed("openid不正确!");
        }

        WxSpy wxSpy = wxSpyService.findById(id);
        if (wxSpy != null && wxSpy.getOpenId().equals(openid)) {
            wxSpyService.delete(id);
            return success("");
        }

        return failed("取消失败!");
    }

    /**
     * 添加仅微信级别的水位监控
     */
    @ResponseBody
    @RequestMapping("addWxMonitor")
    public Object addWxMonitor(@RequestParam String openid, @RequestParam String b, @RequestParam String q, @RequestParam Double t) {
        if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(b) || StringUtils.isEmpty(q) || t <= 0) {
            return failed("输入错误!");
        }

        List<Symbol> symbols = CoinUtils.allSymbols();
        boolean find = false;
        for (Symbol symbol : symbols) {
            if (symbol.getBaseCurrency().equals(b) && symbol.getQuoteCurrency().equals(q)) {
                find = true;
                break;
            }
        }

        if (!find) {
            return failed("交易对" + b.toUpperCase() + "/" + q.toUpperCase() + "不存在!");
        }

        WaterLevelMonitor waterLevelMonitor = new WaterLevelMonitor();
        waterLevelMonitor.setTargetValue(t);
        waterLevelMonitor.setQuoteCoin(q);
        waterLevelMonitor.setBaseCoin(b);
        waterLevelMonitor.setWxNotice(openid);

        waterLevelMonitorService.save(waterLevelMonitor);
        return success("添加成功!");
    }

    @ResponseBody
    @RequestMapping("delWxMonitor")
    public Object delWxMonitor(@RequestParam String openid,@RequestParam Long id){
        WaterLevelMonitor waterLevelMonitor = waterLevelMonitorService.findById(id);
        if(waterLevelMonitor != null && waterLevelMonitor.getWxNotice().equals(openid)){
            waterLevelMonitorService.delete(id);
        }
        return success("success");
    }
}
