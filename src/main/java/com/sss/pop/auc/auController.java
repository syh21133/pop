package com.sss.pop.auc;


import com.sss.pop.dto.auDTO;
import com.sss.pop.dto.caDTO;
import com.sss.pop.dto.reDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class auController {

    private ModelAndView mav;

    private final auService ausvc;

    private final HttpSession session;

    private final AuctionRoomRepository repository;

    private final auDAO audao;







   // 채팅서버 만들기
    @GetMapping(value = "/server")
    public String create(){

        List<auDTO> auList = audao.auList1();


         for(int i=0 ; i<auList.size();i++) {


             repository.createAuctionRoomDTO(auList.get(i).getAuNum());
         }

        return "redirect:/index";
    }


    // auList : 경매품 리스트
    @GetMapping("/auList")
    public ModelAndView auList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "category", required = false, defaultValue = "0") int category,
                               @RequestParam(value = "search", required = false, defaultValue = "") String search,
                               HttpServletRequest request ) {
        session.removeAttribute("category");
        session.removeAttribute("search");


        return ausvc.auList(page,category,search);
    }


    // AUCT002 auAdd : 판매할 중고품 등록 메소드
    @PostMapping("/auAdd")
    public ModelAndView auAdd(auDTO auction,reDTO region, caDTO category) throws IOException {
        return ausvc.auAdd(auction,region,category);
    }

    // AUCT001 경매물품 등록
    @GetMapping("/auAdd")
    public String auAdd()  {
        return "/auAdd";
    }

    // selectCaMain : mainCategory 선택시 subCategory 변경
    @PostMapping("/selectCaMain1")
    public @ResponseBody List<caDTO> selectCaMain(@RequestParam("caMain") String caMain) {
        List<caDTO> caList = ausvc.selectCaMain(caMain);
        return caList;
    }

    // selectReCity : reCity 선택시 subCategory 변경
    @PostMapping("/selectReCity1")
    public @ResponseBody List<reDTO> selectReCity(@RequestParam("reCity") String reCity) {
        List<reDTO> reList = ausvc.selectReCity(reCity);
        return reList;
    }

    // UMYP004 경매 구매 내역
    @GetMapping("/auBList")
    public ModelAndView auBList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                @RequestParam("userId")String userId){
        return ausvc.auBList(page, keyword, userId);
    }

    // UMYP005  경매 판매 내역
    @GetMapping("/auSList")
    public ModelAndView auSList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                @RequestParam("userId")String userId){
        return ausvc.auSList(page, keyword, userId);
    }

    @GetMapping("/auBB")
    public ModelAndView auBB(@RequestParam("auNum") int auNum,@RequestParam("userId")String userId) {
        return ausvc.auBB(auNum,userId);
    }


    // likeAuctionList : 관심 거래 상품 목록
    @GetMapping("/likeAuctionList")
    public ModelAndView likeAuctionList(@RequestParam("userId") String userId
            , @RequestParam(value = "page", required = false, defaultValue = "1") int page){

        mav = ausvc.likeAuctionList(userId, page);

        return mav;
    }

    // likeAuctionDel : 관심 거래 상품 삭제
    @PostMapping("/likeAuctionDel")
    public @ResponseBody String likeAuctionDel(@RequestParam("alItem") int alItem
            , @RequestParam("alUser") String alUser){

        String result = ausvc.likeAuctionDel(alItem, alUser);

        return result;
    }

    @PostMapping("/auTimeCheck")
    public @ResponseBody String auTimeCheck(){

        List<auDTO> auList = audao.auList2();

        Date now = new Date(System.currentTimeMillis());

        for(int i =0; i<auList.size();i++) {
            // 현재시간 - 등록시간
            long deadDate = (now.getTime() - auList.get(i).getAuDeadline().getTime());
            if(deadDate>0){
                if(auList.get(i).getAuCheck()==0) {
                    ausvc.auComplete(auList.get(i).getAuNum());
                }
            }
        }



        for(int i=0 ; i<auList.size();i++) {

            repository.createAuctionRoomDTO(auList.get(i).getAuNum());
        }

        return "result";
    }




}
