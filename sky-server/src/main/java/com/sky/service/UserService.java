package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.springframework.stereotype.Service;

import java.io.IOException;



public interface UserService {

    //微信用户登录
    User wxLogin(UserLoginDTO userLoginDTO) throws IOException;
}
