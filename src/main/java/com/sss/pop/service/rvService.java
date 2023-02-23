package com.sss.pop.service;

import com.sss.pop.dto.rvDTO;
import org.springframework.web.servlet.ModelAndView;

public interface rvService {
    ModelAndView rvWriteForm(int itemNum);

    ModelAndView rvWrite(rvDTO review);
}
