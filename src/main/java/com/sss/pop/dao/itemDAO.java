package com.sss.pop.dao;

import com.sss.pop.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface itemDAO {
    int caSeqFind(caDTO category);

    int reSeqFind(reDTO region);

    void itemAdd(itemDTO item);

    int itemCount();

    int itemCountC(int caSeq);

    int itemCountS(String search);

    int itemCountSC(Map<String, Object> map);

    List<itemDTO> itemList(pageDTO paging);

    List<itemDTO> itemListC(Map<String, Object> map);

    List<itemDTO> itemListS(Map<String, Object> map);

    List<itemDTO> itemListSC(Map<String, Object> map);

    itemDTO itemView(int itemNum);

    void itemHit(int itemNum);

    userDTO itemSellerInfo(String itemSeller);

    reDTO itemRegionInfo(int itemRegion);

    caDTO itemCategoryInfo(int itemCategory);

    List<caDTO> selectCaMain(String caMain);

    List<reDTO> selectReCity(String reCity);

    List<itemDTO> selectCategoryItemList(int caSeq);

    int itemNumCheck(itemDTO item);

    void itemsDelete(int itemNum);

    void itemCommentsDelete(int itemNum);

    List<sellDTO> SBCount(String userId);

    List<itemDTO> itemBList(pageDTO paging);

    List<sellDTO> SSCount(String userId);

    List<itemDTO> itemSList(pageDTO paging);

    // 상점 주인의 판매 물품 갯수
    int itemCnt(String userId);

    void itemModify(itemDTO item);

    void itemLike(ilDTO il);

    Integer itemLikeCheck(ilDTO il);

    void itemLikeDelete(ilDTO il);

    String userCashCheck(Map<String, Object> map);

    void itemPayment(Map<String, Object> map);

    void itemLikeUP(int ilItem);

    void itemLikeDown(int ilItem);

    List<ilDTO> ilList(String userId);

    void itemCheckChange(Map<String, Object> map);

    void itemSendBuyNote(Map<String, Object> map);

    void itemPhotoReset(int itemNum);

    void itemTakeCheck(itemDTO item);

    void itemSell(itemDTO item);

    void itemSendSellNote(itemDTO item);

    int userCash(String userId);

    void itemLikesDelete(int itemNum);

    int itemBB(int itemNum);

    List<rvDTO> itemReCheck(String userId);


    int itemSCount(String where);

    int itemBCount(String where);

    // likeItemList : 관심 거래 상품 목록
    List<itemDTO> likeItemList(pageDTO paging);

    // likeItemCount : 관심 거래 상품 갯수
    int likeItemCount(String userId);

    String itemPhotoNum(int itemNum);

    String itemPhoto1Num(int itemNum);

    String itemPhoto2Num(int itemNum);

    String itemPhoto3Num(int itemNum);
}
