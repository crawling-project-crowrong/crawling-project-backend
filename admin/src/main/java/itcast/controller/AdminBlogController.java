package itcast.controller;

import itcast.ResponseTemplate;
import itcast.application.AdminBlogService;
import itcast.domain.blog.enums.BlogStatus;
import itcast.domain.user.enums.Interest;
import itcast.dto.request.AdminBlogRequest;
import itcast.dto.response.AdminBlogResponse;
import itcast.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blogs")
public class AdminBlogController {

    private final AdminBlogService adminService;
    private final AdminBlogService adminBlogService;

    @PostMapping
    public ResponseTemplate<AdminBlogResponse> createBlog(
            @RequestParam Long userId,
            @RequestBody AdminBlogRequest adminBlogRequest
    ) {
        AdminBlogResponse response = adminService.createBlog(userId, AdminBlogRequest.toEntity(adminBlogRequest));
        return new ResponseTemplate<>(HttpStatus.CREATED, "관리자 블로그 생성 성공", response);
    }

    @GetMapping
    public ResponseTemplate<PageResponse<AdminBlogResponse>> retrieveBlog(
            @RequestParam Long userId,
            @RequestParam(required = false) BlogStatus blogStatus,
            @RequestParam(required = false) Interest interest,
            @RequestParam(required = false) LocalDate sendAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminBlogResponse> blogPage = adminBlogService.retrieveBlog(userId, blogStatus,interest, sendAt, page, size);
        PageResponse<AdminBlogResponse> blogPageResponse = new PageResponse<>(
                blogPage.getContent(),
                blogPage.getNumber(),
                blogPage.getSize(),
                blogPage.getTotalPages()
        );
        return new ResponseTemplate<>(HttpStatus.OK, "관리자 블로그 조회 성공", blogPageResponse);
    }
}
