package com.example.koifishfengshui.model.response.paged;

import com.example.koifishfengshui.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedUserResponse {
    private List<User> users;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
