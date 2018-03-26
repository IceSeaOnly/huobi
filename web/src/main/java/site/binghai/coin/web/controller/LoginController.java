package site.binghai.coin.web.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.coin.common.entity.Admin;
import site.binghai.coin.data.impl.AdminService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by binghai on 2018/2/24.
 *
 * @ huobi
 */
@Controller
public class LoginController extends BaseController {
    @Autowired
    private AdminService adminService;

    @RequestMapping("logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "/";
    }

    @RequestMapping(value = "adminLogin", method = RequestMethod.POST)
    @ResponseBody
    public Object adminLogin(HttpServletRequest request, HttpSession session) throws IOException {

        Scanner sc = new Scanner(request.getInputStream());
        String json = sc.nextLine();
        sc.close();
        JSONObject input = JSONObject.parseObject(json);

        if(input == null){
            return failed("非法参数");
        }

        String username = input.getString("username");
        String password = input.getString("password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return failed("用户名/密码不正确");
        }

        Admin admin = adminService.findByUserNameAndMd5Password(username, password);
        if (admin != null) {
            session.setAttribute("admin", admin);
        }
        return admin == null ? failed("用户名/密码不正确") : success("/admin/", "success");
    }
}
