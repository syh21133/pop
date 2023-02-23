package com.sss.pop.kakaopay;

import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log
@Controller
public class SampleController {
    @Setter(onMethod_ = @Autowired)
    private KakaoPay kakaopay;


    @GetMapping("/kakaoPay")
    public void kakaoPayGet() {

    }

    // UCAS002
    @PostMapping("/kakaoPay")
    public String kakaoPay(@RequestParam("price") int price,@RequestParam("userId") String userId) {
        log.info("kakaoPay post............................................");

        return "redirect:" + kakaopay.kakaoPayReady(price,userId);

    }

    @GetMapping("/kakaoPaySuccess")
    public void kakaoPaySuccess(@RequestParam("pg_token") String pg_token, Model model,String userId) {
        log.info("kakaoPaySuccess get............................................");
        log.info("kakaoPaySuccess pg_token : " + pg_token);

        model.addAttribute("info", kakaopay.kakaoPayInfo(pg_token));
        model.addAttribute("userId",userId);

    }

    @GetMapping("/kakaoPayFail")
    public String kakaoPayFail() {


        return "kakaoPayFail";
    }

}
