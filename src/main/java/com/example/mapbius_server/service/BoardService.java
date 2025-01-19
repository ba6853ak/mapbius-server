package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.Board;
import com.example.mapbius_server.domain.Review;
import com.example.mapbius_server.domain.TravelRoute;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.BoardMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Value("${upload.cover.path}")
    String uploadCoverPath;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final BoardMapper boardMapper;
    public BoardService(BoardMapper boardMapper) { this.boardMapper = boardMapper; }

    // 가게 ID로 평균 별점과 후기 개수를 가져오는 메서드
    public Map<String, Object> getStoreReviewStats(String storeId) {
        Map<String, Object> stats = boardMapper.getStoreReviewStats(storeId);

        if (stats != null && stats.get("avgRating") != null) {
            // avgRating 값을 소수점 첫째 자리까지만 표시
            Double avgRating = ((BigDecimal) stats.get("avgRating")).doubleValue();
            Double roundedAvgRating = Math.round(avgRating * 10) / 10.0;

            // 수정된 avgRating 값을 Map에 다시 설정
            stats.put("avgRating", roundedAvgRating);
        }

        return stats;
    }

    // 리뷰 저장 (텍스트 및 이미지 파일 저장)
    public boolean saveReview(@RequestBody Review rv) throws IOException {


        try {
            // DB에 여행 경로 데이터 삽입 ********************************************************************************************************************
            boardMapper.insertReview(rv);
            return true;

        } catch (IOException e) {
            logger.error("IOException occurred during file saving for userId={}:", rv.getUserId(), e);
            throw e;

        } catch (RuntimeException e) {
            logger.error("RuntimeException occurred during DB update for userId={}:", rv.getUserId(), e);
            throw e;
        }
    }

