package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.enums.BlogStatus;
import com.example.koifishfengshui.model.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findByStatus(BlogStatus status, Pageable pageable);

    Page<Blog> findByCategory_CategoryId(Long categoryId, Pageable pageable);

    //    @Query("SELECT b FROM Blog b " +
//            "WHERE (:search IS NULL OR b.title LIKE %:search% " +
//            "OR b.shortDescription LIKE %:search% " +
//            "OR :search MEMBER OF b.tags)")
//    Page<Blog> searchBlogs(@Param("search") String search, Pageable pageable);
    @Query("SELECT b FROM Blog b WHERE (:search IS NULL OR b.title LIKE CONCAT('%', :search, '%') " +
            "OR b.shortDescription LIKE CONCAT('%', :search, '%') " +
            "OR :search MEMBER OF b.tags)")
    Page<Blog> searchBlogs(@Param("search") String search, Pageable pageable);

}


