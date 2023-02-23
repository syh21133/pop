package com.sss.pop.controller;

import com.sss.pop.dto.noteDTO;
import com.sss.pop.service.noteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class noteController {

    private ModelAndView mav;

    private final noteService notesvc;


    // NOLS001  noteGetList : 받은 쪽지함 페이지 요청(로그인 아이디 + 페이징처리)
    @GetMapping("/noteGetList")
    public ModelAndView noteGetList(HttpSession session
            , @RequestParam(value = "page", required=false, defaultValue="1") int page
            , @RequestParam(value = "limit", required=false, defaultValue="10") int limit
            , @RequestParam(value = "category", required = false, defaultValue = "") String category
            , @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        mav = new ModelAndView();

        mav = notesvc.noteGetList(session, page, limit, category, keyword);

        return mav;
    }

    // NOSL002  noteSendList : 보낸 쪽지함 페이지 요청(로그인 아이디 + 페이징 처리)
    @GetMapping("/noteSendList")
    public ModelAndView noteSendList(HttpSession session
            , @RequestParam(value = "page", required=false, defaultValue="1") int page
            , @RequestParam(value = "limit", required=false, defaultValue="10") int limit
            , @RequestParam(value = "category", required = false, defaultValue = "") String category
            , @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        mav = new ModelAndView();

        mav = notesvc.noteSendList(session, page, limit, category, keyword);

        return mav;
    }









    // NOVW001 noteView : 쪽지 상세보기(보낸 쪽지, 받은 쪽지 포함)
    @GetMapping("/noteView")
    public ModelAndView noteView(@RequestParam("noteSeq") int noteSeq
            , @RequestParam(value = "pageName", required = false, defaultValue = "get") String pageName) {

        mav = new ModelAndView();

        mav = notesvc.noteView(noteSeq, pageName);

        return mav;
    }

    // NOWF001  noteWriteForm(GET) : 쪽지 보내기 페이지 요청(그냥 쪽지를 보낼 경우)
    @GetMapping("/noteWriteForm")
    public String noteWriteForm(){
        return "noteWriteForm";
    }

    // NOWF002  noteWriteForm(POST) : 쪽지 답장하기 페이지 요청(쪽지 상세보기에서 답장할 경우)
    @PostMapping("/noteWriteForm")
    public ModelAndView noteWriteForm(@ModelAttribute noteDTO note){
        mav = new ModelAndView();

        mav.addObject("note", note);
        mav.setViewName("noteWriteForm");

        return mav;
    }

    // NOWF003  noteWrite : 쪽지 답장하기
    @PostMapping("/noteWrite")
    public ModelAndView noteWrite(@ModelAttribute noteDTO note, HttpSession session) {
        mav = new ModelAndView();

        mav = notesvc.noteWrite(note, session);

        return mav;
    }

    // NDEL001  noteDelete : 받은 쪽지 삭제하기
    @GetMapping("/noteDelete")
    public ModelAndView noteDelete(@RequestParam("noteSeq") int noteSeq
            , @RequestParam(value = "pageName", required = false, defaultValue = "get") String pageName) {
        mav = new ModelAndView();

        mav = notesvc.noteDelete(noteSeq, pageName);

        return mav;
    }

    // noteGetNoneCheckCnt : 안 읽은 쪽지 갯수 header에 표시하기(ajax)
    @PostMapping("/noteGetNoneCheckCnt")
    public @ResponseBody int noteGetNoneCheckCnt(@RequestParam("userId") String userId) {

        int result = notesvc.noteGetNoneCheckCnt(userId);

        return result;
    }

    // 내쪽지함
    @PostMapping("/noteMynote")
    public @ResponseBody List<noteDTO> noteMynote(@RequestParam("userId") String userId) {

        List<noteDTO> mynoteList = notesvc.noteMynote(userId);

        return mynoteList;
    }
    
    //////////////////////////////////////////////////////////////////////////////////
    // 회원 상점에서 쪽지 보내기 버튼 클릭 시 쪽지작성 페이지 요청
    @GetMapping("/StoreNoteWriteForm")
    public ModelAndView StoreNoteWriteForm(@RequestParam("myId") String myId
                                        , @RequestParam("memId") String memId) {

        mav = new ModelAndView();

        noteDTO note = new noteDTO();

        note.setNoteSender(memId);

        mav.addObject("note", note);
        mav.setViewName("noteWriteForm");

        return mav;
    }
    
    
    
    
}
