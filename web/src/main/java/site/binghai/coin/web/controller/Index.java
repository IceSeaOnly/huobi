package site.binghai.coin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@RequestMapping("/")
@Controller
public class Index {

    @RequestMapping("index")
    public String index() {
        return "index";
    }
}
