package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.Blog;
import com.example.koifishfengshui.enums.BlogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Tìm tất cả các blog có trạng thái đã duyệt
    List<Blog> findByStatus(BlogStatus status);

    // Tìm blog theo categoryId
    List<Blog> findByCategory_CategoryId(Long categoryId);

    // Tìm blog dựa theo title, shortDescription hoặc tags
    @Query("SELECT b FROM Blog b " +
            "WHERE (:title IS NULL OR b.title LIKE %:title%) " +
            "AND (:shortDescription IS NULL OR b.shortDescription LIKE %:shortDescription%) " +
            "AND (:tags IS NULL OR :tags MEMBER OF b.tags)")
    List<Blog> searchBlogs(@Param("title") String title,
                           @Param("shortDescription") String shortDescription,
                           @Param("tags") String tags);

}


