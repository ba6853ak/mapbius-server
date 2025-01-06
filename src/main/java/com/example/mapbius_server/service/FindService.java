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
    private final UserService userService;

    @Autowired
    public FindService(VerificationCodeService verificationCodeService, VerificationCodeService verificationCodeService1, FindMapper findMapper, EmailService emailService, UserService userService) {
        this.verificationCodeService = verificationCodeService1;
        this.findMapper = findMapper;
        this.emailService = emailService;
        this.userService = userService;
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


    public boolean validatePwResetCode(Email email) {
        if(verificationCodeService.validateCode(email)){
            return true;
        } else {
            return false;
        }
    }

    public boolean sendPwEmail(String to, String ramdomPwd) throws MessagingException {

            Email email = new Email();
            // HTML 콘텐츠 생성

            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<body style=\"font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f9f9f9;\">" +
                    "    <div style=\"max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border: 1px solid #dddddd; border-radius: 8px;\">" +
                    "        <div style=\"font-size: 24px; font-weight: bold; color: #333333; text-align: center; margin-bottom: 20px;\">" +
                    "            Mapbius 임시 비밀번호 발급</div>" +
                    "        <div style=\"font-size: 16px; color: #555555; line-height: 1.5; margin-bottom: 20px;\">" +
                    "            안녕하세요, Mapbius 사용자님!<br>" +
                    "            요청하신 내용에 따라 임시 비밀번호를 발급해드립니다.<br>" +
                    "        </div>" +
                    "        <div style=\"font-size: 20px; font-weight: bold; color: #ff6600; text-align: center; margin: 20px 0;\">" +
                    "            임시 비밀번호: " + ramdomPwd + "</div>" +
                    "        <div style=\"font-size: 16px; color: #555555; line-height: 1.5; margin-bottom: 20px;\">" +
                    "            임시 비밀번호를 사용하여 로그인하신 후, 반드시 새로운 비밀번호로 변경해주세요.<br>" +
                    "        </div>" +
                    "        <div style=\"font-size: 14px; color: #888888; text-align: center; margin-top: 20px;\">" +
                    "            감사합니다.<br>Mapbius 팀 드림</div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";
            email.setText(htmlContent);
            email.setTo(to);
            email.setSubject("Mapbius 임시 비밀번호를 발급해드립니다.");
            userService.mailToPwdUpdate(to, ramdomPwd); // 비밀번호 변경
            emailService.sendHtmlMail(email);
            return true;

        }





    // 이메일로 계정 존재 여부
    public boolean existEmail(User user)  {

        if (findMapper.selectExistEmail(user) > 0){
            return true;
        } else {
            return false;
        }
    }












}
