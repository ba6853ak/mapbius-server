package com.example.mapbius_server.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email {

    private String to;
    private String subject;
    private String text;

}
