package com.sss.pop.kakaoLogin;


import com.sss.pop.service.userService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakaoLogout")
public class kakaoLogoutController {

    private ModelAndView mav = new ModelAndView();

    private final kakaoLogoutService klosvc;

    private final userService usersvc;

    @GetMapping("/callbackLogout")
    public ModelAndView kakaoLogout(HttpSession session
                        , HttpServletRequest request
                        , HttpServletResponse response) throws Exception {


        klosvc.kakaoLogout(session, request ,response);


        mav.setViewName("redirect:/");
        return mav;
    }

    @GetMapping("/kakaoUserDelete")
    public ModelAndView kakaoUserDelete(HttpSession session
            , HttpServletRequest request
            , HttpServletResponse response
            , @RequestParam("state") String userId) throws Exception {




        klosvc.kakaoLogout(session, request ,response);

        mav = usersvc.kakaoUserDelete(userId);


        return mav;
    }


}