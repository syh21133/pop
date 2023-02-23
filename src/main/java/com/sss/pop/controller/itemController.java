package com.sss.pop.controller;

import com.sss.pop.dao.itemDAO;
import com.sss.pop.dto.*;
import com.sss.pop.service.itemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class itemController {

    private ModelAndView mav = new ModelAndView();

    private final itemService itemsvc;

    private final HttpSession session;

    private final itemDAO itemdao;

    // ITEM001  itemAddForm : 중고품 등록 페이지로 이동
    @GetMapping("/itemAdd")
    public String itemAddForm(HttpSession session) {
        return "itemAdd";
    }

    // ITEM002  itemAdd : 판매할 중고품 등록 메소드
    @PostMapping("/itemAdd")
    public ModelAndView itemAdd(itemDTO item, reDTO region, caDTO category) throws IOException {
        return itemsvc.itemAdd(item, region, category);
    }

    // ITEM003  selectCaMain : mainCategory 선택시 subCategory 변경
    @PostMapping("/selectCaMain")
    public @ResponseBody List<caDTO> selectCaMain(@RequestParam("caMain") String caMain) {
        List<caDTO> caList = itemsvc.selectCaMain(caMain);
        return caList;
    }

    // ITEM04   selectReCity : reCity 선택시 subCategory 변경
    @PostMapping("/selectReCity")
    public @ResponseBody List<reDTO> selectReCity(@RequestParam("reCity") String reCity) {
        List<reDTO> reList = itemsvc.selectReCity(reCity);
        return reList;
    }

    // ITLS001  itemList : 판매되는 중고품 리스트 페이지
    @GetMapping("/itemList")
    public ModelAndView itemList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                 @RequestParam(value = "category", required = false, defaultValue = "0") int category,
                                 @RequestParam(value = "search", required = false, defaultValue = "") String search
            , HttpServletRequest request) throws ParseException {
        session.removeAttribute("category");
        session.removeAttribute("search");

        return itemsvc.itemList(page, category, search);
    }

    // ITVW001 itemView : 판매되는 중고품의 상세정보 페이지
    @GetMapping("/itemView")
    public ModelAndView itemView(@RequestParam("itemNum") int itemNum,
                                 @SessionAttribute(name = "login", required = false) userDTO user,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        /* 조회수 로직 */
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(itemNum + "itemView")) {
                    oldCookie = cookie;
                }
            }
        }
        if (user != null) {

            if (oldCookie != null) {
                if (!oldCookie.getValue().contains("[" + user.getUserId() + "]")) {
                    // ITVW002  : itemHit = 조회수 증가
                    itemdao.itemHit(itemNum);
                    oldCookie.setValue(oldCookie.getValue() + "_[" + user.getUserId() + "]");
                    oldCookie.setPath("/");
                    oldCookie.setMaxAge(60 * 60 * 8);
                    response.addCookie(oldCookie);
                }
            } else {
                itemdao.itemHit(itemNum);
                Cookie newCookie = new Cookie(itemNum + "itemView", "[" + user.getUserId() + "]");
                newCookie.setPath("/");
                newCookie.setMaxAge(60 * 60 * 8);
                response.addCookie(newCookie);
            }
        } else {
            if (oldCookie != null) {
                if (!oldCookie.getValue().contains("[" + itemNum + "]")) {
                    itemdao.itemHit(itemNum);
                    oldCookie.setValue(oldCookie.getValue() + "_[" + itemNum + "]");
                    oldCookie.setPath("/");
                    oldCookie.setMaxAge(60 * 60 * 8);
                    response.addCookie(oldCookie);
                }
            } else {
                itemdao.itemHit(itemNum);
                Cookie newCookie = new Cookie(itemNum + "itemView", "[" + itemNum + "]");
                newCookie.setPath("/");
                newCookie.setMaxAge(60 * 60 * 8);
                response.addCookie(newCookie);
            }

        }

        return itemsvc.itemView(itemNum);
    }

    // itemModifyForm (Get) : 수정페이지 이동시 예외처리
    @GetMapping("/itemModifyForm")
    public String itemModifyForm() {
        return "14-404";
    }

    // ITMO001  itemModifyForm : 중고품 수정 페이지로 이동
    @PostMapping("/itemModifyForm")
    public ModelAndView itemModifyForm(@RequestParam("itemNum") int itemNum) {
        return itemsvc.itemModifyForm(itemNum);
    }

    // ITMO002  itemModify : 중고품 수정
    @PostMapping("/itemModify")
    public ModelAndView itemModify(itemDTO item ,reDTO region, caDTO category) throws IOException {
        return itemsvc.itemModify(item, region, category);
    }

    // IDEL001  itemDelete : 판매되는 중고품 내역 삭제
    @PostMapping("/itemDelete")
    public ModelAndView itemDelete(@RequestParam("itemNum") int itemNum) {
        return itemsvc.itemDelete(itemNum);
    }

    // UMYP002 거래 구매 내역
    @GetMapping("/itemBList")
    public ModelAndView itemBList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                  @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                  @RequestParam("userId") String userId) {
        return itemsvc.itemBList(page, keyword, userId);
    }

    // UMYP003 거래 판매 내역
    @GetMapping("/itemSList")
    public ModelAndView itemSList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                  @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                  @RequestParam("userId") String userId) {
        return itemsvc.itemSList(page, keyword, userId);
    }


    // UMYP007  itemAuctionList : ()회원 상점 내 중고품 거래 물품 목록 페이지
    @GetMapping("/itemAuctionList")
    public ModelAndView itemAuctionList(@RequestParam("userId") String userId) {

        mav = itemsvc.itemAuctionList(userId);

        return mav;
    }

    // ITVW002  itemLike : 중고품 좋아요
    @PostMapping("/itemLike")
    public @ResponseBody String itemLike(@RequestParam("ilItem") int ilItem,
                                         @RequestParam("ilUser") String ilUser) {
        return itemsvc.itemLike(ilItem, ilUser);
    }

    // itemLikeCheck : 중고품 좋아요 여부 확인
    @PostMapping("/itemLikeCheck")
    public @ResponseBody String itemLikeCheck(@RequestParam("ilItem") int ilItem,
                                              @RequestParam("ilUser") String ilUser) {
        return itemsvc.itemLikeCheck(ilItem, ilUser);
    }

    // ITVW003  userCashCheck : 중고품 구매시, 회원 계좌 확인
    @PostMapping("/userCashCheck")
    public @ResponseBody String userCashCheck(@RequestParam("itemNum") int itemNum,
                                              @RequestParam("itemPrice") int itemPrice,
                                              @RequestParam("itemSeller") String itemSeller,
                                              @RequestParam("itemName") String itemName,
                                              @RequestParam("userId") String userId) {
        return itemsvc.userCashCheck(itemNum, itemPrice, itemSeller, itemName, userId);
    }

    // itemTakeCheck : 수취확인버튼
    @PostMapping("/itemTakeCheck")
    public @ResponseBody String itemTakeCheck(@RequestParam("itemNum")int itemNum,
                                              @RequestParam("itemPrice")int itemPrice,
                                              @RequestParam("itemSeller")String itemSeller,
                                              @RequestParam("itemBuyer")String itemBuyer){
        return itemsvc.itemTakeCheck(itemNum, itemPrice, itemSeller, itemBuyer);
    }

    // userCash : 캐시 확인
    @PostMapping("/userCash")
    public @ResponseBody int userCash(@RequestParam("userId")String userId){
        return itemsvc.userCash(userId);
    }

    @GetMapping("/itemBB")
    public ModelAndView itemAuctionList(@RequestParam("itemNum") int itemNum, @RequestParam("userId") String userId) {
        return itemsvc.itemBB(itemNum,userId);
    }

    @PostMapping("/itemReCheck")
    public @ResponseBody List<rvDTO> itemReCheck(@RequestParam("userId") String userId){

        List<rvDTO> irList= itemsvc.itemReCheck(userId);

        return irList;
    }


    // UMYP006 likeItemList : 관심 거래 상품 목록
    @GetMapping("/likeItemList")
    public ModelAndView likeItemList(@RequestParam("userId") String userId
                                , @RequestParam(value = "page", required = false, defaultValue = "1") int page){

        mav = itemsvc.likeItemList(userId, page);

        return mav;
    }

    // likeItemDel : 관심 거래 상품 삭제
    @PostMapping("/likeItemDel")
    public @ResponseBody String likeItemDel(@RequestParam("ilItem") int ilItem
                                            , @RequestParam("ilUser") String ilUser){

        String result = itemsvc.likeItemDel(ilItem, ilUser);

        return result;
    }



}
