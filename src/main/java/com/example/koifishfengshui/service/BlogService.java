package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.BlogStatus;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.model.entity.Blog;
import com.example.koifishfengshui.model.entity.Category;
import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.model.request.BlogRequest;
import com.example.koifishfengshui.model.response.BlogResponse;
import com.example.koifishfengshui.repository.BlogRepository;
import com.example.koifishfengshui.repository.CategoryRepository;
import com.example.koifishfengshui.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final String ADMIN = "ADMIN";

    @Transactional
    public BlogResponse createBlog(BlogRequest blogRequest, Authentication authentication) {
        Account authorAccount = (Account) authentication.getPrincipal();

        User author = authorAccount.getUser();

        Blog blog = modelMapper.map(blogRequest, Blog.class);
        blog.setAuthor(author);

        blog.setStatus(ADMIN.equals(authorAccount.getRole()) ? BlogStatus.APPROVED : BlogStatus.PENDING);
        blog.setCreatedAt(LocalDateTime.now());

        Category category = categoryService.findOrCreateCategory(blogRequest.getCategoryName());
        blog.setCategory(category);

        Blog newBlog = blogRepository.save(blog);

        return mapToBlogResponse(newBlog);
    }



    @Transactional
    public BlogResponse updateBlog(Long blogId, BlogRequest blogRequest) {
        Blog existingBlog = blogRepository.findById(blogId)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found"));

        if (blogRequest.getTitle() != null) existingBlog.setTitle(blogRequest.getTitle());
        if (blogRequest.getThumbnail() != null) existingBlog.setThumbnail(blogRequest.getThumbnail());
        if (blogRequest.getContent() != null) existingBlog.setContent(blogRequest.getContent());
        if (blogRequest.getShortDescription() != null) existingBlog.setShortDescription(blogRequest.getShortDescription());
        if (blogRequest.getImageUrl() != null) existingBlog.setImageUrl(blogRequest.getImageUrl());
        if (blogRequest.getTags() != null) existingBlog.setTags(blogRequest.getTags());
        if (blogRequest.getCategoryName() != null) existingBlog.getCategory().setCategoryName(blogRequest.getCategoryName());

        Blog updatedBlog = blogRepository.save(existingBlog);
        return mapToBlogResponse(updatedBlog);
    }

    @Transactional
    public BlogResponse approveBlog(Long blogId, BlogStatus status) {
        Blog existingBlog = blogRepository.findById(blogId)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found"));
        existingBlog.setStatus(status);

        Blog approvedBlog = blogRepository.save(existingBlog);
        return mapToBlogResponse(approvedBlog);
    }

    @Transactional
    public void deleteBlog(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found"));
        blogRepository.delete(blog);
    }

    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found"));
        return mapToBlogResponse(blog);
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> getAllBlogsByStatus(BlogStatus blogStatus) {
        return blogRepository.findByStatus(blogStatus)
                .stream()
                .map(this::mapToBlogResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> searchBlogs(String title, String shortDescription, String tags) {
        return blogRepository.searchBlogs(title, shortDescription, tags)
                .stream()
                .map(this::mapToBlogResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> getBlogsByCategory(Long categoryId) {
        return blogRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(this::mapToBlogResponse)
                .collect(Collectors.toList());
    }

    private BlogResponse mapToBlogResponse(Blog blog) {
        BlogResponse response = modelMapper.map(blog, BlogResponse.class);
        response.setAuthorName(blog.getAuthor().getFullName());
        return response;
    }
}
