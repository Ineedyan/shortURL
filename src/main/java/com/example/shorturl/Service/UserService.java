package com.example.shorturl.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.shorturl.DTO.LoginFormDTO;
import com.example.shorturl.Entity.User;
import com.example.shorturl.Utils.Result;

public interface UserService extends IService<User> {

    Result login(LoginFormDTO loginFormDTO);

    Result sendCode(String username);

}
