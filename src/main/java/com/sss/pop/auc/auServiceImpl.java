package com.sss.pop.auc;


import com.sss.pop.dto.*;
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
public class auServiceImpl implements auService {

    private final HttpSession session;

    private ModelAndView mav;

    private final auDAO audao;

    private final AuctionRoomRepository repository;

    private final HttpServletRequest request;

    // 경매품 리스트
    @Override
    public ModelAndView auList() {
            mav = new ModelAndView();



            List<auDTO> auList = audao.auList1();



        mav.setViewName("auList");
        mav.addObject("auList", auList);



            return mav;
        }

    @Override
    public ModelAndView auList(int page, int category, String search) {
        mav = new ModelAndView();

        // 한 화면에 보여줄 페이지 번호 갯수
        int block = 5;

        // 한 화면에 보여줄 물품 갯수
        int limit = 9;

        int count;

        // 전체 중고품 갯수
        if(category  == 0){
            if(search == null){
                // 카테고리 X / 검색 X
                count = audao.auCount();
            } else {
                // 카테고리 X / 검색 O
                count = audao.auCountS(search);
            }
        } else {
            if(search == null){
                // 카테고리 O / 검색 X
                count = audao.auCountC(category);
            } else {
                // 카테고리 O / 검색 O
                Map<String, Object> map = new HashMap<String, Object>();

                caDTO cate = new caDTO();
                cate.setCaSeq(category);

                seDTO sear = new seDTO();
                sear.setSearch(search);

                map.put("cate", cate);
                map.put("sear", sear);

                count = audao.auCountSC(map);
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
            if(maxPage == 0){
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

        List<auDTO> auList;

        // 카테고리 값이 존재하지 않을경우
        if(category == 0 ) {
            // 검색값이 존재하지 않을 경우
            if(search == null){
                auList = audao.auList(paging);
                // 카테고리가 없고, 검색값이 있을경우
            } else {
                seDTO sear = new seDTO();
                sear.setSearch(search);

                Map<String, Object> map = new HashMap<String,Object>();

                map.put("paging", paging);
                map.put("sear", sear);

                session.removeAttribute("search");
                session.setAttribute("search", sear);
                auList = audao.auListS(map);
                mav.addObject("sear",sear);
            }

        }

        // 카테고리 값이 존재할경우
        else {
            if(search == null) {
                caDTO cate = new caDTO();
                cate.setCaSeq(category);

                Map<String, Object> map = new HashMap<String, Object>();

                map.put("paging", paging);
                map.put("cate", cate);

                session.removeAttribute("category");
                session.setAttribute("category", cate);
                auList = audao.auListC(map);

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

                auList = audao.auListSC(map);


                mav.addObject("cate", cate);
                mav.addObject("sear",sear);
            }
        }

        // 시간&지역 출력 관련 정보
        for(int i =0 ; i < auList.size(); i++){
            // 현재 시간을 불러온다
            Date now = new Date(System.currentTimeMillis());

            // 현재시간 - 등록시간
            long deadDate = (auList.get(i).getAuDeadline().getTime()-now.getTime())/1000;

            // 현재시간과 등록시간의 차이가
            // 60초 이전의 값
            if(deadDate >= 0){
                auList.get(i).setAucDate("곧");
            }
            // 1달 이후의 값
            if(deadDate >= (30*24*60*60)){
                deadDate = deadDate/(30*24*60*60);
                auList.get(i).setAucDate(deadDate+"달 후");
            }
            // 1일 이후의 값
            if(deadDate >=(24*60*60)){
                deadDate = deadDate/(24*60*60);
                auList.get(i).setAucDate(deadDate+"일 후");
            }
            // 1시간 이후의 값
            if(deadDate >= (60*60)){
                deadDate = deadDate/(60*60);
                auList.get(i).setAucDate(deadDate+"시간 후");
            }
            // 60초 이후의 값
            if(deadDate >= 60){
                deadDate = deadDate/60;
                auList.get(i).setAucDate(deadDate+"분 후");
            }





            // 지역 정보 출력
            // reData에 auRegionInfo로 reCity, reNine 정보를 가져온다.
            reDTO reData = audao.auRegionInfo(auList.get(i).getAuRegion());

            // itRegion에 reCity와 reNine값을 합친 값을 넣어준다.
            auList.get(i).setAucRegion(reData.getReCity() + " " + reData.getReNine());
        }

        if (request.getSession().getAttribute("login") != null) {


            userDTO user = (userDTO) request.getSession().getAttribute("login");

            String userId1 = user.getUserId();
            List<alDTO> alList = audao.alList(userId1);
            mav.addObject("alList",alList);

        }



        mav.setViewName("auList");
        mav.addObject("auList", auList);
        mav.addObject("paging", paging);

        return mav;
    }


    // 경매품 상세보기
    @Override
    public ModelAndView auView(int auNum) {
        mav = new ModelAndView();

        // 판매물건 정보 가져오기
        auDTO auView = audao.auView(auNum);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        auView.setAucDate(dateFormat.format(auView.getAuDeadline()));
        auView.setADate(dateFormat.format(auView.getAuDate()));


        // 판매자 정보 가져오기
        userDTO userInfo = audao.auSellerInfo(auView.getAuSeller());

        // 거래 지역 정보 가져오기
        reDTO reInfo = audao.auRegionInfo(auView.getAuRegion());

        // 카테고리 정보 가져오기
        caDTO caInfo = audao.auCategoryInfo(auView.getAuCategory());




        mav.setViewName("auView");
        mav.addObject("auView", auView);
        mav.addObject("userInfo", userInfo);
        mav.addObject("reInfo", reInfo);
        mav.addObject("caInfo", caInfo);

        return mav;
    }


    // 경매품 입찰
    @Override
    public int auPropose(String userId, int price, int auNum) throws ParseException {
        auDTO auction = audao.auView(auNum);



        userDTO loginUser = audao.userInfo(userId);


        // 0 일때는 잔액부족
        if(loginUser.getUserCash()<=price){
            return 0;
        }
        // 1 일때는 이미 내가 입찰
        // 2 일때 실행
        else if(auction.getAuBuyer()==null){
            auction.setAuBuyer(userId);
            auction.setAuPrice(price);
            loginUser.setUserCash(loginUser.getUserCash()-price);
            audao.cashUpdate(loginUser);
            audao.auUpdate(auction);

            return  2;}
        else {
            if(auction.getAuBuyer().equals(userId)) {
                return 1;
            }
            else{
                Date now = new Date(System.currentTimeMillis());

                long deadDate = (auction.getAuDeadline().getTime()-now.getTime())/60000;


                // 시간 계산후 남은시간이 5분안쪽으로 남았으면 현재시간에서 5분 추가
                if(deadDate <= 5){
                    audao.deadUpdate(auNum);

                };


                // 마감시간이랑 현재시간 비교해서 5분 안쪽이면 지금시간에서 5분 늘린걸 마감시간에 저장

                // 경매품 원래 구매자한테 가격 다시 입금
                userDTO buyer = audao.userInfo(auction.getAuBuyer());

                // 원래 입찰자한테 캐시 환급

                audao.sendNotebuyer(auction);
                buyer.setUserCash(buyer.getUserCash()+ auction.getAuPrice());
                audao.cashUpdate(buyer);

                // 경매품 구매자, 가격 변경 , 구매자 캐시 빼기
                auction.setAuBuyer(userId);
                auction.setAuPrice(price);
                loginUser.setUserCash(loginUser.getUserCash()-price);
                audao.cashUpdate(loginUser);
                audao.auUpdate(auction);

                return 2;
            }



        }


    }

    // 경매 마감
    @Override
    public int auComplete(int auNum) {


        auDTO auction = audao.auView(auNum);
        
        // 쪽지를 최초 한번만 보내기 위해 작성
        if(auction.getAuBuyer()!= null){
        if(auction.getAuCheck() == 0){
            audao.sendNoteSeller(auction);
            audao.sendNoteComplete(auction);
        }}

        int result = audao.auComplete(auNum);

        return result;
    }

    // 경매 남은 시간
    @Override
    public String auRemain(int auNum) {
        auDTO auView = audao.auView(auNum);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        auView.setAucDate(dateFormat.format(auView.getAuDeadline()));


        return auView.getAucDate();
    }


    // 경매물품 등록
    @Override
    public ModelAndView auAdd(auDTO auction,reDTO region, caDTO category) throws IOException {

        mav = new ModelAndView();

        // 사진 파일 등록1
        MultipartFile auPhoto = auction.getAuPhoto();

        if (!auPhoto.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/auPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFileName = auPhoto.getOriginalFilename();

            String auPhotoName = uuid + "_" + originalFileName;

            auction.setAuPhotoName(auPhotoName);

            String savePath = path + "/" + auPhotoName;

            auPhoto.transferTo(new File(savePath));
        }
        // 사진 파일 등록2
        MultipartFile auPhoto1 = auction.getAuPhoto1();

        if (!auPhoto1.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/auPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFileName = auPhoto1.getOriginalFilename();

            String auPhoto1Name = uuid + "_" + originalFileName;

            auction.setAuPhoto1Name(auPhoto1Name);

            String savePath = path + "/" + auPhoto1Name;

            auPhoto1.transferTo(new File(savePath));
        }
        // 사진 파일 등록3
        MultipartFile auPhoto2 = auction.getAuPhoto2();

        if (!auPhoto2.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/auPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFileName = auPhoto2.getOriginalFilename();

            String auPhoto2Name = uuid + "_" + originalFileName;

            auction.setAuPhoto2Name(auPhoto2Name);

            String savePath = path + "/" + auPhoto2Name;

            auPhoto2.transferTo(new File(savePath));
        }
        // 사진 파일 등록4
        MultipartFile auPhoto3 = auction.getAuPhoto3();

        if (!auPhoto3.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/auPhoto");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFileName = auPhoto3.getOriginalFilename();

            String auPhoto3Name = uuid + "_" + originalFileName;

            auction.setAuPhoto3Name(auPhoto3Name);

            String savePath = path + "/" + auPhoto3Name;

            auPhoto3.transferTo(new File(savePath));
        }

        // 경매시간 합치기

        auction.setAuDeadlineDate((auction.getAuDeadlineDay()*24*60)+(auction.getAuDeadlineHour()*60)+auction.getAuDeadlineMinute());


        // 카테고리 시퀀스번호 caSeq 찾아오기
        auction.setAuCategory(audao.caSeqFind(category));


        // 지역 시퀀스번호 reSeq 찾아오기
        auction.setAuRegion(audao.reSeqFind(region));


        //try {
            // 중고물품 등록 성공시 (에러나 예외처리가 없을 경우)
            audao.auAdd(auction);
            List<auDTO> auList = audao.auList1();

            for(int i=0 ; i<auList.size();i++) {

                repository.createAuctionRoomDTO(auList.get(i).getAuNum());
            }

            mav.setViewName("redirect:/auList");
        /*} catch (Exception e) {
//            System.out.println("물건 등록 실패!");
            mav.setViewName("14-404");
        }*/

        return mav;
    }



    // selectCaMain : mainCategory 선택시 subCategory 변경
    @Override
    public List<caDTO> selectCaMain(String caMain) {
        List<caDTO> caList = audao.selectCaMain(caMain);

        return caList;
    }

    // selectReCity : reCity 선택시 subCategory 변경
    @Override
    public List<reDTO> selectReCity(String reCity) {
        List<reDTO> reList = audao.selectReCity(reCity);

        return reList;
    }

    @Override
    public ModelAndView auBList(int page, String keyword, String userId) {

        mav = new ModelAndView();
        // 한 화면에 보여줄 페이지 번호 갯수
        int block = 5;

        // 한 화면에 보여줄 게시글 갯수
        int limit = 5;

        String where = "auName LIKE '%" + keyword + "%' AND auBuyer = '" + userId + "'";

        int count = audao.auBCount(where);

        int startRow = (page - 1) * limit + 1;
        int endRow = page * limit;

        int maxPage = (int)(Math.ceil((double) count / limit ));
        int startPage = (((int)(Math.ceil((double) page / block))) - 1) * block + 1;
        int endPage = startPage + block - 1;

        if(endPage > maxPage) {
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



        List<auDTO> pagingList = audao.auBList(paging);



        // model
        mav.addObject("pagingList", pagingList);
        mav.addObject("paging", paging);

        // view
        mav.setViewName("auBList");

        return mav;
    }

    @Override
    public ModelAndView auSList(int page, String keyword, String userId) {

        mav = new ModelAndView();
        // 한 화면에 보여줄 페이지 번호 갯수
        int block = 5;

        // 한 화면에 보여줄 게시글 갯수
        int limit = 5;

        String where = "auName LIKE '%" + keyword + "%' AND auSeller = '" + userId + "'";

        int count = audao.auSCount(where);

        int startRow = (page - 1) * limit + 1;
        int endRow = page * limit;

        int maxPage = (int)(Math.ceil((double) count / limit ));
        int startPage = (((int)(Math.ceil((double) page / block))) - 1) * block + 1;
        int endPage = startPage + block - 1;

        if(endPage > maxPage) {
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



        List<auDTO> pagingList = audao.auSList(paging);



        // model
        mav.addObject("pagingList", pagingList);
        mav.addObject("paging", paging);

        // view
        mav.setViewName("auSList");

        return mav;
    }

    @Override
    public String auLike(int alItem, String alUser) {
        alDTO al = new alDTO();

        al.setAlItem(alItem);

        al.setAlUser(alUser);
        al.setAlItem(alItem);

        Integer result = audao.auLikeCheck(al);

        if(result == null) {
            audao.auLike(al);
            audao.auLikeUP(alItem);
            return "Y";
        } else {
            audao.auLikeDelete(al);
            audao.auLikeDown(alItem);
            return "N";
        }
    }

    @Override
    public String auLikeCheck(int alItem, String alUser) {
        alDTO al = new alDTO();

        al.setAlItem(alItem);

        al.setAlUser(alUser);
        al.setAlItem(alItem);

        Integer result = audao.auLikeCheck(al);

        if (result == null){
            return "N";
        } else {
            return "Y";
        }
    }

    @Override
    public ModelAndView auBB(int auNum, String userId) {
        mav=new ModelAndView();

        auDTO auction = audao.auView(auNum);

        int result = audao.auBB(auNum);

        // 판매자의 캐시 증가
        audao.auSell(auction);

        // 판매자에게 쪽지 보내기
        audao.auSendSellNote(auction);

        if(result > 0){
            mav.setViewName("redirect:/auBList?userId="+userId);
        }

        return mav;
    }


    // likeAuctionList : 관심 거래 상품 목록
    @Override
    public ModelAndView likeAuctionList(String userId, int page) {
        mav = new ModelAndView();

        // 페이징 처리
        // 한 화면에 보여줄 페이지 번호 갯수
        int block = 5;

        // 한 화면에 보여줄 상품 갯수
        int limit = 10;

        // 전체 관심 거래상품 갯수
        int count;
        count = audao.likeAuctionCount(userId);


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
//        List<itemDTO> likeItemList = itemdao.likeItemList(paging);
//        mav.addObject("likeItemList", likeItemList);


        // 관심 경매 상품 목록
        List<auDTO> likeAuctionList = audao.likeAuctionList(paging);
        mav.addObject("likeAuctionList", likeAuctionList);

        mav.setViewName("likeAuctionList");

        return mav;
    }

    // likeAuctionDel : 관심 거래 상품 삭제
    @Override
    public String likeAuctionDel(int alItem, String alUser) {
        alDTO al = new alDTO();

        al.setAlUser(alUser);
        al.setAlItem(alItem);

        Integer result = audao.auLikeCheck(al);

        if (result != null) {   // 가져온 데이터로 AUCTIONLIKE 테이블을 조회했을 경우 값이 존재할 때
            audao.auLikeDelete(al);
            audao.auLikeDown(alItem);
            return "Y";
        } else {                // 가져온 데이터로 AUCTIONLIKE 테이블을 조회했을 경우 값이 존재하지 않을 때
            return "N";
        }
    }
}

