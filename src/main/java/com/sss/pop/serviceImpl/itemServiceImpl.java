package com.sss.pop.serviceImpl;

import com.sss.pop.auc.auDAO;
import com.sss.pop.dao.itemDAO;
import com.sss.pop.dao.userDAO;
import com.sss.pop.dto.*;
import com.sss.pop.service.itemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class itemServiceImpl implements itemService {

    private final itemDAO itemdao;

    private final HttpSession session;

    private ModelAndView mav;

    private final userDAO userdao;

    private final auDAO audao;

    private final HttpServletRequest request;


    // itemAdd : 판매할 중고물품 추가
    @Override
    public ModelAndView itemAdd(itemDTO item, reDTO region, caDTO category) throws IOException {

        mav = new ModelAndView();

        // 사진 파일 등록
        MultipartFile itemPhoto = item.getItemPhoto();

        if (!itemPhoto.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/itemPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFileName = itemPhoto.getOriginalFilename();

            String itemPhotoName = uuid + "_" + originalFileName;

            item.setItemPhotoName(itemPhotoName);

            String savePath = path + "/" + itemPhotoName;

            itemPhoto.transferTo(new File(savePath));
        }

        MultipartFile itemPhoto1 = item.getItemPhoto1();

        if (!itemPhoto1.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/itemPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFile1Name = itemPhoto1.getOriginalFilename();

            String itemPhoto1Name = uuid + "_" + originalFile1Name;

            item.setItemPhoto1Name(itemPhoto1Name);

            String savePath = path + "/" + itemPhoto1Name;

            itemPhoto1.transferTo(new File(savePath));
        }

        MultipartFile itemPhoto2 = item.getItemPhoto2();

        if (!itemPhoto2.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/itemPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFile2Name = itemPhoto2.getOriginalFilename();

            String itemPhoto2Name = uuid + "_" + originalFile2Name;

            item.setItemPhoto2Name(itemPhoto2Name);

            String savePath = path + "/" + itemPhoto2Name;

            itemPhoto2.transferTo(new File(savePath));
        }

        MultipartFile itemPhoto3 = item.getItemPhoto3();

        if (!itemPhoto3.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/itemPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFile3Name = itemPhoto3.getOriginalFilename();

            String itemPhoto3Name = uuid + "_" + originalFile3Name;

            item.setItemPhoto3Name(itemPhoto3Name);

            String savePath = path + "/" + itemPhoto3Name;

            itemPhoto3.transferTo(new File(savePath));
        }

        // 카테고리 시퀀스번호 caSeq 찾아오기
        item.setItemCategory(itemdao.caSeqFind(category));

        // 지역 시퀀스번호 reSeq 찾아오기
        item.setItemRegion(itemdao.reSeqFind(region));

        try {
            // 중고물품 등록 성공시 (에러나 예외처리가 없을 경우)
            itemdao.itemAdd(item);
            mav.setViewName("redirect:/index");
        } catch (Exception e) {
            // 에러 발생시
            mav.setViewName("14-404");
        }
        return mav;
    }

    // selectCaMain : mainCategory 선택시 subCategory 변경
    @Override
    public List<caDTO> selectCaMain(String caMain) {
        List<caDTO> caList = itemdao.selectCaMain(caMain);

        return caList;
    }

    // selectReCity : reCity 선택시 subCategory 변경
    @Override
    public List<reDTO> selectReCity(String reCity) {
        List<reDTO> reList = itemdao.selectReCity(reCity);

        return reList;
    }

    @Override
    public ModelAndView itemDelete(int itemNum) {
        mav = new ModelAndView();

        itemdao.itemLikesDelete(itemNum);
        itemdao.itemCommentsDelete(itemNum);
        itemdao.itemsDelete(itemNum);



        mav.setViewName("redirect:/index");
        return mav;
    }

    // 중고품 리스트
    @Override
    public ModelAndView itemList(int page, int category, String search) throws ParseException {
        mav = new ModelAndView();

        // 한 화면에 보여줄 페이지 번호 갯수
        int block = 5;

        // 한 화면에 보여줄 물품 갯수
        int limit = 9;

        int count;

        // 전체 중고품 갯수
        if (category == 0) {
            if (search == null) {
                // 카테고리 X / 검색 X
                count = itemdao.itemCount();
            } else {
                // 카테고리 X / 검색 O
                count = itemdao.itemCountS(search);
            }
        } else {
            if (search == null) {
                // 카테고리 O / 검색 X
                count = itemdao.itemCountC(category);
            } else {
                // 카테고리 O / 검색 O
                Map<String, Object> map = new HashMap<String, Object>();

                caDTO cate = new caDTO();
                cate.setCaSeq(category);

                seDTO sear = new seDTO();
                sear.setSearch(search);

                map.put("cate", cate);
                map.put("sear", sear);

                count = itemdao.itemCountSC(map);
            }
        }

        int startRow = (page - 1) * limit + 1;
        int endRow = page * limit;

        // ceil() 함수 : 올림 기능
        int maxPage = (int) (Math.ceil((double) count / limit));
        int startPage = (((int) (Math.ceil((double) page / block))) - 1) * block + 1;
        int endPage = startPage + block - 1;

        // 오류 방지 코드
        if (endPage > maxPage) {
            if (maxPage == 0) {
                maxPage = startPage;
            }
            endPage = maxPage;
        }

        // 페이징 객체 생성
        pageDTO paging = new pageDTO();

        paging.setPage(page);
        paging.setStartRow(startRow);
        paging.setEndRow(endRow);
        paging.setMaxPage(maxPage);
        paging.setStartPage(startPage);
        paging.setEndPage(endPage);
        paging.setLimit(limit);

        List<itemDTO> itemList;

        // 카테고리 값이 존재하지 않을경우
        if (category == 0) {
            // 검색값이 존재하지 않을 경우
            if (search == null) {
                itemList = itemdao.itemList(paging);
                // 카테고리가 없고, 검색값이 있을경우
            } else {
                seDTO sear = new seDTO();
                sear.setSearch(search);

                Map<String, Object> map = new HashMap<String, Object>();

                map.put("paging", paging);
                map.put("sear", sear);

                session.removeAttribute("search");
                session.setAttribute("search", sear);
                itemList = itemdao.itemListS(map);
                mav.addObject("sear", sear);
            }

        }

        // 카테고리 값이 존재할경우
        else {
            if (search == null) {
                caDTO cate = new caDTO();
                cate.setCaSeq(category);

                Map<String, Object> map = new HashMap<String, Object>();

                map.put("paging", paging);
                map.put("cate", cate);

                session.removeAttribute("category");
                session.setAttribute("category", cate);
                itemList = itemdao.itemListC(map);

                mav.addObject("cate", cate);
            }
            // 카테고리 값이 있고, 검색값이 존재할경우
            else {
                caDTO cate = new caDTO();
                cate.setCaSeq(category);

                seDTO sear = new seDTO();
                sear.setSearch(search);

                Map<String, Object> map = new HashMap<String, Object>();

                map.put("paging", paging);
                map.put("cate", cate);
                map.put("sear", sear);

                session.removeAttribute("category");
                session.setAttribute("category", cate);
                session.removeAttribute("search");
                session.setAttribute("search", sear);

                itemList = itemdao.itemListSC(map);

                mav.addObject("cate", cate);
                mav.addObject("sear", sear);
            }
        }

        // 시간&지역 출력 관련 정보
        for (int i = 0; i < itemList.size(); i++) {
            // 현재 시간을 불러온다
            Date now = new Date(System.currentTimeMillis());

            // 현재시간 - 등록시간
            long deadDate = (now.getTime() - itemList.get(i).getItemDate().getTime()) / 1000;

            // 현재시간과 등록시간의 차이가
            // 60초 이전의 값
            if (deadDate >= 0) {
                itemList.get(i).setItDate("방금 전");
            }
            // 1달 이후의 값
            if (deadDate >= (30 * 24 * 60 * 60)) {
                deadDate = deadDate / (30 * 24 * 60 * 60);
                itemList.get(i).setItDate(deadDate + "달 전");
            }
            // 1일 이후의 값
            if (deadDate >= (24 * 60 * 60)) {
                deadDate = deadDate / (24 * 60 * 60);
                itemList.get(i).setItDate(deadDate + "일 전");
            }
            // 1시간 이후의 값
            if (deadDate >= (60 * 60)) {
                deadDate = deadDate / (60 * 60);
                itemList.get(i).setItDate(deadDate + "시간 전");
            }
            // 60초 이후의 값
            if (deadDate >= 60) {
                deadDate = deadDate / 60;
                itemList.get(i).setItDate(deadDate + "분 전");
            }

            // 지역 정보 출력
            // reData에 itemRegionInfo로 reCity, reNine 정보를 가져온다.
            reDTO reData = itemdao.itemRegionInfo(itemList.get(i).getItemRegion());

            // itRegion에 reCity와 reNine값을 합친 값을 넣어준다.
            itemList.get(i).setItRegion(reData.getReCity() + " " + reData.getReNine());
        }

        if (request.getSession().getAttribute("login") != null) {
            userDTO user = (userDTO) request.getSession().getAttribute("login");

            String userId1 = user.getUserId();
            List<ilDTO> ilList = itemdao.ilList(userId1);
            mav.addObject("ilList", ilList);

        }

        mav.setViewName("itemList");
        mav.addObject("itemList", itemList);
        mav.addObject("paging", paging);

        return mav;
    }

    // 중고품 상세정보 확인
    @Override
    public ModelAndView itemView(int itemNum) {
        mav = new ModelAndView();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        // 판매물건 정보 가져오기
        itemDTO itemView = itemdao.itemView(itemNum);
        itemView.setItDate(dateFormat.format(itemView.getItemDate()));


        // 판매자 정보 가져오기
        userDTO userInfo = itemdao.itemSellerInfo(itemView.getItemSeller());

        // 거래 지역 정보 가져오기
        reDTO reInfo = itemdao.itemRegionInfo(itemView.getItemRegion());

        // 카테고리 정보 가져오기
        caDTO caInfo = itemdao.itemCategoryInfo(itemView.getItemCategory());


        mav.setViewName("itemView");
        mav.addObject("itemView", itemView);
        mav.addObject("userInfo", userInfo);
        mav.addObject("reInfo", reInfo);
        mav.addObject("caInfo", caInfo);

        return mav;
    }

    @Override
    public ModelAndView itemBList(int page, String keyword, String userId) {
        mav = new ModelAndView();
        // 한 화면에 보여줄 페이지 번호 갯수
        int block = 5;

        // 한 화면에 보여줄 게시글 갯수
        int limit = 5;

        String where = "itemName LIKE '%" + keyword + "%' AND itemBuyer = '" + userId + "'";

        int count = itemdao.itemBCount(where);


        int startRow = (page - 1) * limit + 1;
        int endRow = page * limit;

        int maxPage = (int) (Math.ceil((double) count / limit));
        int startPage = (((int) (Math.ceil((double) page / block))) - 1) * block + 1;
        int endPage = startPage + block - 1;

        if (endPage > maxPage) {
            endPage = maxPage;
        }

        // 페이징 객체 생성
        pageDTO paging = new pageDTO();

        paging.setPage(page);
        paging.setStartRow(startRow);
        paging.setEndRow(endRow);
        paging.setMaxPage(maxPage);
        paging.setStartPage(startPage);
        paging.setEndPage(endPage);
        paging.setLimit(limit);
        paging.setKeyword(keyword);
        paging.setWhere(where);

        List<itemDTO> pagingList = itemdao.itemBList(paging);


        // model
        mav.addObject("pagingList", pagingList);
        mav.addObject("paging", paging);

        // view
        mav.setViewName("itemBList");

        return mav;
    }

    @Override
    public ModelAndView itemSList(int page, String keyword, String userId) {
        mav = new ModelAndView();
        // 한 화면에 보여줄 페이지 번호 갯수
        int block = 5;

        // 한 화면에 보여줄 게시글 갯수
        int limit = 5;


        String where = "itemName LIKE '%" + keyword + "%' AND itemSeller = '" + userId + "'";


        int count = itemdao.itemSCount(where);


        int startRow = (page - 1) * limit + 1;
        int endRow = page * limit;

        int maxPage = (int) (Math.ceil((double) count / limit));
        int startPage = (((int) (Math.ceil((double) page / block))) - 1) * block + 1;
        int endPage = startPage + block - 1;

        if (endPage > maxPage) {
            endPage = maxPage;
        }

        // 페이징 객체 생성
        pageDTO paging = new pageDTO();

        paging.setPage(page);
        paging.setStartRow(startRow);
        paging.setEndRow(endRow);
        paging.setMaxPage(maxPage);
        paging.setStartPage(startPage);
        paging.setEndPage(endPage);
        paging.setLimit(limit);
        paging.setKeyword(keyword);
        paging.setWhere(where);

        List<itemDTO> pagingList = itemdao.itemSList(paging);

        // model
        mav.addObject("pagingList", pagingList);
        mav.addObject("paging", paging);

        // view
        mav.setViewName("itemSList");

        return mav;
    }

    // itemAuctionList : ()회원 상점 내 중고품 거래 물품 목록
    @Override
    public ModelAndView itemAuctionList(String userId) {
        mav = new ModelAndView();


        userDTO user = userdao.userInfo(userId);                    // 상점 주인 정보
        int itemCnt = itemdao.itemCnt(userId);                // 상점 주인의 거래 판매 물품 갯수
        int auCnt = audao.auCnt(userId);                        // 상점 주인의 경매 판매 물품 갯수


        // 1) Start 전체 거래 목록 //
        List<itemDTO> itemAllList = userdao.itemAllList(userId);

        if (itemAllList.size() != 0) {
            // 전체 거래 목록의 지역 출력
            for (int i = 0; i < itemAllList.size(); i++) {
                reDTO region = userdao.regionOutput(itemAllList.get(i).getItemRegion());
                String totalRegion = region.getReCity() + " " + region.getReNine();

                itemAllList.get(i).setItRegion(totalRegion);
            }

            // 전체 거래 목록의 시간 출력 관련 정보
            for (int i = 0; i < itemAllList.size(); i++) {
                // 현재 시간을 불러온다
                Date now = new Date(System.currentTimeMillis());

                // 현재시간 - 등록시간
                long deadDate = (now.getTime() - itemAllList.get(i).getItemDate().getTime()) / 1000;

                // 현재시간과 등록시간의 차이가
                // 60초 이전의 값
                if (deadDate >= 0) {
                    itemAllList.get(i).setItDate("방금 전");
                }
                // 60초 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    itemAllList.get(i).setItDate(deadDate + "분 전");
                }
                // 1시간 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    itemAllList.get(i).setItDate(deadDate + "시간 전");
                }
                // 1일 이후의 값
                if (deadDate >= 24) {
                    deadDate = deadDate / 24;
                    itemAllList.get(i).setItDate(deadDate + "일 전");
                }
                // 1달 이후의 값
                if (deadDate >= 30) {
                    deadDate = deadDate / 30;
                    itemAllList.get(i).setItDate(deadDate + "달 전");
                }

            }
            // 1) End 전체 거래 목록 //
        }


        // 2) Start 판매중 거래 목록 //
        List<itemDTO> itemSellingList = userdao.itemSellingList(userId);

        if (itemSellingList.size() != 0) {
            // 전체 거래 목록의 지역 출력
            for (int i = 0; i < itemSellingList.size(); i++) {
                reDTO region = userdao.regionOutput(itemSellingList.get(i).getItemRegion());
                String totalRegion = region.getReCity() + " " + region.getReNine();

                itemSellingList.get(i).setItRegion(totalRegion);
            }

            // 전체 거래 목록의 시간 출력 관련 정보
            for (int i = 0; i < itemSellingList.size(); i++) {
                // 현재 시간을 불러온다
                Date now = new Date(System.currentTimeMillis());

                // 현재시간 - 등록시간
                long deadDate = (now.getTime() - itemSellingList.get(i).getItemDate().getTime()) / 1000;

                // 현재시간과 등록시간의 차이가
                // 60초 이전의 값
                if (deadDate >= 0) {
                    itemSellingList.get(i).setItDate("방금 전");
                }
                // 60초 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    itemSellingList.get(i).setItDate(deadDate + "분 전");
                }
                // 1시간 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    itemSellingList.get(i).setItDate(deadDate + "시간 전");
                }
                // 1일 이후의 값
                if (deadDate >= 24) {
                    deadDate = deadDate / 24;
                    itemSellingList.get(i).setItDate(deadDate + "일 전");
                }
                // 1달 이후의 값
                if (deadDate >= 30) {
                    deadDate = deadDate / 30;
                    itemSellingList.get(i).setItDate(deadDate + "달 전");
                }

            }
            // 2) End 판매중 거래 목록
        }


        // 3) Start 판매완료 거래 목록 //
        List<itemDTO> itemDoneList = userdao.itemDoneList(userId);

        if (itemDoneList.size() != 0) {
            // 전체 거래 목록의 지역 출력
            for (int i = 0; i < itemDoneList.size(); i++) {
                reDTO region = userdao.regionOutput(itemDoneList.get(i).getItemRegion());
                String totalRegion = region.getReCity() + " " + region.getReNine();

                itemDoneList.get(i).setItRegion(totalRegion);
            }

            // 전체 거래 목록의 시간 출력 관련 정보
            for (int i = 0; i < itemDoneList.size(); i++) {
                // 현재 시간을 불러온다
                Date now = new Date(System.currentTimeMillis());

                // 현재시간 - 등록시간
                long deadDate = (now.getTime() - itemDoneList.get(i).getItemDate().getTime()) / 1000;

                // 현재시간과 등록시간의 차이가
                // 60초 이전의 값
                if (deadDate >= 0) {
                    itemDoneList.get(i).setItDate("방금 전");
                }
                // 60초 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    itemDoneList.get(i).setItDate(deadDate + "분 전");
                }
                // 1시간 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    itemDoneList.get(i).setItDate(deadDate + "시간 전");
                }
                // 1일 이후의 값
                if (deadDate >= 24) {
                    deadDate = deadDate / 24;
                    itemDoneList.get(i).setItDate(deadDate + "일 전");
                }
                // 1달 이후의 값
                if (deadDate >= 30) {
                    deadDate = deadDate / 30;
                    itemDoneList.get(i).setItDate(deadDate + "달 전");
                }

            }
            // 3) End 판매완료 거래 목록
        }


        // 1-2) Start 전체 경매 목록 //
        List<auDTO> auAllList = userdao.auAllList(userId);

        if (auAllList.size() != 0) {
            // 전체 경매 목록의 지역 출력
            for (int i = 0; i < auAllList.size(); i++) {
                reDTO region = userdao.regionOutput(auAllList.get(i).getAuRegion());
                String totalRegion = region.getReCity() + " " + region.getReNine();

                auAllList.get(i).setAucRegion(totalRegion);
            }

            // 전체 경매 목록의 시간 출력 관련 정보
            for (int i = 0; i < auAllList.size(); i++) {
                // 현재 시간을 불러온다
                Date now = new Date(System.currentTimeMillis());

                // 현재시간 - 등록시간
                long deadDate = (now.getTime() - auAllList.get(i).getAuDate().getTime()) / 1000;

                // 현재시간과 등록시간의 차이가
                // 60초 이전의 값
                if (deadDate >= 0) {
                    auAllList.get(i).setAucDate("방금 전");
                }
                // 60초 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    auAllList.get(i).setAucDate(deadDate + "분 전");
                }
                // 1시간 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    auAllList.get(i).setAucDate(deadDate + "시간 전");
                }
                // 1일 이후의 값
                if (deadDate >= 24) {
                    deadDate = deadDate / 24;
                    auAllList.get(i).setAucDate(deadDate + "일 전");
                }
                // 1달 이후의 값
                if (deadDate >= 30) {
                    deadDate = deadDate / 30;
                    auAllList.get(i).setAucDate(deadDate + "달 전");
                }

            }
            // 1-2) End 전체 경매 목록 //
        }


        // 2-2) Start 판매중 경매 목록 //
        List<auDTO> auSellingList = userdao.auSellingList(userId);

        if (auSellingList.size() != 0) {
            // 전체 경매 목록의 지역 출력
            for (int i = 0; i < auSellingList.size(); i++) {
                reDTO region = userdao.regionOutput(auSellingList.get(i).getAuRegion());
                String totalRegion = region.getReCity() + " " + region.getReNine();

                auSellingList.get(i).setAucRegion(totalRegion);
            }

            // 전체 경매 목록의 시간 출력 관련 정보
            for (int i = 0; i < auSellingList.size(); i++) {
                // 현재 시간을 불러온다
                Date now = new Date(System.currentTimeMillis());

                // 현재시간 - 등록시간
                long deadDate = (now.getTime() - auSellingList.get(i).getAuDate().getTime()) / 1000;

                // 현재시간과 등록시간의 차이가
                // 60초 이전의 값
                if (deadDate >= 0) {
                    auSellingList.get(i).setAucDate("방금 전");
                }
                // 60초 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    auSellingList.get(i).setAucDate(deadDate + "분 전");
                }
                // 1시간 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    auSellingList.get(i).setAucDate(deadDate + "시간 전");
                }
                // 1일 이후의 값
                if (deadDate >= 24) {
                    deadDate = deadDate / 24;
                    auSellingList.get(i).setAucDate(deadDate + "일 전");
                }
                // 1달 이후의 값
                if (deadDate >= 30) {
                    deadDate = deadDate / 30;
                    auSellingList.get(i).setAucDate(deadDate + "달 전");
                }

            }
            // 2-2) End 판매중 경매 목록
        }


        // 3-2) Start 판매완료 경매 목록 //
        List<auDTO> auDoneList = userdao.auDoneList(userId);


        if (auDoneList.size() != 0) {
            // 전체 경매 목록의 지역 출력
            for (int i = 0; i < auDoneList.size(); i++) {
                reDTO region = userdao.regionOutput(auDoneList.get(i).getAuRegion());
                String totalRegion = region.getReCity() + " " + region.getReNine();

                auDoneList.get(i).setAucRegion(totalRegion);
            }

            // 전체 경매 목록의 시간 출력 관련 정보
            for (int i = 0; i < auDoneList.size(); i++) {
                // 현재 시간을 불러온다
                Date now = new Date(System.currentTimeMillis());

                // 현재시간 - 등록시간
                long deadDate = (now.getTime() - auDoneList.get(i).getAuDate().getTime()) / 1000;

                // 현재시간과 등록시간의 차이가
                // 60초 이전의 값
                if (deadDate >= 0) {
                    auDoneList.get(i).setAucDate("방금 전");
                }
                // 60초 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    auDoneList.get(i).setAucDate(deadDate + "분 전");
                }
                // 1시간 이후의 값
                if (deadDate >= 60) {
                    deadDate = deadDate / 60;
                    auDoneList.get(i).setAucDate(deadDate + "시간 전");
                }
                // 1일 이후의 값
                if (deadDate >= 24) {
                    deadDate = deadDate / 24;
                    auDoneList.get(i).setAucDate(deadDate + "일 전");
                }
                // 1달 이후의 값
                if (deadDate >= 30) {
                    deadDate = deadDate / 30;
                    auDoneList.get(i).setAucDate(deadDate + "달 전");
                }

            }
            // 3-2) End 판매완료 경매 목록
        }


        try {
            mav.addObject("user", user);
            mav.addObject("itemCnt", itemCnt);                  //  상점 주인의 거래 판매 물품 갯수
            mav.addObject("auCnt", auCnt);                      // 상점 주인의 경매 판매 물품 갯수


            mav.addObject("itemAllList", itemAllList);          // 전체 거래 목록
            mav.addObject("itemSellingList", itemSellingList);  // 판매중 거래목록
            mav.addObject("itemDoneList", itemDoneList);        // 판매완료 거래목록

            mav.addObject("auAllList", auAllList);          // 전체 경매 목록
            mav.addObject("auSellingList", auSellingList);  // 판매중 경매 목록
            mav.addObject("auDoneList", auDoneList);        // 판매완료 경매 목록

            mav.setViewName("itemAuctionList");
        } catch (Exception e) {
            mav.setViewName("index");
        }


        return mav;
    }

    // 중고품 수정 페이지
    @Override
    public ModelAndView itemModifyForm(int itemNum) {
        mav = new ModelAndView();

        mav.setViewName("itemModify");
        mav.addObject("itemView", itemdao.itemView(itemNum));
        return mav;
    }

    // 중고품 수정
    @Override
    public ModelAndView itemModify(itemDTO item, reDTO region, caDTO category) throws IOException {
        mav = new ModelAndView();

        String itemPhotoOrigin = itemdao.itemPhotoNum(item.getItemNum());
        String itemPhoto1Origin = itemdao.itemPhoto1Num(item.getItemNum());
        String itemPhoto2Origin = itemdao.itemPhoto2Num(item.getItemNum());
        String itemPhoto3Origin = itemdao.itemPhoto3Num(item.getItemNum());

        itemdao.itemPhotoReset(item.getItemNum());

        // 사진 파일 등록
        MultipartFile itemPhoto = item.getItemPhoto();

        if (!itemPhoto.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/itemPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFileName = itemPhoto.getOriginalFilename();

            String itemPhotoName = uuid + "_" + originalFileName;

            item.setItemPhotoName(itemPhotoName);

            String savePath = path + "/" + itemPhotoName;

            itemPhoto.transferTo(new File(savePath));

        } else if (itemPhoto.isEmpty()) {

            item.setItemPhotoName(itemPhotoOrigin);
        }

        MultipartFile itemPhoto1 = item.getItemPhoto1();

        if (!itemPhoto1.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/itemPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFile1Name = itemPhoto1.getOriginalFilename();

            String itemPhoto1Name = uuid + "_" + originalFile1Name;

            item.setItemPhoto1Name(itemPhoto1Name);

            String savePath = path + "/" + itemPhoto1Name;

            itemPhoto1.transferTo(new File(savePath));
        } else if (itemPhoto1.isEmpty()) {

            item.setItemPhoto1Name(itemPhoto1Origin);
        }

        MultipartFile itemPhoto2 = item.getItemPhoto2();

        if (!itemPhoto2.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/itemPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFile2Name = itemPhoto2.getOriginalFilename();

            String itemPhoto2Name = uuid + "_" + originalFile2Name;

            item.setItemPhoto2Name(itemPhoto2Name);

            String savePath = path + "/" + itemPhoto2Name;

            itemPhoto2.transferTo(new File(savePath));
        } else if (itemPhoto2.isEmpty()) {

            item.setItemPhoto2Name(itemPhoto2Origin);
        }

        MultipartFile itemPhoto3 = item.getItemPhoto3();

        if (!itemPhoto3.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/itemPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFile3Name = itemPhoto3.getOriginalFilename();

            String itemPhoto3Name = uuid + "_" + originalFile3Name;

            item.setItemPhoto3Name(itemPhoto3Name);

            String savePath = path + "/" + itemPhoto3Name;

            itemPhoto3.transferTo(new File(savePath));
        } else if (itemPhoto3.isEmpty()) {

            item.setItemPhoto3Name(itemPhoto3Origin);
        }

        // 카테고리 시퀀스번호 caSeq 찾아오기
        item.setItemCategory(itemdao.caSeqFind(category));

        // 지역 시퀀스번호 reSeq 찾아오기
        item.setItemRegion(itemdao.reSeqFind(region));

        try {
            // 중고물품 수정 성공시 (에러나 예외처리가 없을 경우)
            itemdao.itemModify(item);
            mav.setViewName("redirect:/itemView?itemNum=" + item.getItemNum());
        } catch (Exception e) {
            // 에러 발생시
            mav.setViewName("14-404");
        }
        return mav;
    }

    // 중고품 좋아요
    @Override
    public String itemLike(int ilItem, String ilUser) {

        ilDTO il = new ilDTO();

        il.setIlUser(ilUser);
        il.setIlItem(ilItem);

        Integer result = itemdao.itemLikeCheck(il);

        if (result == null) {
            itemdao.itemLike(il);
            itemdao.itemLikeUP(ilItem);
            return "Y";
        } else {
            itemdao.itemLikeDelete(il);
            itemdao.itemLikeDown(ilItem);
            return "N";
        }
    }

    // 중고품 좋아요 체크
    @Override
    public String itemLikeCheck(int ilItem, String ilUser) {
        ilDTO il = new ilDTO();

        il.setIlItem(ilItem);

        il.setIlUser(ilUser);
        il.setIlItem(ilItem);

        Integer result = itemdao.itemLikeCheck(il);

        if (result == null) {
            return "N";
        } else {
            return "Y";
        }
    }

    // 해당 사용자 계좌의 돈을 확인.
    @Override
    public String userCashCheck(int itemNum, int itemPrice, String itemSeller, String itemName, String userId) {

        Map<String, Object> map = new HashMap<String, Object>();

        itemDTO item = new itemDTO();
        item.setItemNum(itemNum);
        item.setItemPrice(itemPrice);
        item.setItemSeller(itemSeller);
        item.setItemName(itemName);

        userDTO user = new userDTO();
        user.setUserId(userId);

        map.put("item", item);
        map.put("user", user);

        String result = itemdao.userCashCheck(map);

        if (result.equals("Y")) {

            // 실제 결제 (계좌에서 차감)
            itemdao.itemPayment(map);

            // 판매중으로 변경 + 해당 유저ID를 Buyer로 지정
            itemdao.itemCheckChange(map);

            // 판매자에게 쪽지 보내기
            itemdao.itemSendBuyNote(map);

            return result;
        } else {
            return result;
        }
    }

    @Override
    public String itemTakeCheck(int itemNum, int itemPrice, String itemSeller, String itemBuyer) {

        itemDTO item = new itemDTO();

        item.setItemNum(itemNum);
        item.setItemPrice(itemPrice);
        item.setItemSeller(itemSeller);
        item.setItemBuyer(itemBuyer);

        // 판매완료로 변경
        itemdao.itemTakeCheck(item);

        // 판매자의 캐시 증가
        itemdao.itemSell(item);

        // 판매자에게 쪽지 보내기
        itemdao.itemSendSellNote(item);

        return null;
    }

    @Override
    public int userCash(String userId) {
        return itemdao.userCash(userId);
    }

    @Override
    public List<rvDTO> itemReCheck(String userId) {
        List<rvDTO> irList = itemdao.itemReCheck(userId);

        return irList;
    }

    @Override
    public ModelAndView itemBB(int itemNum, String userId) {
        mav = new ModelAndView();

        itemDTO item = itemdao.itemView(itemNum);

        // 판매자의 캐시 증가
        itemdao.itemSell(item);

        // 판매자에게 쪽지 보내기
        itemdao.itemSendSellNote(item);

        int result = itemdao.itemBB(itemNum);

        if (result > 0) {
            mav.setViewName("redirect:/itemBList?userId=" + userId);
        }

        return mav;
    }

    // likeItemList : 관심 거래 상품 목록
    @Override
    public ModelAndView likeItemList(String userId, int page) {
        mav = new ModelAndView();

        // 페이징 처리
        // 한 화면에 보여줄 페이지 번호 갯수
        int block = 5;

        // 한 화면에 보여줄 상품 갯수
         int limit = 10;

        // 전체 관심 거래상품 갯수
        int count;
        count = itemdao.likeItemCount(userId);


        int startRow = (page - 1) * limit + 1;
        int endRow = page * limit;

        // ceil() 함수 : 올림 기능
        int maxPage = (int) (Math.ceil((double) count / limit));
        int startPage = (((int) (Math.ceil((double) page / block))) - 1) * block + 1;
        int endPage = startPage + block - 1;

        // 오류 방지 코드
        if (endPage > maxPage) {
            if(maxPage == 0){
                maxPage = startPage;
            }
            endPage = maxPage;
        }

        // 페이징 정보 인스턴스에 저장
        pageDTO paging = new pageDTO();

        paging.setPage(page);
        paging.setStartRow(startRow);
        paging.setEndRow(endRow);
        paging.setMaxPage(maxPage);
        paging.setStartPage(startPage);
        paging.setEndPage(endPage);
        paging.setLimit(limit);
        paging.setUserId(userId);


        // 넘겨줄 데이터를 담을 Map 객체 생성
//        Map<String, Object> map = new HashMap<String, Object>();
        // Map 영역에 넘어온 값을 객체 타입으로 저장
//        map.put("paging", paging);


        // paging 인스턴스 mav객체에 저장
        mav.addObject("paging", paging);

        // 관심 거래 상품 목록
        List<itemDTO> likeItemList = itemdao.likeItemList(paging);
        mav.addObject("likeItemList", likeItemList);


        // 관심 경매 상품 목록
//        List<alDTO> likeAuctionList = audao.likeAuctionList(userId);
//        mav.addObject("likeAuctionList", likeAuctionList);

        mav.setViewName("likeItemList");

        return mav;
    }

    // likeItemDel : 관심 거래 상품 삭제
    @Override
    public String likeItemDel(int ilItem, String ilUser) {
        ilDTO il = new ilDTO();

        il.setIlUser(ilUser);
        il.setIlItem(ilItem);

        Integer result = itemdao.itemLikeCheck(il);

        if (result != null) {   // 가져온 데이터로 ITEMLIKE 테이블을 조회했을 경우 값이 존재할 때
            itemdao.itemLikeDelete(il);
            itemdao.itemLikeDown(ilItem);
            return "Y";
        } else {                // 가져온 데이터로 ITEMLIKE 테이블을 조회했을 경우 값이 존재하지 않을 때
            return "N";
        }

    }


}