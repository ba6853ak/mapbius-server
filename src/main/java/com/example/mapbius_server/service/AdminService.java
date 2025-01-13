package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.GrantRoleRequest;
import com.example.mapbius_server.dto.GrantStateRequest;
import com.example.mapbius_server.mapper.AdminMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;

    // 프로필 이미지 기본 URL (예시)
    private static final String PROFILE_IMAGE_BASE_URL = "http://58.74.46.219:61061/uploads/profiles/";

    /**
     * 전체 사용자 정보를 조회하고 프로필 이미지를 처리합니다.
     *
     * @return 사용자 정보 리스트
     */
    public List<Map<String, Object>> getAllUsersWithProcessedImages() {
        // 매퍼를 통해 사용자 정보 가져오기
        List<Map<String, Object>> users = adminMapper.getAllUsers();

        // 프로필 이미지 처리
        return manageUserDisplayAndSearch(users);
    }

    /**
     * 사용자 리스트의 프로필 이미지를 처리합니다.
     *
     * @param users 사용자 정보 리스트
     * @return 프로필 이미지가 처리된 사용자 리스트
     */
    private List<Map<String, Object>> manageUserDisplayAndSearch(List<Map<String, Object>> users) {
        for (Map<String, Object> user : users) {
            // 프로필 이미지 필드를 확인
            Object profileImage = user.get("profile_image");

            if (profileImage == null || profileImage.toString().isEmpty()) {
                // 프로필 이미지가 없으면 빈 문자열 설정
                user.put("profile_image", "");
            } else {
                // 프로필 이미지가 있으면 기본 URL과 결합
                String fullProfileImageUrl = PROFILE_IMAGE_BASE_URL + profileImage.toString();
                user.put("profile_image", fullProfileImageUrl);
            }
        }
        return users;
    }







    public boolean grantStateToUser(GrantStateRequest gStateReq) {

        String getId = gStateReq.getId(); // 권한을 부여할 사용자의 아이디
        // String getState = gStateReq.getState(); // 부여할 권한 -> 일반:activate / 관리자: deactivate


            if(adminMapper.updateActivateAccount(getId) > 0){
                return true;
            } return false;


    }


    public boolean grantRoleToUser(GrantRoleRequest gRoleReq) {

        String getId = gRoleReq.getId(); // 권한을 부여할 사용자의 아이디
        // String getRole = gRoleReq.getRole(); // 부여할 권한 -> 일반:normal / 관리자: admin

        if (adminMapper.existsAdminUser(getId)) {
            adminMapper.deleteAdminUser(getId);
            return true;
        } else {
            adminMapper.insertAdminUser(getId);
            return true;
        }


    }

}
