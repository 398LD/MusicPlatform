package com.kexun.centmusic.api;

import com.kexun.centmusic.common.RedisUtil;
import com.kexun.centmusic.pojo.User;
import com.kexun.centmusic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("sso")
public class Login {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redis;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Map<String, Object> login(String username, String password, HttpServletResponse response) {
        User login = userService.login(username, password);
        HashMap<String, Object> map = new HashMap<>();
        if (login != null) {
            saveUser(username, response);
            //登录成功
            map.put("code", 0);
            map.put("msg", "ok");
        } else {
            map.put("code", -1);
            map.put("msg", "用户名或密码错误");
        }
        return map;
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public Map<String, Object> register(String username, String password, String showName, HttpServletResponse response) {
        HashMap<String, Object> map = new HashMap<>();
        //判断是否存在
        boolean have = userService.have(username);
        if (!have) {
            map.put("code", 0);
            map.put("msg", "用户名已经存在");
            return map;
        }
        boolean register = userService.register(username, password, showName);

        if (register) {
            saveUser(username, response);
            map.put("code", 0);
            map.put("msg", "ok");
        } else {
            map.put("code", 0);
            map.put("msg", "系统错误");
        }
        return map;
    }

    //保存以后到redis 和浏览器 时间为 30天
    private void saveUser(String username, HttpServletResponse response) {
        String uuid = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("login", uuid);
        //一个月
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
        redis.set("login:" + username, uuid);
        redis.expire("login:" + username, 30, TimeUnit.DAYS);
    }


}
