package site.binghai.coin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.coin.common.entity.WxSpy;
import site.binghai.coin.common.response.Symbol;
import site.binghai.coin.common.utils.CoinUtils;
import site.binghai.coin.data.impl.WxSpyService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by binghai on 2018/3/4.
 *
 * @ huobi
 */
@Controller
@RequestMapping("wx")
public class WxContoller extends BaseController{

    @Autowired
    private WxSpyService wxSpyService;

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

        map.put("myList", my);
        map.put("allList", all);
        map.put("openid", openid);
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
            if(wxSpy.getBaseCoin().equals(b) && wxSpy.getQuoteCoin().equals(q))
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
        if(wxSpy != null && wxSpy.getOpenId().equals(openid)){
            wxSpyService.delete(id);
            return success("");
        }

        return failed("取消失败!");
    }
}
