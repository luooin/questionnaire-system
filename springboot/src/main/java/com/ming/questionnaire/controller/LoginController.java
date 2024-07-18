package com.ming.questionnaire.controller;

import com.ming.questionnaire.pojo.ResponseResult;
import com.ming.questionnaire.pojo.User;
import com.ming.questionnaire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public ResponseResult login(@RequestBody User user){
        // 调用登录方法
        return userService.login(user);
    }

    @PostMapping("/logout2")
    public ResponseResult logout(@RequestBody String userId){
        return userService.logout(userId);
    }

}
