package com.sss.pop.serviceImpl;

import com.sss.pop.auc.auDAO;
import com.sss.pop.dao.itemDAO;
import com.sss.pop.dao.userDAO;
import com.sss.pop.dto.*;
import com.sss.pop.service.userService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.lang3.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class userServiceImpl implements userService {

    private ModelAndView mav;

    private final userDAO userdao;

    private final JavaMailSender mailSender;

    // 암호화를 위한 객체 선언
    private final PasswordEncoder pwEnc;

    private final HttpSession session;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final itemDAO itemdao;

    private final auDAO audao;


    // idOverlapCheck : 아이디 중복 체크
    @Override
    public String idOverlapCheck(String userId) {

        String checkId = userdao.idOverlapCheck(userId);
        String result;

        if (checkId == null) {
            result = "OK";
        } else {
            result = "NO";
        }

        return result;
    }

    // userEmailCheck : 이메일 인증 번호 발송(ajax)
    @Override
    public String userEmailCheck(String userEmail) {
        String uuid = UUID.randomUUID().toString().substring(0, 6);

        MimeMessage mail = mailSender.createMimeMessage();

        // 보낼 내용
        String mailContent = "<h2>안녕하세요. 중고거래 사이트 POP(People On Poeple)입니다.</h2><br/>"
                + "<h3>인증번호는 " + uuid + " 입니다.</h3>";

        // 가입성공 메일 보내기
        try {
            mail.setSubject("[이메일 인증] 중고거래 사이트 POP(People On People) 이메일 인증", "UTF-8");    // mail 객체에 제목을 저장
            String str = "<pre>" + mailContent + "</pre>";  // 내용을 저장
            mail.setText(str, "UTF-8", "html"); // mail객체에 내용 저장,보내는 문서의 형식

            // 메일 받을 사람을 지정
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            mailSender.send(mail);  // 실제로 메일을 보내는 역할
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return uuid;
    }

    // userJoin : 회원가입 처리
    @Override
    public ModelAndView userJoin(userDTO user) throws IOException {
        mav = new ModelAndView();

        // 비밀번호 암호화
        // [1] 입력한 비밀번호 가져오기 : user.getUserPw()
        // [2] 입력한 비밀번호 암호화 : pwEnc.encode()
        // [3] 암호화 된 비밀번호 저장 : user.setUserpw()

        user.setUserPw(pwEnc.encode(user.getUserPw()));

        // 파일업로드 설정

        // 1. 파일 불러오기
        // 업로드한 파일 그 자체
        MultipartFile profile = user.getUserProfile();

        // 2. 파일 선택여부 확인
        if (!profile.isEmpty()) {

            // 3. 파일 저장 위치 설정(상대경로)
            Path path = Paths.get(System.getProperty("user.dir")
                    , "src/main/resources/static/profile");

            // 4. 기존에 있던 파일 삭제하기
//            String deletePath = path + "/" + user.getUserProfileName();
//            File deleteFile = new File(deletePath);
//
//            if(deleteFile.exists()) {
//                deleteFile.delete();
//            }

            // 5. 파일 이름 불러오기
            // 업로드한 파일의 이름
            String originalFileName = profile.getOriginalFilename();

            // 6. 난수 생성하기
            String uuid = UUID.randomUUID().toString().substring(0, 8);

            // 7. 업로드 할 파일이름 생성하기(3번 + 2번)
            String userProfileName = uuid + "_" + originalFileName;

            // 8. 파일 선택시 userDTO user객체의 userProfileName 필드에 업로드파일 이름 저장
            user.setUserProfileName(userProfileName);

            // 9. 파일 업로드
            // 빨간줄 생성 시 throws Exception 처리 해주어야함(service,service interface, controller 모두)
            String savePath = path + "/" + userProfileName;
            profile.transferTo(new File(savePath)); // 회원가입 정보가 제대로 저장되었을 경우 savePath에 저장하기 위함
        } else {
            String originalFileName = "checkin.png";
            user.setUserProfileName(originalFileName);
        }

        // 주소 api 결합
        String addr1 = user.getAddr1();
        String addr2 = user.getAddr2();
        String addr3 = user.getAddr3();

        String addr = "(" + addr1 + ") " + addr2 + ", " + addr3;
        user.setUserAddr(addr);


        try {
            // 회원가입 성공시 (에러나 예외처리가 없을 경우)
            int result = userdao.userJoin(user);

            if (result > 0) {
                mav.setViewName("redirect:/userLoginForm");
            } else {
                mav.setViewName("redirect:/userJoinForm");
            }

        } catch (Exception e) {
            // 예외처리 발생 시
            mav.setViewName("redirect:/userJoinForm");
            // 파일 삭제
            Path path = Paths.get(System.getProperty("user.dir")
                    , "src/main/resources/static/profile");
            String deletePath = path + "/" + user.getUserProfileName();

            File deleteFile = new File(deletePath);

            if (deleteFile.exists()) {
                deleteFile.delete();
            }

        }

        return mav;
    }

    // userLogin : 로그인 처리(쿠키 생성)
    @Override
    public int userLogin(userDTO user) {

        int result = 0;
        userDTO login = userdao.userLogin(user.getUserId());

        // 가져온 회원정보의 탈퇴여부가 1이 아니라면(탈퇴한 상태 = 1)
        if(login.getUserCheck() != 1){
            DecimalFormat decFormat = new DecimalFormat("###,###");

            String userCash = decFormat.format(login.getUserCash());
            login.setStrUserCash(userCash);

            String encPw = login.getUserPw();

            // pwEnc.mathces()로 입력한 비번 : user.getUserPW(), DB에 저장된 비번 : encPw
            // 일치하면 true, 일치하지 않으면 false 반환
            if (pwEnc.matches(user.getUserPw(), encPw)) {
                List<noteDTO> myNote = userdao.myNote(user.getUserId());

                // 세션에 로그인 회원 정보 보관
                session.setAttribute("login", login);

                result = 1;
            } else {
                result = 0;
            }
        }
        else {      // 탈퇴한 회원일 경우 -1 반환(탈퇴한 상태 = 1임, 탈퇴회원여부와 result는 같은거 아님)
            result = -1;
        }

        return result;
    }

    @Override
    public ModelAndView userModiForm(String userId) {
        mav = new ModelAndView();

        userDTO user = userdao.userModiForm(userId);

        if (user.getUserAddr() != null) {
            String addr = user.getUserAddr();
            user.setAddr1(addr.split("\\(")[1].split("\\)")[0]);
            user.setAddr2(addr.split("\\)\\s")[1].split(",")[0]);
            user.setAddr3(addr.split(",\\s")[1]);
        } else {
            user.setAddr1("");
            user.setAddr2("");
            user.setAddr3("");
        }

        mav.addObject("users", user);
        mav.setViewName("userModiForm");
        return mav;
    }

    @Override
    public int userModicheck(String userId, String userPw) {

        userDTO login = userdao.userLogin(userId);

        String encPw = login.getUserPw();

        // pwEnc.mathces()로 입력한 비번 : user.getUserPW(), DB에 저장된 비번 : encPw
        // 일치하면 true, 일치하지 않으면 false 반환
        if (pwEnc.matches(userPw, encPw)) {
            // 성공
            return 0;
        } else {
            // 실패
            return 1;
        }

    }

    @Override
    public ModelAndView userModi(userDTO user) throws IOException {
        mav = new ModelAndView();

        // 파일업로드 설정

        // 1. 파일 불러오기
        // 업로드한 파일 그 자체
        MultipartFile profile = user.getUserProfile();

        // 2. 파일 선택여부 확인
        if (!profile.isEmpty()) {

            // 3. 파일 저장 위치 설정(상대경로)
            Path path = Paths.get(System.getProperty("user.dir")
                    , "src/main/resources/static/profile");

            // 4. 기존에 있던 파일 삭제하기
            String deletePath = path + "/" + user.getUserProfileName();
            File deleteFile = new File(deletePath);

            if (deleteFile.exists()) {
                deleteFile.delete();
            }

            // 5. 파일 이름 불러오기
            // 업로드한 파일의 이름
            String originalFileName = profile.getOriginalFilename();

            // 6. 난수 생성하기
            String uuid = UUID.randomUUID().toString().substring(0, 8);

            // 7. 업로드 할 파일이름 생성하기(3번 + 2번)
            String userProfileName = uuid + "_" + originalFileName;

            // 8. 파일 선택시 userDTO user객체의 userProfileName 필드에 업로드파일 이름 저장
            user.setUserProfileName(userProfileName);

            // 9. 파일 업로드
            // 빨간줄 생성 시 throws Exception 처리 해주어야함(service,service interface, controller 모두)
            String savePath = path + "/" + userProfileName;
            profile.transferTo(new File(savePath)); // 회원가입 정보가 제대로 저장되었을 경우 savePath에 저장하기 위함
        }

        // 주소 api 결합
        String addr1 = user.getAddr1();
        String addr2 = user.getAddr2();
        String addr3 = user.getAddr3();

        if (addr1.equals("") && addr2.equals("") && addr3.equals("")) {
            user.setUserAddr("");
        } else {
            String addr = "(" + addr1 + ") " + addr2 + ", " + addr3;
            user.setUserAddr(addr);
        }

        try {
            int result = 0;

            // 비밀번호 변경 X
            if (!StringUtils.isEmpty(user.getUserPw())) {
                user.setUserPw(pwEnc.encode(user.getUserPw()));

                result = userdao.userModiPw(user);

            } else {
                // user.getUserId().substring(0, 6).equals("kakao_")

                if (user.getUserId().contains("kakao_") && (user.getUserId().indexOf("kakao_") == 0)) {
                    result = userdao.userModiKakao(user);
                } else {
                    result = userdao.userModi(user);
                }

            }

            if (result > 0) {
                mav.setViewName("redirect:/userStore?userId=" + user.getUserId());
            } else {
                mav.setViewName("redirect:/userModiForm?userId=" + user.getUserId());
            }

        } catch (Exception e) {
            // 예외처리 발생 시
            mav.setViewName("redirect:/userModiForm?userId=" + user.getUserId());

            // 파일 삭제
            Path path = Paths.get(System.getProperty("user.dir")
                    , "src/main/resources/static/profile");
            String deletePath = path + "/" + user.getUserProfileName();

            File deleteFile = new File(deletePath);

            if (deleteFile.exists()) {
                deleteFile.delete();
            }

        }


        return mav;
    }

    ////////////////////////////////////////////////////////////////////////////
    // userStore : 회원 상점으로 이동
    @Override
    public ModelAndView userStore(String userId) {
        mav = new ModelAndView();

        userDTO user = userdao.userInfo(userId);                    // 상점 주인 정보

        DecimalFormat decFormat = new DecimalFormat("###,###");

        String userCash = decFormat.format(user.getUserCash());
        user.setStrUserCash(userCash);


        List<rvDTO> rvList = userdao.userRvList(userId);            // 리뷰 목록
        int rvCnt = userdao.userRvCnt(userId);                      // 리뷰 갯수

        int itemCnt = itemdao.itemCnt(userId);                      // 상점 주인의 거래 판매 물품 갯수
        int auCnt = audao.auCnt(userId);                            // 상점 주인의 경매 판매 물품 갯수
        int totalCnt = itemCnt + auCnt;                             // 전체 판매 물품 갯수


        // 2) Start 판매중 거래 목록 //
        List<itemDTO> itemSellingList = userdao.itemSellingList(userId);

        // 전체 거래 목록의 지역 출력
        for (int i = 0; i < itemSellingList.size(); i++) {
            reDTO region = userdao.regionOutput(itemSellingList.get(i).getItemRegion());
            String totalRegion = region.getReCity() + " " + region.getReNine();

            itemSellingList.get(i).setItRegion(totalRegion);
        }

        // 전체 거래 목록의 시간 출력 관련 정보
        for (int i = 0; i < itemSellingList.size(); i++) {
            // 현재 시간을 불러온다
            Date now = new Date(System.currentTimeMillis());

            // 현재시간 - 등록시간
            long deadDate = (now.getTime() - itemSellingList.get(i).getItemDate().getTime()) / 1000;

            // 현재시간과 등록시간의 차이가
            // 60초 이전의 값
            if (deadDate >= 0) {
                itemSellingList.get(i).setItDate("방금 전");
            }
            // 60초 이후의 값
            if (deadDate >= 60) {
                deadDate = deadDate / 60;
                itemSellingList.get(i).setItDate(deadDate + "분 전");
            }
            // 1시간 이후의 값
            if (deadDate >= 60) {
                deadDate = deadDate / 60;
                itemSellingList.get(i).setItDate(deadDate + "시간 전");
            }
            // 1일 이후의 값
            if (deadDate >= 24) {
                deadDate = deadDate / 24;
                itemSellingList.get(i).setItDate(deadDate + "일 전");
            }
            // 1달 이후의 값
            if (deadDate >= 30) {
                deadDate = deadDate / 30;
                itemSellingList.get(i).setItDate(deadDate + "달 전");
            }

        }
        // 2) End 판매중 거래 목록


        // 2-2) Start 판매중 경매 목록 //
        List<auDTO> auSellingList = userdao.auSellingList(userId);

        // 전체 거래 목록의 지역 출력
        for (int i = 0; i < auSellingList.size(); i++) {
            reDTO region = userdao.auRegionOutput(auSellingList.get(i).getAuRegion());
            String totalRegion = region.getReCity() + " " + region.getReNine();

            auSellingList.get(i).setAucRegion(totalRegion);
        }

        // 전체 거래 목록의 시간 출력 관련 정보
        for (int i = 0; i < auSellingList.size(); i++) {
            // 현재 시간을 불러온다
            Date now = new Date(System.currentTimeMillis());

            // 현재시간 - 등록시간
            long deadDate = (now.getTime() - auSellingList.get(i).getAuDate().getTime()) / 1000;

            // 현재시간과 등록시간의 차이가
            // 60초 이전의 값
            if (deadDate >= 0) {
                auSellingList.get(i).setAucDate("방금 전");
            }
            // 60초 이후의 값
            if (deadDate >= 60) {
                deadDate = deadDate / 60;
                auSellingList.get(i).setAucDate(deadDate + "분 전");
            }
            // 1시간 이후의 값
            if (deadDate >= 60) {
                deadDate = deadDate / 60;
                auSellingList.get(i).setAucDate(deadDate + "시간 전");
            }
            // 1일 이후의 값
            if (deadDate >= 24) {
                deadDate = deadDate / 24;
                auSellingList.get(i).setAucDate(deadDate + "일 전");
            }
            // 1달 이후의 값
            if (deadDate >= 30) {
                deadDate = deadDate / 30;
                auSellingList.get(i).setAucDate(deadDate + "달 전");
            }

        }
        // 2-2) End 판매중 경매 목록


        try {
            mav.addObject("user", user);
            mav.addObject("rvList", rvList);
            mav.addObject("rvCnt", rvCnt);
            mav.addObject("totalCnt", totalCnt);                // 전체 판매 상품 갯수

            mav.addObject("itemSellingList", itemSellingList);  // 판매중 거래목록
            mav.addObject("auSellingList", auSellingList);      // 판매중 경매 목록

            mav.setViewName("userStore");
        } catch (Exception e) {
            mav.setViewName("index");
        }

        return mav;
    }

    // userStoreIntroModi : 상점 소개글 가져오기(ajax)
    @Override
    public String GetUserStoreIntro(String userId) {

        String userStoreIntro = userdao.GetUserStoreIntro(userId);

        return userStoreIntro;
    }

    // userStoreIntroModi : 상점 소개글 수정하기(ajax)
    @Override
    public String userStoreIntroModi(String userId, String userStoreIntro) {

        userDTO user = new userDTO();

        user.setUserId(userId);
        user.setUserStoreIntro(userStoreIntro);

        int result = userdao.userStoreIntroModi(user);
        String strResult = "N";

        if (result > 0) {
            strResult = "Y";
        }

        return strResult;
    }


    // userDelete : 회원 삭제
    @Override
    public int userDelete(String userId, String userPw) {

        userDTO login = userdao.userLogin(userId);

        String encPw = login.getUserPw();

        int returnResult = 0;

        // pwEnc.mathces()로 입력한 비번 : user.getUserPW(), DB에 저장된 비번 : encPw
        // 일치하면 true, 일치하지 않으면 false 반환
        if (pwEnc.matches(userPw, encPw)) {
            // 성공
            int result = userdao.userDelete(userId);

            if (result > 0) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie c : cookies) {
                        if (c.getName().equals("idCookie")) {     // 쿠키 이름 가져오기
                            // 아이디 쿠키 초기화
                            Cookie logoutCookie = new Cookie("idCookie", null); // "(쿠키 이름)"에 대한 값을 null로 지정
                            logoutCookie.setMaxAge(0);          // 유효시간을 0으로 설정
                            logoutCookie.setPath("/");
                            response.addCookie(logoutCookie);   // 응답 헤더에 추가해서 없어지도록 함
                        }

                    }
                }
                if (session != null) {
                    session.invalidate();
                }

            } else {
                // 실패
                returnResult = 1;
            }
        } else {
            returnResult = 1;
        }
        return returnResult;
    }


    // kakaoUserDelete : 카카오 회원 탈퇴
    @Override
    public ModelAndView kakaoUserDelete(String userId) {
        mav = new ModelAndView();

        int result = userdao.userDelete(userId);

        if (result > 0) {
            mav.setViewName("redirect:/");
        } else {
            mav.setViewName("redirect:/userStore?userId=" + userId);
        }

        return mav;
    }

    // userIdFind : 회원 아이디 찾기
    @Override
    public List<String> userIdFind(String userName, String userPhone) {

        userDTO user = new userDTO();
        user.setUserName(userName);
        user.setUserPhone(userPhone);

        List<String> userIdList = userdao.userIdFind(user);

        return userIdList;
    }


    // userPwFind : 비밀번호 찾기 처리 시 일치하는 아이디 조회
    @Override
    public String userPwFind(String userId, String userPhone) {

        userDTO user = new userDTO();
        user.setUserId(userId);
        user.setUserPhone(userPhone);

        String getUserId = userdao.userPwFind(user);

        return getUserId;
    }

    // userPwChange : 비밀번호 변경 처리
    @Override
    public ModelAndView userPwChange(String userId, String userPw) {
        mav = new ModelAndView();


        userDTO user = new userDTO();
        user.setUserId(userId);
        user.setUserPw(pwEnc.encode(userPw));


        // 비밀번호 암호화
        // [1] 입력한 비밀번호 가져오기 : user.getUserPw()
        // [2] 입력한 비밀번호 암호화 : pwEnc.encode()
        // [3] 암호화 된 비밀번호 저장 : user.setUserpw()

        int result = userdao.userPwChange(user);

        if(result>0){
            mav.addObject("message", "success");
            mav.setViewName("userPwChangeForm");
        } else {
            mav.addObject("message", "changeFail");
            mav.setViewName("redirect:/userPwFindCheckForm");
        }

        return mav;
    }

    // strUserCash : 보유 캐시 출력
    @Override
    public String strUserCash(String userId) {

        userDTO user = userdao.userInfo(userId);

        DecimalFormat decFormat = new DecimalFormat("###,###");
        String strUserCash = decFormat.format(user.getUserCash());

        return strUserCash;
    }

}
