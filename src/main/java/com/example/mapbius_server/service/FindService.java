package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.Email;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.FindMapper;
import com.example.mapbius_server.mapper.UserMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindService {
    private final VerificationCodeService verificationCodeService;
    private final FindMapper findMapper;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public FindService(VerificationCodeService verificationCodeService, VerificationCodeService verificationCodeService1, FindMapper findMapper, EmailService emailService) {
        this.verificationCodeService = verificationCodeService1;
        this.findMapper = findMapper;
        this.emailService = emailService;
    }

    public String findEmail(String email) {
        if(findMapper.selectFindEmail(email).equals(email)) {
            return findId(email);
        }
        return "";
    }


    public String findId(String email){
        return findMapper.selectUserId(email);
    }





    // 이메일로 계정 존재 여부
    public boolean existEmail(User user) throws MessagingException {

        if (findMapper.selectExistEmail(user) > 0){

            String code = verificationCodeService.generateCode(user.getEmail());
            Email email = new Email();
            email.setTo(user.getEmail());
            email.setSubject("Mapbius 비밀번호 찾기 요청입니다.");


            // HTML 콘텐츠 생성

            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<body style=\"font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f9f9f9;\">" +
                    "    <div style=\"max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border: 1px solid #dddddd; border-radius: 8px;\">" +
                    "        <div style=\"font-size: 24px; font-weight: bold; color: #333333; text-align: center; margin-bottom: 20px;\">" +
                    "            Mapbius 비밀번호 찾기</div>" +
                    "        <div style=\"font-size: 16px; color: #555555; line-height: 1.5; margin-bottom: 20px;\">" +
                    "            안녕하세요, Mapbius 사용자님!<br>" +
                    "            아래 인증 코드를 입력하셔서 비밀번호를 재설정해주세요.<br><br>" +
                    "            인증 코드는 3분간 유효합니다. 3분 후에는 새로운 인증 코드를 요청해야 합니다.<br>" +
                    "        </div>" +
                    "        <div style=\"font-size: 20px; font-weight: bold; color: #ff6600; text-align: center; margin: 20px 0;\">" +
                    "            인증 코드: " + code + "</div>" +
                    "        <div style=\"font-size: 16px; color: #555555; line-height: 1.5; margin-bottom: 20px;\">" +
                    "            인증 코드 사용 후에는 다른 사람이 사용할 수 없도록 즉시 삭제됩니다.<br>" +
                    "            비밀번호 찾기를 요청하지 않으셨다면 이 이메일을 무시하셔도 됩니다." +
                    "        </div>" +
                    "        <div style=\"font-size: 14px; color: #888888; text-align: center; margin-top: 20px;\">" +
                    "            감사합니다.<br>Mapbius 팀 드림</div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";


            email.setText(htmlContent);
            emailService.sendHtmlMail(email);
            return true;
        } else {
            return false;
        }
    }



}
