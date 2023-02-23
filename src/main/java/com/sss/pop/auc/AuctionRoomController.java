package com.sss.pop.auc;

import com.sss.pop.dto.noteDTO;
import com.sss.pop.dto.userDTO;
import com.sss.pop.service.noteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/auction")
@Log4j2
public class AuctionRoomController {

    private final AuctionRoomRepository repository;

    private final auService ausvc;

    private final auDAO audao;

    private final noteService notesvc;

    private final HttpSession session;



    //채팅방 개설
    @PostMapping(value = "/auView")
    public String create(@RequestParam String name, RedirectAttributes rttr){

        log.info("# Create Chat Room , name: " + name);
        rttr.addFlashAttribute("roomName", repository.createAuctionRoomDTO(Integer.parseInt(name)));
        return "redirect:/auction/auView";
    }



    // AUVW001  경매품 상세보기
    @GetMapping("/auView")
    public ModelAndView getRoom(String auNum, Model model, @SessionAttribute(name = "login", required = false) userDTO user,
                                HttpServletRequest request,
                                HttpServletResponse response){

        log.info("# get Chat Room, roomID : " + auNum);


        /* 조회수 로직 */
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(auNum+"auView")) {
                    oldCookie = cookie;
                }
            }
        }
        if(user != null) {

            if (oldCookie != null) {
                if (!oldCookie.getValue().contains("[" + user.getUserId() + "]")) {
                    audao.auHit(Integer.parseInt(auNum));
                    oldCookie.setValue(oldCookie.getValue() + "_[" + user.getUserId() + "]");
                    oldCookie.setPath("/");
                    oldCookie.setMaxAge(60 * 60 * 8);
                    response.addCookie(oldCookie);
                }
            } else {
                audao.auHit(Integer.parseInt(auNum));
                Cookie newCookie = new Cookie(auNum+"auView","[" + user.getUserId() + "]");
                newCookie.setPath("/");
                newCookie.setMaxAge(60 * 60 * 8);
                response.addCookie(newCookie);
            }
        }else {
            if (oldCookie != null) {
                if (!oldCookie.getValue().contains("[" + auNum  + "]")) {
                    audao.auHit(Integer.parseInt(auNum));
                    oldCookie.setValue(oldCookie.getValue() + "_[" + auNum + "]");
                    oldCookie.setPath("/");
                    oldCookie.setMaxAge(60 * 60 * 8);
                    response.addCookie(oldCookie);
                }
            } else {
                audao.auHit(Integer.parseInt(auNum));
                Cookie newCookie = new Cookie(auNum+"auView","[" + auNum + "]");
                newCookie.setPath("/");
                newCookie.setMaxAge(60 * 60 * 8);
                response.addCookie(newCookie);
            }

        }
        model.addAttribute("auNum", repository.findRoomById(auNum));
        return ausvc.auView(Integer.parseInt(auNum));
    }

    // AUPP001  경매 입찰
    @PostMapping(value = "/auPropose")
    public @ResponseBody int auPropose(@RequestParam("userId") String userId, @RequestParam("price") int price, @RequestParam("auNum") int auNum) throws ParseException {
        int result = ausvc.auPropose(userId,price,auNum);

        return result;
    }

    // AUPP002  경매 끝날 시
    @PostMapping(value = "/auComplete")
    public @ResponseBody int auComplete(@RequestParam("auNum") int auNum){
        int result = ausvc.auComplete(auNum);

        return result;
    }

    // 경매남은 시간
    @PostMapping(value = "/auRemain")
    public @ResponseBody String auRemain(@RequestParam("auNum") int auNum){

        return ausvc.auRemain(auNum);
    }


    // 내쪽지함
    @PostMapping("/noteMynote")
    public @ResponseBody List<noteDTO> noteMynote(@RequestParam("userId") String userId) {


        List<noteDTO> mynoteList = notesvc.noteMynote(userId);

        return mynoteList;
    }

    // auLike : 중고품 좋아요
    @PostMapping ("/auLike")
    public @ResponseBody String itemLike(@RequestParam("alItem")int alItem,
                                         @RequestParam("alUser")String alUser){
        return ausvc.auLike(alItem,alUser);
    }

    // itemLikeCheck : 중고품 좋아요 여부 확인
    @PostMapping ("/auLikeCheck")
    public @ResponseBody String itemLikeCheck(@RequestParam("alItem")int alItem,
                                              @RequestParam("alUser")String alUser) {
        return ausvc.auLikeCheck(alItem,alUser);
    }




}