// 리뷰 전체 목록 가져오기
    public List<Review> getAllReviews(HttpServletRequest request) {
        // 데이터베이스에서 Map 형태로 리뷰 목록 가져오기
        List<Map<String, Object>> mapData = boardMapper.getReviews();

        // Map 데이터를 Review 객체로 변환
        List<Review> data = mapData.stream().map(map -> {
            Review review = new Review();
            review.setReviewId((Integer) map.get("review_id")); // 리뷰 ID
            review.setUserId((String) map.get("user_id")); // 사용자 ID
            review.setPhoneNumber((String) map.get("phone_number")); // 전화번호

            Random random = new Random();
            // 0부터 10까지의 난수 생성
            int randomNumber = random.nextInt(11); // 11은 upper bound로 포함되지 않음


            review.setHeartCount(randomNumber); // 좋아요 개수

            // review_date 처리: LocalDateTime -> String 변환
            Object reviewDateObj = map.get("review_date");
            if (reviewDateObj instanceof LocalDateTime) {
                LocalDateTime reviewDate = (LocalDateTime) reviewDateObj;
                review.setReviewDate(reviewDate.toString());
            } else if (reviewDateObj instanceof String) {
                review.setReviewDate((String) reviewDateObj);
            }

            review.setContent((String) map.get("content")); // 리뷰 내용

            Object ratingObj = map.get("rating");
            if (ratingObj instanceof BigDecimal) {
                review.setRating(((BigDecimal) ratingObj).doubleValue());
            } else if (ratingObj instanceof Double) {
                review.setRating((Double) ratingObj);
            }

            review.setCoverImage((String) map.get("cover_image")); // 커버 이미지 파일 이름

            if(review.getCoverImage() == null) {
                review.setCoverImage("");
            }

            // 사용자 이름 조회 및 설정
            String userId = review.getUserId();
            String userNm = boardMapper.selectUserIdToUserNm(userId); // 사용자 이름 조회
            review.setUserNm(userNm);

            return review;
        }).collect(Collectors.toList());

        // 업로드 경로 및 파일 URL 구성
        String uploadPath = "upload/cover_image";

        for (Review rv : data) {
            String fileName = rv.getCoverImage();

            if (fileName != null && !fileName.isEmpty()) {
                String rootDir = System.getProperty("user.dir");
                String fileDir = rootDir + File.separator + uploadPath;

                try {
                    Path filePath = Paths.get(fileDir).resolve(fileName).normalize();
                    Resource resource = new UrlResource(filePath.toUri());

                    if (resource.exists()) {
                        String baseUrl = String.format("%s://%s:%d",
                                request.getScheme(),
                                request.getServerName(),
                                request.getServerPort()
                        );
                        String fileUrl = baseUrl + "/uploads/cover_images/" + fileName;
                        rv.setCoverImage(fileUrl);
                    } else {
                        logger.warn("커버 이미지 파일이 존재하지 않습니다: " + filePath.toString());
                    }
                } catch (Exception e) {
                    logger.error("커버 이미지 파일 처리 중 오류 발생", e);
                }
            }
        }

        return data;
    }


    // 리뷰 일부 목록 가져오기
    public List<Review> getSelectReviews(String phoneNumber, HttpServletRequest request) {


        List<Map<String, Object>> mapData = boardMapper.getSelectReviews(request, phoneNumber);         // 데이터베이스에서 Map 형태로 리뷰 목록 가져오기

        // Map 데이터를 Review 객체로 변환
        List<Review> data = mapData.stream().map(map -> {
            Review review = new Review();
            review.setReviewId((Integer) map.get("review_id")); // 리뷰 ID
            review.setUserId((String) map.get("user_id")); // 사용자 ID
            review.setPhoneNumber((String) map.get("phone_number")); // 전화번호

            Random random = new Random();
            // 0부터 10까지의 난수 생성
            int randomNumber = random.nextInt(11); // 11은 upper bound로 포함되지 않음


            review.setHeartCount(randomNumber); // 좋아요 개수

            // review_date 처리: LocalDateTime -> String 변환
            Object reviewDateObj = map.get("review_date");
            if (reviewDateObj instanceof LocalDateTime) {
                LocalDateTime reviewDate = (LocalDateTime) reviewDateObj;
                review.setReviewDate(reviewDate.toString());
            } else if (reviewDateObj instanceof String) {
                review.setReviewDate((String) reviewDateObj);
            }

            review.setContent((String) map.get("content")); // 리뷰 내용

            Object ratingObj = map.get("rating");
            if (ratingObj instanceof BigDecimal) {
                review.setRating(((BigDecimal) ratingObj).doubleValue());
            } else if (ratingObj instanceof Double) {
                review.setRating((Double) ratingObj);
            }

            review.setCoverImage((String) map.get("cover_image")); // 커버 이미지 파일 이름

            if(review.getCoverImage() == null) {
                review.setCoverImage("");
            }

            // 사용자 이름 조회 및 설정
            String userId = review.getUserId();
            String userNm = boardMapper.selectUserIdToUserNm(userId); // 사용자 이름 조회
            review.setUserNm(userNm);

            return review;
        }).collect(Collectors.toList());

        // 업로드 경로 및 파일 URL 구성
        String uploadPath = "upload/cover_image";

        for (Review rv : data) {
            String fileName = rv.getCoverImage();

            if (fileName != null && !fileName.isEmpty()) {
                String rootDir = System.getProperty("user.dir");
                String fileDir = rootDir + File.separator + uploadPath;

                try {
                    Path filePath = Paths.get(fileDir).resolve(fileName).normalize();
                    Resource resource = new UrlResource(filePath.toUri());

                    if (resource.exists()) {
                        String baseUrl = String.format("%s://%s:%d",
                                request.getScheme(),
                                request.getServerName(),
                                request.getServerPort()
                        );
                        String fileUrl = baseUrl + "/uploads/cover_images/" + fileName;
                        rv.setCoverImage(fileUrl);
                    } else {
                        logger.warn("커버 이미지 파일이 존재하지 않습니다: " + filePath.toString());
                    }
                } catch (Exception e) {
                    logger.error("커버 이미지 파일 처리 중 오류 발생", e);
                }
            }
        }

        return data;
    }









/*    // 전화번호에 대한 모든 후기 조회
    public List<Review> getReviews() {
        return boardMapper.getReviews();
    }*/



