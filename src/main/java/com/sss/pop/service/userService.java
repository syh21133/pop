package com.sss.pop.service;

import com.sss.pop.dto.userDTO;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

public interface userService {
    
    // idOverlapCheck : 아이디 중복 체크
    String idOverlapCheck(String userId);

    // userEmailCheck : 이메일 인증 번호 발송
    String userEmailCheck(String userEmail);

    // userJoin : 회원가입 처리
    ModelAndView userJoin(userDTO user) throws IOException;

    // userLogin : 로그인(쿠키 생성)
    int userLogin(userDTO user);

    // userStore : 회원 상점으로 이동
    ModelAndView userStore(String userId);

    ModelAndView userModiForm(String userId);

    int userModicheck(String userId, String userPw);

    ModelAndView userModi(userDTO user) throws IOException;

    // GetUserStoreIntro : 상점 소개글 가져오기(ajax)
    String GetUserStoreIntro(String userId);

    // userStoreIntroModi : 상점 소개글 수정하기(ajax)
    String userStoreIntroModi(String userId, String userStoreIntro);

    // userDelete : 일반회원 탈퇴
    int userDelete(String userId, String  userPw);

    // kakaouserDelete : 카카오회원 탈퇴
    ModelAndView kakaoUserDelete(String userId);

    // userIdFind : 아이디 찾기 처리 시 모든 아이디 조회
    List<String> userIdFind(String userName, String userPhone);

    // userPwFind : 비밀번호 찾기 처리 시 일치하는 아이디 조회
    String userPwFind(String userId, String userPhone);

    // userPwChange : 비밀번호 변경 처리
    ModelAndView userPwChange(String userId, String userPw);

    // strUserCash : 보유 캐시 출력
    String strUserCash(String userId);
}
