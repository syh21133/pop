package com.sss.pop.auc;

import com.sss.pop.dto.auDTO;
import com.sss.pop.dto.caDTO;
import com.sss.pop.dto.reDTO;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface auService {



    ModelAndView auView(int auNum);

    int auPropose(String userId, int price, int auNum) throws ParseException;

    int auComplete(int auNum);

    String auRemain(int auNum);

    

    List<reDTO> selectReCity(String reCity);

    List<caDTO> selectCaMain(String caMain);

    ModelAndView auAdd(auDTO auction, reDTO region, caDTO category) throws IOException;


    ModelAndView auList();

    ModelAndView auList(int page, int category, String search);

    ModelAndView auBList(int page, String keyword, String userId);

    ModelAndView auSList(int page, String keyword, String userId);

    String auLike(int alItem, String alUser);

    String auLikeCheck(int alItem, String alUser);

    ModelAndView auBB(int auNum, String userId);


    // likeAuctionList : 관심 거래 상품 목록
    ModelAndView likeAuctionList(String userId, int page);

    // likeAuctionDel : 관심 거래 상품 삭제
    String likeAuctionDel(int alItem, String alUser);
}
