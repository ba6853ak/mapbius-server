package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.Email;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    // 메모리 내 인증 코드 저장소
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();
    private final Map<String, Long> expirationStore = new ConcurrentHashMap<>();
    private static final long CODE_TTL = 3 * 60 * 1000; // 인증 코드 유효 시간 (3분, 밀리초)

    // 인증 코드 생성
    public String generateCode(String email) {
        // 1) 랜덤 6자리 숫자 생성
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 2) 메모리 저장소에 저장
        codeStore.put(email, code);
        expirationStore.put(email, System.currentTimeMillis() + CODE_TTL);

        // 3분 후 코드 삭제 스케줄링
        scheduleCodeInvalidation(email);

        return code;
    }

    // 인증 코드 검증
    public boolean validateCode(Email email) {
        // 유효 시간 체크
        Long expirationTime = expirationStore.get(email.getTo());
        if (expirationTime == null || expirationTime < System.currentTimeMillis()) {
            return false; // 코드 만료
        }

        boolean valid = codeStore.get(email.getTo()).equals(email.getCode());
        if(valid){
            invalidateCode(email.getTo()); // 인증 성공 시 인증코드 삭제
            return valid;
        } else {
            return valid;
        }
    }

    // 인증 코드 삭제
    public void invalidateCode(String email) {
        codeStore.remove(email);
        expirationStore.remove(email);
    }

    // 3분 후 인증 코드 삭제 스케줄링
    private void scheduleCodeInvalidation(String email) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> invalidateCode(email), CODE_TTL, TimeUnit.MILLISECONDS);
    }
}
