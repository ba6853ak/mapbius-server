package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.GrantRoleRequest;
import com.example.mapbius_server.dto.GrantStateRequest;
import com.example.mapbius_server.mapper.AdminMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;

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
