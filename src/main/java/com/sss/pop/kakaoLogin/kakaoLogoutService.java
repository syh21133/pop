package com.sss.pop.kakaoLogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface kakaoLogoutService {
    
    // 로그아웃 2
    void kakaoLogout(HttpSession session, HttpServletRequest request , HttpServletResponse response);


    // 로그아웃 액세스 토큰 요청
//    String getLogoutAccessToken();




}