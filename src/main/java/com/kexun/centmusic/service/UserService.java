package com.kexun.centmusic.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kexun.centmusic.common.MD5Utils;
import com.kexun.centmusic.data.UserDao;
import com.kexun.centmusic.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("password", MD5Utils.toMD5(password, username));
        return userDao.selectOne(wrapper);
    }

    public boolean register(String username, String password, String showName) {
        User user = new User();
        user.setShowName(showName);
        user.setUsername(username);
        user.setPassword(MD5Utils.toMD5(password, username));
        user.setIsMember(1);
        user.setCreateTime(System.currentTimeMillis());
        //2020-01-01
        user.setExpirationTime(1577808000);
        return userDao.insert(user) > 0 ? true : false;
    }


    public boolean have(String username) {
        Integer re = userDao.selectCount(new QueryWrapper<User>().eq("username", username));
        return re > 0 ? false : true;

    }


}
