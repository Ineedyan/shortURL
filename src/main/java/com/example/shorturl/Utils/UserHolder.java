package com.example.shorturl.Utils;

import com.example.shorturl.DTO.UserInfoDTO;

public class UserHolder {
    private static final ThreadLocal<UserInfoDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserInfoDTO user){
        tl.set(user);
    }

    public static UserInfoDTO getUser(){return tl.get();}

    public static void removeUser(){
        tl.remove();
    }
}
