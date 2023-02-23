package com.sss.pop.kakaoLogin;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface kakaoLoginService {

    // 카카오 인가 코드 전달
    String getAccessToken(String code);

    // 카카오 회원 정보 조회
    ModelAndView createKakaoUser(String token
            , HttpServletRequest request
            , HttpServletResponse response) throws Exception;
}
