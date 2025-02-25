package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.Board;
import com.example.mapbius_server.domain.Review;
import com.example.mapbius_server.domain.TravelRoute;
import com.example.mapbius_server.mapper.BoardMapper;
import com.example.mapbius_server.service.BoardService;
import com.example.mapbius_server.service.UserService;
import com.example.mapbius_server.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final BoardService boardService;
    public final JwtUtil jwtUtil;
    private final BoardMapper boardMapper;

    ResponseData responseData;

    // 후기 등록
    @PostMapping("/api/private/reviews/enroll")
    public ResponseEntity<?> travelRouteEnroll(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Review review) {

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String creator_id = jwtUtil.validateToken(token).getSubject(); // 토큰 검증
        review.setUserId(creator_id);

        ResponseData responseData = new ResponseData();
        if (boardService.saveReview(review)) {
            responseData.setCode(200);
            responseData.setMessage("후기 등록 성공");
            System.out.println("후기 등록 성공");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("후기 등록 실패");
            System.out.println("후기 등록 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }


    // 후기 글 좋아요 기능
    @PostMapping("/api/private/reviews/heart")
    public ResponseEntity<?> reviewHeart(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Review review) {

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String creator_id = jwtUtil.validateToken(token).getSubject(); // 토큰 검증
        review.setUserId(creator_id);

        ResponseData responseData = new ResponseData();
        if(boardService.reviewitsmine(creator_id, review.getReviewId())){
            responseData.setCode(200);
            responseData.setMessage("본인이 작성한 후기는 추천을 누를 수 없습니다.");
            System.out.println("본인이 작성한 후기는 추천을 누를 수 없습니다.");
            return ResponseEntity.status(200).body(responseData);
        }

        int state = boardService.reviewHeartAdd(creator_id, review.getReviewId());

        if (state == 1) {
            responseData.setCode(201);
            responseData.setMessage("해당 게시글에 대한 좋아요를 해제했습니다.");
            return ResponseEntity.status(201).body(responseData);
        }
        if (state == 3) {
            responseData.setCode(202);
            responseData.setMessage("해당 게시글에 대해 좋아요를 눌렀습니다.");
            return ResponseEntity.status(202).body(responseData);
        }
        else {
            responseData.setCode(404);
            responseData.setMessage("변경 실패! 매개변수를 확인하세요.");
            System.out.println("좋아요 상태 변경 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }




    // 리뷰 전체 목록 가져오기 //  사용안해
    @PostMapping("/api/public/reviews/entire-list")
    public ResponseEntity<?> reviewEntireList(HttpServletRequest request) {

        ResponseData responseData = new ResponseData();


        List<Review> receivedData = boardService.getAllReviews(request);

        if (receivedData != null) {
            responseData.setCode(200);
            responseData.setMessage("리뷰 전체 리스트 출력");
            responseData.setObjData(receivedData);
            System.out.println("리뷰 전체 출력 성공");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("리뷰 전체  출력 실패");
            System.out.println("리뷰 전체 출력 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // 해당 가게 리뷰 검색 가져오기
    @PostMapping("/api/public/reviews/select-list")
    public ResponseEntity<?> reviewSelectList( HttpServletRequest request, @RequestBody Review review) {

        ResponseData responseData = new ResponseData();


        List<Review> receivedData = boardService.getSelectReviews(review.getPhoneNumber(), request);

        if(review.getPhoneNumber().equals("")){
            receivedData = boardService.getAllReviews(request);
        }



        if (receivedData != null) {
            responseData.setCode(200);
            responseData.setMessage("리뷰 전체 리스트 출력");
            responseData.setObjData(receivedData);
            System.out.println("리뷰 전체 출력 성공");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("리뷰 전체 출력 실패");
            System.out.println("리뷰 전체 출력 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }


    // 해당 가게에 대한 후기 개수와 평균 가져오기
    @PostMapping("/api/public/reviews/get")
    public ResponseEntity<?> reviewGet(@RequestBody Review review) {
        ResponseData responseData = new ResponseData();

        // 가게 전화번호로 후기 개수와 평균 별점 가져오기
        Map<String, Object> stats = boardService.getStoreReviewStats(review.getPhoneNumber());

        if (stats != null && !stats.isEmpty()) {
            responseData.setCode(200);
            responseData.setObjData(stats);
            responseData.setMessage("결과를 출력합니다.");
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode(404); // 데이터가 없을 경우 404로 설정
            responseData.setObjData(null);
            responseData.setMessage("결과가 없습니다.");
            return ResponseEntity.status(404).body(responseData);
        }
    }

    // 여행 경로 전체 목록 가져오기 (모든 글 !)
    @PostMapping("/api/public/travel-route/entire-list")
    public ResponseEntity<?> travelRouteCompleteEntireList( HttpServletRequest request) {

        ResponseData responseData = new ResponseData();



        List<TravelRoute> receivedData = boardService.getCompleteAllTravelRoutes(request);

        if (receivedData != null) {
            responseData.setCode(200);
            responseData.setMessage("여행 경로 리스트 출력");
            responseData.setObjData(receivedData);
            System.out.println("여행 경로 리스트 출력 성공");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("여행 경로 리스트 출력 실패");
            System.out.println("여행 경로 리스트 출력 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }




    // 여행 경로 전체 목록 가져오기 (자기 자신, 본인이 좋아요 누른 글)
    @PostMapping("/api/private/travel-route/entire-list")
    public ResponseEntity<?> travelRouteEntireList(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {

        ResponseData responseData = new ResponseData();

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String creatorId = jwtUtil.validateToken(token).getSubject(); // 토큰 검증

        List<TravelRoute> receivedData = boardService.getAllTravelRoutes(request, creatorId);

        if (receivedData != null) {
            responseData.setCode(200);
            responseData.setMessage("여행 경로 리스트 출력");
            responseData.setObjData(receivedData);
            System.out.println("여행 경로 리스트 출력 성공");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("여행 경로 리스트 출력 실패");
            System.out.println("여행 경로 리스트 출력 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // 여행 경로 상세보기 가져오기
    // 만든 사람 프로필 이미지랑 하트 개수
    @PostMapping("/api/public/travel-route/detail")
    public ResponseEntity<?> travelRouteArticleDatail( @RequestBody TravelRoute tr) {

        ResponseData responseData = new ResponseData();

        Object data = boardService.getTravelRouteById(tr.getId());

        if (data != null) {
            responseData.setCode(200);
            responseData.setMessage("해당 여행 경로 출력");
            responseData.setObjData(data);
            System.out.println("여행 경로 가져오기 성공");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("해당 여행 경로 출력 실패");
            System.out.println("여행 경로 가져오기 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }


    // 여행 경로 등록
    @PostMapping("/api/private/travel-route/enroll")
    public ResponseEntity<?> travelRouteEnroll(@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute TravelRoute tr, @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String creator_id = jwtUtil.validateToken(token).getSubject(); // 토큰 검증
        tr.setCreatorId(creator_id);
        // 파일이 없으면 coverImageName을 null로 설정
        if (tr.getImageFile() == null || tr.getImageFile().isEmpty()) {
            tr.setCoverImageName(null); // 파일이 없으면 coverImageName을 null로 설정
        }


        ResponseData responseData = new ResponseData();
        if (boardService.saveCoverImageAndTravelRoute(tr)) {
            responseData.setCode(200);
            responseData.setMessage("여행 경로 등록 성공");
            System.out.println("여행 경로 등록 성공");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("여행 경로 등록 실패");
            System.out.println("여행 경로 등록 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // 여행 경로 수정
    @PostMapping("/api/private/travel-route/modify")
    public ResponseEntity<?> travelRouteModify(        @RequestHeader("Authorization") String authorizationHeader,
                                                       @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
                                                       @ModelAttribute TravelRoute tr) {

        ResponseData responseData = new ResponseData();

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String creator_id = jwtUtil.validateToken(token).getSubject(); // 토큰 검증
        tr.setCreatorId(creator_id);


        System.out.println("받은 이미지파일 : " + tr.getImageFile());


        if(tr.getTitle().isEmpty() || tr.getContent().isEmpty() || tr.getLocationInfo().isEmpty()) {
            responseData.setCode(401);
            responseData.setMessage("Error: 제목, 내용, 여행 경로가 누락되었습니다.");
            System.out.println("Error: 제목, 내용, 여행 경로가 누락되었습니다.");
            return ResponseEntity.status(401).body(responseData);
        }

        if (boardService.updateTravelRoute(tr)) {
            responseData.setCode(200);
            responseData.setMessage("여행 경로 수정 성공");
            System.out.println("여행 경로 수정 성공");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("여행 경로 수정 실패");
            System.out.println("여행 경로 수정 실패");
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // 여행 경로 삭제
    @PostMapping("/api/private/travel-route/delete")
    public ResponseEntity<?> travelRouteDelete(@RequestHeader("Authorization") String authorizationHeader, @RequestBody TravelRoute tr) {

        logger.info("삭제 요청 여행 경로: " + tr.getId());

        ResponseData responseData = new ResponseData();


        if(tr.getImageFile() != null && !tr.getImageFile().isEmpty()){
            if (boardService.deleteTravelRoute(tr.getId())) {
                responseData.setCode(200);
                responseData.setMessage("여행 경로가 삭제되었습니다.");
                logger.info("여행 경로 삭제 성공");
                return ResponseEntity.status(200).body(responseData);
            } else {
                responseData.setCode(404);
                responseData.setMessage("여행 경로가 존재하지 않습니다.");
                logger.info("여행 경로 삭제 실패");
                return ResponseEntity.status(404).body(responseData);
            }
        }


        return null;
    }





    // 공지사항 목록 반환
    @GetMapping("/api/public/notices/")
    public ResponseEntity<ResponseData> getNotices(
            @RequestParam(defaultValue = "1") int curpage,  // 현재 페이지
            @RequestParam(value = "keyword", required = false) String keyword, // 검색어 (옵션)
            @RequestParam(value = "type", required = false) String type        // 검색 타입 (옵션: title, content)
    ) {
        final int PAGEPERARTICLESIZE = 5;
        ResponseData responseData = new ResponseData(); // ResponseData 초기화
        responseData.setMessage("공지사항 데이터 반환 성공!");
        responseData.setCode(200);
        responseData.setObjData(boardService.getNotices(curpage - 1, PAGEPERARTICLESIZE, keyword, type)); // 1부터 시작 변환
        return ResponseEntity.status(200).body(responseData);
    }



    // 공지사항 게시글 상세보기

    @GetMapping("/api/public/notices/{id}")
    public Object getNoticeById(@PathVariable("id") int id) {
        System.out.println("요청된 id: " + id);
        Board result = boardService.noticeDetail(id);
        ResponseData responseData = new ResponseData(); // ResponseData 초기화
        if(result != null){
            responseData.setCode(200);
            responseData.setObjData(result);
            responseData.setMessage("공지사항 상세보기 반환");
            System.out.println("공지사항 상세보기 반환");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(400);
            System.out.println(result);
            responseData.setObjData(result);
            responseData.setMessage("공지사항 상세보기 반환 실패");
            System.out.println("공지사항 상세보기 반환 실패");
            return ResponseEntity.status(400).body(responseData);
        }
    }




    // 공지사항 게시글 등록
    @PostMapping("/api/private/notice-post")
    public ResponseEntity<?> noticePost(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Board board) {

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String jwtLog = jwtUtil.validateToken(token).getSubject(); // 토큰 검증
        System.out.println("공지사항 동작");
        System.out.println("Extracted Username from JWT: " + jwtLog);

        ResponseData responseData = new ResponseData();
        System.out.println("Board Title: " + board.getBoardTitle());
        System.out.println("Board Content: " + board.getBoardContent());
        System.out.println("Board Author: " + board.getBoardAuthor());
        if (boardService.noticeEnroll(board)) {
            responseData.setCode(201);
            responseData.setMessage("공지사항 등록 성공");
            System.out.println("공지사항 등록 성공");
            return ResponseEntity.status(201).body(responseData);
        } else {
            System.out.println("공지사항 동작");
            System.out.println("Extracted Username from JWT: " + jwtLog);
            responseData.setCode(400);
            responseData.setMessage("공지사항 등록 실패");
            System.out.println("공지사항 등록 실패");
            return ResponseEntity.status(400).body(responseData);
        }

    }


    // 공지사항 게시글 수정
    @PostMapping("/api/private/notice-update")
    public ResponseEntity<?> noticeUpdate(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Board board) {

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String tokenOwner = jwtUtil.validateToken(token).getSubject(); // 토큰 주인이 누구인가?
        System.out.println("공지사항 수정 동작");
        System.out.println("Extracted userId from JWT: " + tokenOwner);

        responseData = new ResponseData();

        if (boardService.noticeUpdate(board)) {
            responseData.setCode(200);
            responseData.setMessage("공지사항 수정 성공!");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(400);
            responseData.setMessage("공지사항 수정 실패!");
            return ResponseEntity.status(400).body(responseData);
        }


    }


    // 공지사항 게시글 삭제
    @PostMapping("/api/private/notice-delete")
    public ResponseEntity<?> noticeDelete(@RequestHeader("Authorization") String authorizationHeader, @RequestBody int noticeIdx) {
        String token = authorizationHeader.replace("Bearer ", "");
        String jwtLog = jwtUtil.validateToken(token).getSubject(); // 토큰 검증
        System.out.println("공지사항 삭제 동작");
        System.out.println("Extracted userId from JWT: " + jwtLog);
        responseData = new ResponseData();

        if(boardService.noticeDelete(noticeIdx)) {
            responseData.setCode(200);
            responseData.setMessage("공지사항 삭제 성공!");
            System.out.println("공지사항 삭제 성공");
            return ResponseEntity.status(200).body(responseData);
        }
        else {
            responseData.setCode(400);
            responseData.setMessage("공지사항 삭제 실패!");
            System.out.println("공지사항 삭제 실패");
            return ResponseEntity.status(400).body(responseData);
        }

    }




}
