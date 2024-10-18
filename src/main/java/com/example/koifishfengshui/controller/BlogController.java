package com.example.koifishfengshui.controller;


import com.example.koifishfengshui.enums.BlogStatus;
import com.example.koifishfengshui.model.request.BlogRequest;
import com.example.koifishfengshui.model.response.dto.BlogResponse;
import com.example.koifishfengshui.model.response.paged.PagedBlogResponse;
import com.example.koifishfengshui.service.BlogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class BlogController {

    @Autowired
    private BlogService blogService;

    // Create a new blog post
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogResponse> createBlog(@Valid BlogRequest blogRequest, Authentication authentication) {
        BlogResponse newBlog = blogService.createBlog(blogRequest, authentication);
        return new ResponseEntity<>(newBlog, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{blogId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogResponse> updateBlog(@PathVariable Long blogId, @Valid BlogRequest blogRequest) {
        BlogResponse updatedBlog = blogService.updateBlog(blogId, blogRequest);
        return ResponseEntity.ok(updatedBlog);
    }

    // Admin approves or rejects a blog post
    @PutMapping("/{blogId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BlogResponse> approveBlog(@PathVariable Long blogId, @RequestParam BlogStatus status) {
        BlogResponse updatedBlog = blogService.approveBlog(blogId, status);
        return ResponseEntity.ok(updatedBlog);
    }

    // Delete a blog post
    @DeleteMapping("/{blogId}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long blogId) {
        blogService.deleteBlog(blogId);
        return ResponseEntity.noContent().build();
    }

    // Get a blog post by its ID
    @GetMapping("/{blogId}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Long blogId) {
        BlogResponse blog = blogService.getBlogById(blogId);
        return ResponseEntity.ok(blog);
    }

    // Get all approved blog posts
    @GetMapping
    public ResponseEntity<PagedBlogResponse> getAllBlogsByStatus(
            @RequestParam(required = false) BlogStatus blogStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedBlogResponse response = blogService.getAllBlogsByStatus(blogStatus, pageable);
        return ResponseEntity.ok(response);
    }

    // Search blogs by title, description, or tags
    @GetMapping("/search")
    public ResponseEntity<PagedBlogResponse> searchBlogs(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedBlogResponse response = blogService.searchBlogs(search, pageable);
        return ResponseEntity.ok(response);
    }
    
    // Get blogs by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PagedBlogResponse> getBlogsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedBlogResponse response = blogService.getBlogsByCategory(categoryId, pageable);
        return ResponseEntity.ok(response);
    }
}
