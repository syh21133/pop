package com.sss.pop.serviceImpl;

import com.sss.pop.dao.icDAO;
import com.sss.pop.dto.icDTO;
import com.sss.pop.service.icService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class icServiceImpl implements icService {

    private final icDAO icdao;

    private final HttpSession session;

    private ModelAndView mav;

    // 중고품 댓글 작성
    @Override
    public List<icDTO> itemCommentWrite(int icItemNum, String icContent, String icWriter) {
        icDTO ic = new icDTO();

        ic.setIcContent(icContent);
        ic.setIcItemNum(icItemNum);
        ic.setIcWriter(icWriter);

        int result = icdao.itemCommentWrite(ic);

        if(result > 0){
            List<icDTO> commentList = icdao.itemCommentList(ic.getIcItemNum());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

            for(int i = 0 ; i<commentList.size();i++){
                commentList.get(i).setIcmtDate(dateFormat.format(commentList.get(i).getIcDate()));
            }

            return commentList;
        } else {
            return null;
        }
    }

    @Override
    public List<icDTO> itemCommentList(int itemNum) {
        List<icDTO> commentList = icdao.itemCommentList(itemNum);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

        for(int i = 0 ; i<commentList.size();i++){
            commentList.get(i).setIcmtDate(dateFormat.format(commentList.get(i).getIcDate()));
        }

        return commentList;
    }

    @Override
    public List<icDTO> itemCommentDelete(int icNum,int icItemNum) {
        icdao.itemCommentDelete(icNum);

        List<icDTO> commentList = icdao.itemCommentList(icItemNum);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

        for(int i = 0 ; i<commentList.size();i++){
            commentList.get(i).setIcmtDate(dateFormat.format(commentList.get(i).getIcDate()));
        }

        return commentList;
    }

}
