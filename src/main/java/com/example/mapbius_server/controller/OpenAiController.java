package com.example.mapbius_server.controller;

import com.example.mapbius_server.service.OpenAiService;
import com.example.mapbius_server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OpenAiController {

    @Autowired
    private OpenAiService openAiService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @PostMapping("/api/public/chatgpt")
    public String getChatResponse(@RequestBody Map<String, String> payload) {
        String userQuery = payload.get("query");
        return openAiService.getChatResponse(userQuery);
    }
}
