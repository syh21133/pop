package com.sss.pop.serviceImpl;

import com.sss.pop.dao.rvDAO;
import com.sss.pop.dto.itemDTO;
import com.sss.pop.dto.rvDTO;
import com.sss.pop.service.rvService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
@RequiredArgsConstructor
public class rvServiceImpl implements rvService {
    private ModelAndView mav;

    private final rvDAO rvdao;

    @Override
    public ModelAndView rvWriteForm(int itemNum) {
        mav = new ModelAndView();

        itemDTO idto = rvdao.idto(itemNum);

        mav.addObject("idto",idto);
        mav.setViewName("rvWriteForm");

        return mav;
    }

    @Override
    public ModelAndView rvWrite(rvDTO review) {
        mav = new ModelAndView();

        int result = rvdao.rvWrite(review);

        if(result > 0){
            mav.setViewName("rvWriteS");
        } else {
            mav.setViewName("rvWriteForm");
        }

        return mav;
    }
}
