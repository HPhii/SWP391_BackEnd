package com.example.koifishfengshui.controller;


import com.example.koifishfengshui.model.request.BlogRequest;
import com.example.koifishfengshui.model.response.BlogResponse;
import com.example.koifishfengshui.enums.BlogStatus;
import com.example.koifishfengshui.service.BlogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @PostMapping
    public ResponseEntity<BlogResponse> createBlog(@Valid @RequestBody BlogRequest blogRequest, Authentication authentication) {
        BlogResponse newBlog = blogService.createBlog(blogRequest, authentication);
        return ResponseEntity.ok(newBlog);
    }

    @PutMapping("/{blogId}")
    public ResponseEntity<BlogResponse> updateBlog(@PathVariable Long blogId, @Valid @RequestBody BlogRequest blogRequest) {
        BlogResponse updatedBlog = blogService.updateBlog(blogId, blogRequest);
        return ResponseEntity.ok(updatedBlog);
    }

    @PutMapping("/{blogId}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BlogResponse> approveBlog(
            @PathVariable Long blogId,
            @RequestParam BlogStatus status
    ) {
        BlogResponse updatedBlog = blogService.approveBlog(blogId, status);
        return ResponseEntity.ok(updatedBlog);
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<String> deleteBlog(@PathVariable Long blogId) {
        blogService.deleteBlog(blogId);
        return ResponseEntity.ok("Blog deleted successfully");
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Long blogId) {
        BlogResponse blog = blogService.getBlogById(blogId);
        return ResponseEntity.ok(blog);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<BlogResponse>> getAllBlogsByStatus(@RequestParam BlogStatus blogStatus) {
        List<BlogResponse> blogs = blogService.getAllBlogsByStatus(blogStatus);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogResponse>> searchBlogs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) String tags) {
        List<BlogResponse> blogs = blogService.searchBlogs(title, shortDescription, tags);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BlogResponse>> getBlogsByCategory(@PathVariable Long categoryId) {
        List<BlogResponse> blogs = blogService.getBlogsByCategory(categoryId);
        return ResponseEntity.ok(blogs);
    }
}
