package com.sss.pop.kakaoLogin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class kakaoLogoutServiceImpl implements kakaoLogoutService {

    private final HttpSession session;



    // 로그아웃 액세스 토큰 요청
//    @Override
//    public String getLogoutAccessToken() {
//
//        String access_Token = "";
//        String refresh_Token = "";
//        String reqURL = "https://kauth.kakao.com/v1/user/unlink";
//
//        try {
//            URL url = new URL(reqURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//            //GET 요청을 위해 기본값이 false인 setDoOutput을 true로
//            conn.setRequestMethod("GET");
//            conn.setDoOutput(true);
//
//
//            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
//            StringBuilder sb = new StringBuilder();
//            sb.append("client_id=0d9934d491f5f82afbca7b5269823ea7");                               // TODO REST_API_KEY 입력
//            sb.append("&logout_redirect_uri=http://localhost:9090/kakaoLogout/callbackLogout");     // TODO 인가코드 받은 logout_redirect_uri 입력
//
//            bw.write(sb.toString());
//            bw.flush();
//
//            //결과 코드가 200이라면 성공
//            int responseCode = conn.getResponseCode();
//            System.out.println("로그아웃 responseCode 확인 : " + responseCode);
//
//            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
//            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = "";
//            String result = "";
//
//            while ((line = br.readLine()) != null) {
//                result += line;
//            }
//            System.out.println("로그아웃 response body 확인 : " + result);
//
//            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
//            JsonParser parser = new JsonParser();
//            JsonElement element = parser.parse(result);
//
//            access_Token = element.getAsJsonObject().get("access_token").getAsString();
//            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();
//
//            System.out.println("로그아웃 access_token 확인 : " + access_Token);
//            System.out.println("로그아웃 refresh_token 확인 : " + refresh_Token);
//
//            br.close();
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return access_Token;
//    }

    // 로그아웃 2
    public void kakaoLogout(HttpSession session, HttpServletRequest request , HttpServletResponse response) {

        String access_Token = "";

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie c : cookies) {
                if(c.getName().equals("tokenCookie")){     // 쿠키 이름 가져오기
                    access_Token = c.getValue();           // 쿠키 값 가져오기
                }
            }
        } else {
            access_Token = (String) session.getAttribute("AccessToken");
        }

        // 아이디 쿠키 초기화
        Cookie logoutCookie = new Cookie("idCookie", null); // "(쿠키 이름)"에 대한 값을 null로 지정
        logoutCookie.setMaxAge(0);          // 유효시간을 0으로 설정
        logoutCookie.setPath("/");
        response.addCookie(logoutCookie);   // 응답 헤더에 추가해서 없어지도록 함

        // 토큰 쿠키 초기화
        Cookie tokenCookie = new Cookie("tokenCookie", null); // "(쿠키 이름)"에 대한 값을 null로 지정
        tokenCookie.setMaxAge(0);          // 유효시간을 0으로 설정
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);   // 응답 헤더에 추가해서 없어지도록 함

        session.invalidate();

        String reqURL = "https://kapi.kakao.com/v1/user/logout";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
//            System.out.println("로그아웃 responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
//            System.out.println("로그아웃 response body : "+result);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



}
