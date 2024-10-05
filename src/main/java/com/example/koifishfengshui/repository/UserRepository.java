package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
//    User findUserByUser(long id);
    public List<User> findByStatus(Status status);
}