package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.Board;
import com.example.mapbius_server.domain.Review;
import com.example.mapbius_server.domain.TravelRoute;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    // 게시판에서 유저 아이디로 이미지 셀렉트
    String selectBoardUserProfileImage(@Param("id") String id);

    // 본인의 리뷰인가?
    int selectIsOwnReview( @Param("userId") String userId, @Param("reviewId") int reviewId);


    // 리뷰에 대한 좋아요 개수 조회
    int countLikesByReviewId(@Param("reviewId") int reviewId);

    // 리뷰 좋아요 삽입
    int insertReviewLike( @Param("userId") String userId, @Param("reviewId") int reviewId);

    // 리뷰 좋아요 삭제
    int deleteReviewLike( @Param("userId") String userId, @Param("reviewId") int reviewId);


    // 리뷰 좋아요 삽입 전 중복 체크
    int selectReviewLikeCheck( @Param("userId") String userId, @Param("reviewId") int reviewId);



    // 리뷰 삽입
    int insertReview(Review review);

    // 전체 리뷰 목록 가져오기
    List<Map<String, Object>> getReviews();

    // 해당 리뷰 목록 가져오기
    List<Map<String, Object>> getSelectReviews(HttpServletRequest request, @Param("phoneNumber") String phoneNumber);

    // 가게 ID로 평균 별점과 후기 개수 조회
    Map<String, Object> getStoreReviewStats(String phoneNumber);

    // 1. 여행 루트 삽입
    int insertTravelRoute(TravelRoute travelRoute);

    // 2. 여행 루트 수정
    int updateTravelRoute(TravelRoute travelRoute);

    // 3. 여행 루트 삭제
    int deleteTravelRoute(Long id);

    // 4. 사용자 개인 여행 목록 가져오기
    List<TravelRoute> getTravelRoutes(@Param("creatorId") String creatorId);

    // 5. 여행 루트 개별 가져오기
    TravelRoute getTravelRouteById(Long id);

    // 특정 여행 경로 조회
    TravelRoute findTravelRouteById(Long id);


    // 공지사랑 등록
    boolean insertNotice(Board board);

    // 공지사항 삭제
    int deleteNotice(int id);

    // 공지사항 수정
    int updateNotice(Board board);

   /* // 공지사항 목록 조회 (페이지네이션 포함)
    List<Map<String, Object>> selectNotices(@Param("size") int size, @Param("offset") int offset);*/

    // 공지사항 전체 개수 조회
    int selectNoticeCount();

    // 사용자 아이디 -> 사용자 닉네임 전환
    String selectUserIdToUserNm(String id);

    // 공지사항 게시글 상세보기
    Board selectNoticeDetail(int id);

    // 공지사항 목록 조회 (페이징)
    List<Map<String, Object>> selectNotices(int size, int offset);

    // 제목으로 검색 (페이징)
    List<Map<String, Object>> selectNoticesByTitle(String keyword, int size, int offset);

    // 내용으로 검색 (페이징)
    List<Map<String, Object>> selectNoticesByContent(String keyword, int size, int offset);

    // 제목 또는 내용으로 검색 (페이징)
    List<Map<String, Object>> selectNoticesByTitleOrContent(String keyword, int size, int offset);


    // 검색 조건에 따른 공지사항 수 조회
    int selectNoticeCountByKeyword(String keyword, String type);

    // 공지사항 전체 검색


    // 공지사항 제목으로 검색


    // 공직사항 내용으로 검색




}
