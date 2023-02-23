package com.sss.pop.controller;

import com.sss.pop.dao.userDAO;
import com.sss.pop.dto.userDTO;
import com.sss.pop.service.userService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class userController {
    
    private ModelAndView mav = new ModelAndView();

    private final userService usersvc;

    private final userDAO userdao;

    
    
    // USER001  userJoinForm : 회원가입 페이지로 이동
    @GetMapping("/userJoinForm")
    public String userJoinForm() {
        return "userJoinForm";
    }

    // USER002  idOverlapCheck : 아이디 중복 체크(ajax)
    @PostMapping("/idOverlapCheck")
    public @ResponseBody String idOverlapCheck(@RequestParam("userId") String userId) {

        String result = usersvc.idOverlapCheck(userId);

        return result;
    }

    // USER003  userEmailCheck : 이메일 인증 번호 발송
    @PostMapping("/userEmailCheck")
    public @ResponseBody String userEmailCheck(@RequestParam("userEmail") String userEmail) {

        String uuid = usersvc.userEmailCheck(userEmail);

        return uuid;
    }

    // USER004  userJoin : 회원가입 처리
    @PostMapping("userJoin")
    public ModelAndView userJoin(@ModelAttribute userDTO user) throws IOException {


        mav = usersvc.userJoin(user);

        return mav;
    }
    
    // ULIN001 userLoginForm : 로그인 페이지 요청
    @GetMapping("/userLoginForm")
    public String userLoginForm() {
        return "userLoginForm";
    }

    // ULIN002  userLogin : 로그인 처리
    @PostMapping("/userLogin")
    public @ResponseBody int userLogin(@ModelAttribute userDTO user
            , HttpServletRequest request
            , HttpServletResponse response
            , @RequestParam(value = "remember", required = false, defaultValue = "") String remember) {

        int result = usersvc.userLogin(user);

        // 로그인 저장 체크박스를 체크하였을 경우 아이디 쿠키 생성
        if(result>0) {
            if (remember.equals("createCookie")) {
                //쿠키에 시간 정보를 주지 않으면 세션 쿠키가 된다. (브라우저 종료시 모두 종료)
                Cookie idCookie = new Cookie("idCookie", user.getUserId());
                idCookie.setMaxAge(60 * 60 * 24 * 30);
                response.addCookie(idCookie);
            }
        }

        // 로그인 성공 처리
        return result;
    }

    // UOUT001 userLogout : 로그아웃(아이디 쿠키, 세션 모두 삭재)
    @GetMapping("/userLogout")
    public String userLogout(HttpSession session, HttpServletResponse response) {

            Cookie logoutCookie = new Cookie("idCookie", null); // "(쿠키 이름)"에 대한 값을 null로 지정

            logoutCookie.setMaxAge(0);          // 유효시간을 0으로 설정
            response.addCookie(logoutCookie);   // 응답 헤더에 추가해서 없어지도록 함


        if(session != null) {
            session.invalidate();
        }

        return "redirect:/index";
    }

    // UMOD001 userModicheck :
    @GetMapping("/userModicheck")
    public String userModicheck() {
        return "userModicheck";
    }

    // UMOD003 회원수정 페이지 이동
    @GetMapping("/userModiForm")
    public ModelAndView userModiForm(@RequestParam("userId") String userId) {

        mav = usersvc.userModiForm(userId);

        return mav;
    }

    // UMDO002  회원수정 비밀번호확인
    @PostMapping("/userModicheck")
    public @ResponseBody int userModicheck(@RequestParam("userId") String userId, @RequestParam("userPw") String userPw) {

        int result = usersvc.userModicheck(userId,userPw);

        return result;
    }

    // UMOD004 회원 정보 수정
    @PostMapping("/userModi")
    public ModelAndView userModi(@ModelAttribute userDTO user) throws IOException {
        mav = usersvc.userModi(user);

        return mav;
    }

    //
    @PostMapping("/cashRecharge")
    public @ResponseBody int cashRecharge(@ModelAttribute userDTO user) {

         userdao.cashRecharge(user);

        return 0;
    }
    // UCAS 001
    @GetMapping("/rechargePopup")
    public String rechargePopup(@RequestParam("userId") String userId, HttpSession session) {
        session.setAttribute("userId",userId);
        return "rechargePopup";
    }


    ////////////////////////////////////////////////////////////////////////////////
    // UMYP001 userMyPage : 내 상점 페이지 요청
    @GetMapping("/userStore")
    public ModelAndView userMyPage(@RequestParam("userId") String userId) {

        mav = usersvc.userStore(userId);

        return mav;
    }


    //////////////////////////////////////////////////////////////////////////////////
    // 2022.02.02 jyj 추가

    // UFID001  userIdFindCheckForm : 아이디 찾기 페이지 요청(팝업)
    @GetMapping("/userIdFindCheckForm")
    public String userIdFindCheckForm() {
        return "userIdFindCheckForm";
    }

    // UFID002  userIdFindForm : 찾은 아이디 출력 페이지 요청(팝업)
    @GetMapping("/userIdFindForm")
    public ModelAndView userIdFindForm(@RequestParam("userName") String userName
            , @RequestParam("userPhone") String userPhone) {

        // 회원의 이름과 이메일이 일치하는 아이디를 가져온다(모두 -> 동일한 이름과 이메일로 여러개의 회원 아이디가 있는경우)
        List<String> userIdList = usersvc.userIdFind(userName, userPhone);

        mav.addObject("userIdList", userIdList);
        mav.setViewName("userIdFindForm");

        return mav;
    }



    // UFPW001  userPwFindCheckForm : 비밀번호 찾기 페이지 요청(팝업)
    @GetMapping("/userPwFindCheckForm")
    public String userPwFindCheckForm() {
        return "userPwFindCheckForm";
    }

    // UFPW002  userPwFindForm : 비밀번호 변경 출력 페이지 요청(팝업)
    @GetMapping("/userPwFindForm")
    public ModelAndView userPwFindForm(@RequestParam("userId") String userId
            , @RequestParam("userPhone") String userPhone) {

        // 회원의 아이디과 전화번호가 일치하는 아이디를 가져온다
        String getUserId = usersvc.userPwFind(userId, userPhone);

        // 만약 일치하는 아이디를 가져왔다면 변경 페이지로, 아니라면 다시 인증 페이지로
        if(userId.equals(getUserId)){
            mav.addObject("userId", getUserId);
            mav.setViewName("userPwFindForm");
        } else{
            mav.addObject("message", "fail");
            mav.setViewName("userPwFindCheckForm");
        }

        return mav;
    }
    // UFPW003  userPwChange : 비밀번호 변경 처리
    @GetMapping("/userPwChangeForm")
    public ModelAndView userPwChange(@RequestParam("userId") String userId
                                    , @RequestParam("userPw") String userPw) {

        mav = usersvc.userPwChange(userId, userPw);

        return mav;
    }
    
    
    // userStoreIntro : 상점 소개글 가져오기(ajax)
    @PostMapping("/GetUserStoreIntro")
    public @ResponseBody String GetUserStoreIntro(@RequestParam("userId") String userId){

        String userStoreIntro = usersvc.GetUserStoreIntro(userId);

        return userStoreIntro;
    }
    
    // userStoreIntroModi : 상점 소개글 수정하기(ajax)
    @PostMapping("/userStoreIntroModi")
    public @ResponseBody String userStoreIntroModi(@RequestParam("userId") String userId
                                            , @RequestParam("userStoreIntro") String userStoreIntro){

        String result = usersvc.userStoreIntroModi(userId, userStoreIntro);

        return result;
    }



    // UDEL001  userDeleteCheck : 일반회원 탈퇴 비밀번호 체크 페이지 요청
    @GetMapping("/userDeleteCheck")
    public ModelAndView userDeleteCheck(@RequestParam("userId") String userId) {

        mav.addObject("userId", userId);
        mav.setViewName("userDeleteCheck");

        return mav;
    }

    // UDEL002  userDelete : 일반회원 탈퇴
    @PostMapping("/userDelete")
    public @ResponseBody int userDelete(@RequestParam("userId") String userId, @RequestParam("userPw") String userPw) {

        int result = usersvc.userDelete(userId,userPw);

        return result;
    }

    // getStrUserCash : 보유 캐시 출력
    @PostMapping("/strUserCash")
    public @ResponseBody String strUserCash(@RequestParam("userId") String userId){

        String result = usersvc.strUserCash(userId);

        return result;
    }

    // userCheck : 회원 탈퇴 여부
    @PostMapping("/userCheck")
    public @ResponseBody int userCheck(@RequestParam("userId") String userId){

        // 유저 탈퇴 여부
        int result = 0;

        // 유저 탈퇴여부를 가져오기 위해 userLogin()메소드 재사용
        userDTO user = userdao.userLogin(userId);

        if(!StringUtils.isEmpty(user)){
            result = user.getUserCheck();
        }

        return result;
    }


}
