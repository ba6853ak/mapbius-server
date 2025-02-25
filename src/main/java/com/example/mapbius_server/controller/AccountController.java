package com.example.mapbius_server.controller;


import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.Favorite;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.PIResponse;
import com.example.mapbius_server.mapper.AccountMapper;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.service.AccountService;
import com.example.mapbius_server.service.KakaoAuthService;
import com.example.mapbius_server.service.LoginService;
import com.example.mapbius_server.service.UserService;
import com.example.mapbius_server.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource; // 리소스 파일 처리


@RestController
@RequiredArgsConstructor
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginService loginService;
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;

    // 즐겨찾기 체크
    @PostMapping("/api/private/account/favorite/check")
    public ResponseEntity<ResponseData> faviriteCheck(@RequestHeader("Authorization") String header, @RequestBody Favorite fav) {

        ResponseData responseData = new ResponseData();

        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출

        fav.setUserId(userId); // 토큰에서 획득한 아이디를 fav 인스턴스에 저장함.

 /*       if(!(fav.getType().equals("지역") || fav.getType().equals("장소"))){
            responseData.setCode(404);
            responseData.setMessage("지역과 장소만 type으로 설정하세요.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }*/

        int result = accountMapper.checkIfFavoriteExists(fav);
        if (result > 0) {
            responseData.setCode(200);
            responseData.setMessage("즐겨찾기가 등록되어 있음.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(201);
            responseData.setMessage("즐겨찾기가 등록되어 있지 않음.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(201).body(responseData);
        }

    }


    // 즐겨찾기 등록 및 해제
    @PostMapping("/api/private/account/favorite/enroll")
    public ResponseEntity<ResponseData> faviriteEnroll(@RequestHeader("Authorization") String header, @RequestBody Favorite fav) {

        ResponseData responseData = new ResponseData();

        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출

        fav.setUserId(userId); // 토큰에서 획득한 아이디를 fav 인스턴스에 저장함.

        if(!(fav.getType().equals("지역") || fav.getType().equals("장소"))){
            responseData.setCode(404);
            responseData.setMessage("지역과 장소만 type으로 설정하세요.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }

        int result = accountService.addAndRemoveFavorite(fav);
        if (result == 3) {
            responseData.setCode(200);
            responseData.setMessage("즐겨찾기가 등록 되었습니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(200).body(responseData);
        } else if(result == 1) {
            responseData.setCode(201);
            responseData.setMessage("즐겨찾기가 해제 되었습니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(201).body(responseData);
        } else if (result == 2 || result == 4) {
            responseData.setCode(202);
            responseData.setMessage("잘못된 요청입니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(202).body(responseData);
        } else {
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // 즐겨찾기 리스트
    @PostMapping("/api/private/account/favorite/mylist")
    public ResponseEntity<ResponseData> faviriteMyList(@RequestHeader("Authorization") String header) {

        ResponseData responseData = new ResponseData();

        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출

        List<Favorite> result = accountService.getFavorites(userId);
        if (result != null) {
            responseData.setCode(200);
            responseData.setMessage("사용자의 개인 즐겨찾기 리스트를 반환합니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            responseData.setObjData(result);
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("즐겨찾기 리스트 요청 실패!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }

    }





    // 기존 일반 사용자 카카오 계정 연결
    @PostMapping("/api/private/account/kakao/connect")
    public ResponseEntity<ResponseData> kakaoConnect(@RequestHeader("Authorization") String header, @RequestBody Map<String, String> request) {
        ResponseData responseData = new ResponseData();
        String code = request.get("code");

        boolean state = accountService.kakaoAccountConnect(header, code);
        if (state) {
            responseData.setCode(200);
            responseData.setMessage("Kakao Login API에 통합되었습니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("Kakao Login API에 통합 실패! - 파라미터, 유효성 검사를 확인하세요.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // 회원 정보 수정
    @PostMapping("/api/private/account/update")
    public ResponseEntity<?> accountUpdate(@RequestHeader("Authorization") String header, @RequestBody User user) {
        ResponseData responseData = new ResponseData();
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출
        String loginType = (String) claims.get("login_type"); // 로그인 유형
        user.setId(userId); // 토큰에서 추출한 아이디를 user 객체에 삽입.

/*        // 수정 정보 유효성 검사
        if(!userService.isValidPw(user.getPw()) && !userService.isValidEmail(user.getEmail()) && !userService.isValidNickName(user.getNickName())){
            responseData.setCode(404);
            responseData.setMessage("입력하신 정보는 유효성 검사에 위배됩니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }*/

        // 카카오 계정 정보 변경 시 유효성 검사
        if(loginType.equals("kakao")){
            if(!user.getPw().isEmpty() || !user.getPw().equals("") || !user.getEmail().isEmpty() || !user.getEmail().equals("") ){
                responseData.setCode(404);
                responseData.setMessage("수정할 수 없는 정보가 포함되어 있습니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                return ResponseEntity.status(404).body(responseData);
            }
        }


        if(accountService.updateInfo(user)){
            responseData.setCode(200);
            responseData.setMessage("개인정보 수정 성공!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("개인정보 수정 실패!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // (카카오 및 일반 사용자) 비밀번호 확인 후(카카오 사용자는 미확인) 회원정보 반환
    @PostMapping("/api/private/account/confirm")
    public ResponseEntity<?> accountConfirm(@RequestHeader("Authorization") String header, @RequestBody(required = false) User user) {
        ResponseData responseData = new ResponseData();
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출
        User getUserObj = loginService.getUserInfo(userId);

        if(user == null || user.getPw() == null || user.getPw().isEmpty()){ // 만약 카카오 사용자 일경우 PW을 받지 않음.
            logger.info("실행!");
            if(getUserObj.getKakaoId() == null || getUserObj.getKakaoId().isEmpty()){
                responseData.setCode(404);
                responseData.setMessage("카카오 계정 사용자가 아니므로 정보를 불러올 수 없습니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                return ResponseEntity.status(404).body(responseData);
            }
            logger.info(getUserObj.getId() + "님의 정보를 반환되었습니다.");
            responseData.setCode(200);
            responseData.setMessage("카카오 계정 사용자의 회원정보를 불러옵니다.");
            responseData.setObjData(getUserObj);
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(200).body(responseData);
        } else { // 만약 일반 사용자 일경우 PW을 받음
            if(accountService.confirmPw(header, user.getPw())){
                responseData.setCode(200);
                responseData.setMessage("사용자의 비밀번호가 일치하며 회원정보를 반환합니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                responseData.setObjData(getUserObj);
                return ResponseEntity.status(200).body(responseData);
            } else {
                responseData.setCode(404);
                responseData.setMessage("사용자의 비밀번호가 일치하지 않습니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                return ResponseEntity.status(404).body(responseData);
            }
        }
    }

    // (카카오 사용자) 비밀번호 확인 없이 회원정보 반환
    @PostMapping("/api/private/account/kakao/get-info")
    public ResponseEntity<?> NonAccountConfirmAndGetInfo(@RequestHeader("Authorization") String header) {
        ResponseData responseData = new ResponseData();
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출
        User getUserObj = loginService.getUserInfo(userId);
        if(getUserObj.getKakaoId() == null || getUserObj.getKakaoId().isEmpty()){

            responseData.setCode(404);
            responseData.setMessage("카카오 계정 사용자가 아니므로 정보를 불러올 수 없습니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }
        logger.info(getUserObj.getId() + "님의 정보를 반환되었습니다.");
        responseData.setCode(200);
        responseData.setMessage("카카오 계정 사용자의 회원정보를 불러옵니다.");
        responseData.setObjData(getUserObj);
        responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.status(200).body(responseData);

    }

    // 계정 삭제
    @PostMapping("/api/private/account/delete")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String header) {
        ResponseData responseData = new ResponseData();

        if(accountService.deleteAccount(header)){
            responseData.setCode(200);
            responseData.setMessage("계정 삭제 성공!");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("이미 삭제되었거나 정상적인 요청이 아닙니다.");
            return ResponseEntity.status(404).body(responseData);
        }

    }


    // 프로필 이미지 업로드
    @PostMapping("/api/private/account/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(
            @RequestHeader("Authorization") String header, @RequestPart("file") MultipartFile file // 파일 파트
    ) {
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출

        try {
            // 예: 파일과 함께 userId를 이용한 로직 처리
            accountService.saveProfileImage(userId, file);

            return ResponseEntity.ok("파일 업로드 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 실패: " + e.getMessage());
        }
    }


    /**
     * ResponseEntity는 HTTP 응답을 캡슐화 하는 클래스
     * Resource는 반환할 리소스 (파일, 이미지) 등을 나타냄.
     */

    // 프로필 이미지 반출
    @Value("${upload.path}") // 업로드 경로 값을 저장할 변
    String uploadPath;
    @PostMapping("/api/private/account/getProfileImage")
    public ResponseEntity<?> getProfileImage(@RequestHeader("Authorization") String header,
                                             HttpServletRequest request) {
        String token = header.substring(7).trim();
        Claims claims = jwtTokenProvider.validateToken(token);
        String userId = (String) claims.get("sub");

        // 닉네임 조회
        String userNm = userMapper.selectUserNickNameById(userId);
        if (userNm == null || userNm.isEmpty()) {
            logger.error("사용자 닉네임을 찾을 수 없습니다: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 닉네임을 찾을 수 없습니다.");
        }

        // 프로필 이미지 파일명 조회
        String fileName = accountMapper.findProfileImageByUserId(userId);
        String fileUrl = null;

        // 업로드 경로 및 파일 URL 구성
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
                    fileUrl = baseUrl + "/uploads/profiles/" + fileName;
                    logger.info("파일 URL 생성 성공: " + fileUrl);
                } else {
                    logger.warn("프로필 이미지 파일이 존재하지 않습니다: " + filePath.toString());
                }
            } catch (Exception e) {
                logger.error("프로필 이미지 파일 처리 중 오류 발생", e);
                // 이미지 URL은 null로 유지
            }
        } else {
            logger.info("프로필 이미지 파일이 설정되지 않음.");
        }

        // 응답 객체 구성
        PIResponse response = new PIResponse();
        response.setFileUrl(fileUrl); // 이미지가 없으면 null 반환
        response.setUserNm(userNm);  // 닉네임은 항상 반환

        logger.info("닉네임 및 파일 URL 반환: " + userNm + " / " + (fileUrl != null ? fileUrl : "이미지 없음"));
        return ResponseEntity.ok(response);
    }



    }


/*    @PostMapping("/api/private/account/getProfileImage")
    public ResponseEntity<?> getProfileImage(@RequestHeader("Authorization") String header) {
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출

        logger.info("프로필 이미지 반출 동작");
        System.out.println(userId);
        try {
            // 해당 userId에 대한 프로필 이미지를 가져온다.
            Resource imageResource = accountService.getProfileImage(userId);
            String encodedFilename = URLEncoder.encode(imageResource.getFilename(), StandardCharsets.UTF_8)
                    .replace("+", "%20"); // 공백 처리

            // HTTP 응답 생성 및 리소스 반환
            return ResponseEntity.ok()

                    .contentType(MediaType.IMAGE_JPEG) // Content-Type을 이미지로 설정
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFilename) // 브라우저에서 바로 표시
                    .body(imageResource);

        } catch (Exception e) {
            // 이미지가 없거나 에러 발생 시 404 응답 반환
            return ResponseEntity.notFound().build();
        }
    }*/







