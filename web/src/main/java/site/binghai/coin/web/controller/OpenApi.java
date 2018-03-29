package site.binghai.coin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.coin.common.entity.JinSeNew;
import site.binghai.coin.data.impl.JinSeNewService;

/**
 * Created by binghai on 2018/3/11.
 *
 * @ huobi
 */
@RequestMapping("open")
@Controller
public class OpenApi {

    @Autowired
    private JinSeNewService jinSeNewService;

    @RequestMapping("jinseDetail")
    public Object jinseDetail(@RequestParam Long id, ModelMap map) {
        JinSeNew jinSeNew = jinSeNewService.findById(id);
        map.put("data", jinSeNew);
        map.put("title", getTitle(jinSeNew));
        return "jinseDetail";
    }

    private String getTitle(JinSeNew jinSeNew) {
        if (jinSeNew.getContent().startsWith("【")
                && jinSeNew.getContent().contains("】")) {
            int idx = jinSeNew.getContent().indexOf('】');
            return jinSeNew.getContent().substring(1, idx);
        }
        return "交易助手·突发新闻";
    }
}
