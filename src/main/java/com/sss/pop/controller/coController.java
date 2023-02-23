package com.sss.pop.controller;

import com.sss.pop.dto.coDTO;
import com.sss.pop.service.coService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class coController {

    private final coService csvc;


    // COMM001  cList : 댓글 불러오기
    @GetMapping("/coList")
    public @ResponseBody List<coDTO> coList(@RequestParam("cbNum") int cbNum) {

        List<coDTO> coList = csvc.coList(cbNum);

        return coList;
    }

    // COMM002  cmtWrite : 댓글 작성
    @RequestMapping(value = "/coWrite", method = RequestMethod.POST)
    public @ResponseBody List<coDTO> coWrite(@ModelAttribute coDTO comments) {

        List<coDTO> coList = csvc.coWrite(comments);

        return coList;
    }

    // COMM003  cmtDelete : 댓글 삭제
    @RequestMapping(value = "/coDelete", method = RequestMethod.POST)
    public @ResponseBody List<coDTO> coDelete(@ModelAttribute coDTO comments) {

        List<coDTO> coList = csvc.coDelete(comments);

        return coList;
    }

    // COMM004  cmtModify : 댓글 수정
    @RequestMapping(value = "/coModify", method = RequestMethod.POST)
    public @ResponseBody List<coDTO> cmtModify(@ModelAttribute coDTO comments) {

        List<coDTO> coList = csvc.coModify(comments);

        return coList;
    }
}