// 여행 루트 수정
    public boolean updateTravelRoute(TravelRoute tr) {
        MultipartFile file = tr.getImageFile();  // form-data에서 넘어온 파일
        String fileName = null;

        // 1. DB에서 기존 TravelRoute 정보 조회
        TravelRoute existingImageRoute = boardMapper.findTravelRouteById(tr.getId());
        if(tr.getId() != null){

            if (existingImageRoute == null) {
                throw new RuntimeException("존재하지 않는 여행 경로입니다. ID: " + tr.getId());
            }
        }


        // 2. 새 파일이 있는지 확인
        if (file != null && !file.isEmpty()) {
            try {
                // 파일명 생성
                String originalFileName = file.getOriginalFilename();
                fileName = UUID.randomUUID().toString() + "_" + originalFileName;

                // 파일 경로 생성 및 저장
                String rootPath = System.getProperty("user.dir");
                String filePath = rootPath + File.separator + uploadCoverPath + File.separator + fileName;

                File destinationFile = new File(filePath);
                destinationFile.getParentFile().mkdirs();
                file.transferTo(destinationFile);

                logger.info("File saved successfully: {}", destinationFile.getAbsolutePath());

                // 새 파일 이름 설정
                tr.setCoverImageName(fileName);

            } catch (IOException | java.io.IOException e) {
                logger.error("File upload failed: {}", e.getMessage());
                throw new RuntimeException("File upload failed", e);
            }
        } else {
            logger.info("No new file provided. Keeping existing file data for travel route ID: {}", tr.getId());

            // 3. 기존 이미지를 그대로 사용
            //    별도로 coverImageName이 null이 되지 않도록, 기존 값을 그대로 세팅
            tr.setCoverImageName(existingImageRoute.getCoverImageName());
        }

        // 4. DB 업데이트 호출
        //    제목, 내용, 위치 등 다른 필드는 컨트롤러/서비스에서 받은 값이 덮어씌워지겠지만
        //    이미지 이름은 위에서 새로 설정되었거나 기존 값이 세팅되었으므로 그대로 업데이트 됨
        try {
            int data = boardMapper.updateTravelRoute(tr);
            return data > 0;
        } catch (RuntimeException e) {
            logger.error("Database update failed: {}", e.getMessage());
            throw e;
        }
    }



    // 여행 루트 삭제
    public boolean deleteTravelRoute(Long id) {
        if (boardMapper.deleteTravelRoute(id) > 0) {
            return true;
        } else {
            return false;
        }
    }

    // 여행 루트 목록 가져오기
    public List<TravelRoute> getAllTravelRoutes(HttpServletRequest request) {

        // 데이터베이스에서 여행 루트 목록 가져오기
        List<TravelRoute> data = boardMapper.getTravelRoutes();

        // 업로드 경로 및 파일 URL 구성
        String uploadPath = "upload/cover_image";

        for (TravelRoute tr : data) {
            String fileName = tr.getCoverImageName();

            // coverImageName이 null 또는 비어있지 않은 경우에만 처리
            if (fileName != null && !fileName.isEmpty()) {
                String rootDir = System.getProperty("user.dir");
                String fileDir = rootDir + File.separator + uploadPath;

                try {
                    // 파일 경로 생성
                    Path filePath = Paths.get(fileDir).resolve(fileName).normalize();
                    Resource resource = new UrlResource(filePath.toUri());

                    if (resource.exists()) {
                        // 서버 URL 생성
                        String baseUrl = String.format("%s://%s:%d",
                                request.getScheme(),
                                request.getServerName(),
                                request.getServerPort()
                        );
                        // URL 생성 및 업데이트
                        String fileUrl = baseUrl + "/uploads/cover_images/" + fileName;
                        tr.setCoverImageName(fileUrl);
                    } else {
                        logger.warn("커버 이미지 파일이 존재하지 않습니다: " + filePath.toString());
                    }
                } catch (Exception e) {
                    logger.error("커버 이미지 파일 처리 중 오류 발생", e);
                    // coverImageName은 원래 값 유지
                }
            }
        }

        return data;
    }


    // 특정 여행 루트 가져오기
    public TravelRoute getTravelRouteById(Long id) {
        return boardMapper.getTravelRouteById(id);
    }

    // 여행 경로 저장 (텍스트 및 이미지 파일 저장)

    public boolean saveCoverImageAndTravelRoute(@ModelAttribute TravelRoute tr) throws IOException {

/*        // 사용자 조회
        User user = accountMapper.findByUserId(id);
        if (user == null) {
            logger.error("No user found with id={}. Throwing exception.", id);
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        logger.debug("Found user: {}", user);*/



        MultipartFile file = tr.getImageFile();
        System.out.println(file);


        // 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // 프로젝트 루트 디렉터리 확인 및 경로 생성
        String rootPath = System.getProperty("user.dir"); //
        String filePath = rootPath + File.separator + uploadCoverPath + File.separator + fileName;

        tr.setCoverImageName(fileName); // 백엔드에 저장될 파일 이름 인스턴스에 삽입


        try {
            // 프로젝트 내 이미지 파일 저장
            File destinationFile = new File(filePath);
            destinationFile.getParentFile().mkdirs(); // 디렉터리 생성
            file.transferTo(destinationFile);
            logger.info("File saved successfully: {}", destinationFile.getAbsolutePath());

            // DB에 여행 경로 데이터 삽입 ********************************************************************************************************************
            boardMapper.insertTravelRoute(tr);
            logger.info("DB update successful for userId={}, saved fileName={}", tr.getCreatorId(), fileName);

            return true;

        } catch (IOException e) {
            logger.error("IOException occurred during file saving for userId={}:", tr.getCreatorId(), e);
            throw e;

        } catch (RuntimeException e) {
            logger.error("RuntimeException occurred during DB update for userId={}:", tr.getCreatorId(), e);
            throw e;
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }





    // 공지사항 등록
    public boolean noticeEnroll(Board board) {

        if ((board.getBoardTitle() == null || board.getBoardTitle().trim().isEmpty()) ||
                (board.getBoardContent() == null || board.getBoardContent().trim().isEmpty())) {
            return false;
        }

        if(boardMapper.insertNotice(board)){
            return true;
        }
        else {
            return false;
        }
    }

    // 공지사항 삭제
    public boolean noticeDelete(int noticeIdx) {
        if(boardMapper.deleteNotice(noticeIdx) > 0){
            System.out.println("공지사항 삭제 성공!");
            return true;
        }
        else {
            System.out.println("공지사항 삭제 실패!");
            return false;
        }
    }

    // 공지사항 수정
    public boolean noticeUpdate(Board board) {
        if (boardMapper.updateNotice(board) > 0) {
            System.out.println("공지사항 수정 성공!");
            return true;
        } else {
            System.out.println("공지사항 수정 실패!");
            return false;

        }

    }

    // 공지사항 상세보기
    public Board noticeDetail(int noticeIdx) {
        Board boardResult = boardMapper.selectNoticeDetail(noticeIdx);

        if (boardResult != null) {
            String boardAuthor = boardResult.getBoardAuthor();
            if (boardAuthor != null) {
                String nickName = boardMapper.selectUserIdToUserNm(boardAuthor);
                boardResult.setNickName(nickName);
            }
            return boardResult;
        } else {
            return null;
        }
    }




    // 공지사항 목록 불러오기
    public Map<String, Object> getNotices(int curpage, int size, String keyword, String type) {
        int offset = curpage * size; // 현재 페이지의 시작 위치 계산

        // 데이터 조회
        List<Map<String, Object>> items;
        if (keyword != null && !keyword.isEmpty()) {
            // 검색 조건이 있을 때
            if ("title".equalsIgnoreCase(type)) {
                items = boardMapper.selectNoticesByTitle(keyword, size, offset);
            } else if ("content".equalsIgnoreCase(type)) {
                items = boardMapper.selectNoticesByContent(keyword, size, offset);
            } else {
                items = boardMapper.selectNoticesByTitleOrContent(keyword, size, offset);
            }
        } else {
            // 검색 조건이 없을 때 전체 공지사항 조회
            items = boardMapper.selectNotices(size, offset);
        }

        // 닉네임 추가
        for (Map<String, Object> item : items) {
            String author = (String) item.get("author");
            String nickname = boardMapper.selectUserIdToUserNm(author);
            item.put("nickname", nickname);
        }

        // 총 게시글 수 조회
        int totalNotices = (keyword != null && !keyword.isEmpty()) ?
                boardMapper.selectNoticeCountByKeyword(keyword, type) :
                boardMapper.selectNoticeCount();

        System.out.println("키워드 : " + keyword);
        System.out.println("타입 : " + type);
        System.out.println("전체 수: " + totalNotices);
        System.out.println("페이지 당 게시글 수: " + size);
        // 전체 페이지 수 계산
        int maxpage = (int) Math.ceil((double) totalNotices / size);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);    // 공지사항 데이터 목록
        response.put("maxpage", maxpage); // 전체 페이지 수

        return response;
    }





    // userId로 nick_name 가져오기
    public String getNicknameById(String userId){
        return boardMapper.selectUserIdToUserNm(userId);
    }








/*    // 공지사항 목록 불러오기
    public Map<String, Object> getNotices(int curpage, int size) {
        // 현재 페이지의 시작 위치 계산
        int offset = curpage * size;

        // 데이터 조회
        List<Object> items = boardMapper.selectNotices(size, offset);

        // 전체 페이지 수 계산
        int totalNotices = boardMapper.selectNoticeCount();
        int maxpage = (int) Math.ceil((double) totalNotices / size);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);    // 현재 페이지의 데이터 목록
        response.put("maxpage", maxpage); // 전체 페이지 수

        System.out.println("Size: " + size + " / Offset: " + offset);

        return response;
    }*/




}


/*
    // 공지사항 목록 불러오기
    public List<Board> selectNoticeList() {




    }*/






