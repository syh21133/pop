package com.sss.pop.controller;

import com.sss.pop.dto.rvDTO;
import com.sss.pop.service.rvService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class rvController {

    private final rvService rvsvc;

    @GetMapping("/rvWriteForm")
    public ModelAndView rvWriteForm(@RequestParam("itemNum")int itemNum){
        return rvsvc.rvWriteForm(itemNum);
    }

    @PostMapping("/rvWrite")
    public ModelAndView rvWrite(@ModelAttribute rvDTO review){
        return rvsvc.rvWrite(review);
    }
    

    
}
