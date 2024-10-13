package com.example.koifishfengshui.model.response.dto;

import com.example.koifishfengshui.model.entity.Account;
import lombok.Data;

@Data
public class EmailDetails {
    Account receiver;
    String subject;
    String link;
}