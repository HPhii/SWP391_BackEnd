package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.BlogStatus;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.model.entity.Blog;
import com.example.koifishfengshui.model.entity.Category;
import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.model.request.BlogRequest;
import com.example.koifishfengshui.model.response.dto.BlogResponse;
import com.example.koifishfengshui.model.response.paged.PagedBlogResponse;
import com.example.koifishfengshui.repository.BlogRepository;
import com.example.koifishfengshui.repository.CategoryRepository;
import com.example.koifishfengshui.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private CloudinaryService cloudinaryService;

    private static final String ADMIN = "ADMIN";

    @Transactional
    public BlogResponse createBlog(BlogRequest blogRequest, Authentication authentication) {
        Account authorAccount = (Account) authentication.getPrincipal();

        User author = authorAccount.getUser();

        Blog blog = modelMapper.map(blogRequest, Blog.class);
        blog.setAuthor(author);

        blog.setStatus(ADMIN.equals(authorAccount.getRole()) ? BlogStatus.APPROVED : BlogStatus.PENDING);
        blog.setCreatedAt(LocalDateTime.now());

        if (blogRequest.getThumbnail() != null && !blogRequest.getThumbnail().isEmpty()) {
            String thumbnailUrl = cloudinaryService.uploadImage(blogRequest.getThumbnail());
            blog.setThumbnail(thumbnailUrl);
        }

        if (blogRequest.getImageFile() != null && !blogRequest.getImageFile().isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(blogRequest.getImageFile());
            blog.setImageUrl(imageUrl);
        }

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
        if (blogRequest.getThumbnail() != null && !blogRequest.getThumbnail().isEmpty()) {
            String thumbnailUrl = cloudinaryService.uploadImage(blogRequest.getThumbnail());
            existingBlog.setThumbnail(thumbnailUrl);
        }
        if (blogRequest.getContent() != null) existingBlog.setContent(blogRequest.getContent());
        if (blogRequest.getShortDescription() != null) existingBlog.setShortDescription(blogRequest.getShortDescription());
        if (blogRequest.getImageFile() != null && !blogRequest.getImageFile().isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(blogRequest.getImageFile());
            existingBlog.setImageUrl(imageUrl);
        }
        if (blogRequest.getTags() != null) existingBlog.setTags(blogRequest.getTags());
        if (blogRequest.getCategoryName() != null) {
            Category category = categoryService.findOrCreateCategory(blogRequest.getCategoryName());
            existingBlog.setCategory(category);
        }

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

    @Transactional
    public BlogResponse getBlogById(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found"));
        return mapToBlogResponse(blog);
    }

    @Transactional
    public PagedBlogResponse getAllBlogsByStatus(BlogStatus blogStatus, Pageable pageable) {
        Page<Blog> blogPage = blogRepository.findByStatus(blogStatus, pageable);
        return mapToPagedBlogResponse(blogPage, pageable);
    }


    @Transactional
    public PagedBlogResponse searchBlogs(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            search = search.trim();
        } else {
            search = null;
        }
        Page<Blog> blogPage = blogRepository.searchBlogs(search, pageable);
        return mapToPagedBlogResponse(blogPage, pageable);
    }

    @Transactional
    public PagedBlogResponse getBlogsByCategory(Long categoryId, Pageable pageable) {
        Page<Blog> blogPage = blogRepository.findByCategory_CategoryId(categoryId, pageable);
        return mapToPagedBlogResponse(blogPage, pageable);
    }


    private BlogResponse mapToBlogResponse(Blog blog) {
        BlogResponse response = modelMapper.map(blog, BlogResponse.class);
        response.setAuthorName(blog.getAuthor().getFullName());
        return response;
    }

    private PagedBlogResponse mapToPagedBlogResponse(Page<Blog> blogPage, Pageable pageable) {
        List<BlogResponse> blogResponses = blogPage.getContent()
                .stream()
                .map(this::mapToBlogResponse)
                .collect(Collectors.toList());

        return new PagedBlogResponse(
                blogResponses,
                blogPage.getTotalElements(),
                blogPage.getTotalPages(),
                pageable.getPageNumber()
        );
    }

}
