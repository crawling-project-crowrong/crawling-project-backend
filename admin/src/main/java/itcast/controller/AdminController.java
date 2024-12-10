package itcast.controller;

import itcast.application.AdminService;
import itcast.domain.blog.enums.BlogStatus;
import itcast.domain.news.enums.NewsStatus;
import itcast.domain.user.enums.Interest;
import itcast.dto.request.AdminBlogRequest;
import itcast.dto.request.AdminNewsRequest;
import itcast.dto.response.AdminBlogResponse;
import itcast.dto.response.AdminNewsResponse;
import itcast.dto.response.PageResponse;
import itcast.dto.response.ResponseBodyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/news")
    public ResponseEntity<ResponseBodyDto<AdminNewsResponse>> createNews(@RequestParam Long userId, @RequestBody AdminNewsRequest adminNewsRequest) {
        return new ResponseEntity<>(
                ResponseBodyDto.success("관리자 뉴스 생성 성공",
                        adminService.createNews(userId, adminNewsRequest)), HttpStatus.CREATED);
    }

    @PostMapping("/blogs")
    public ResponseEntity<ResponseBodyDto<AdminBlogResponse>> crawlBlogs(@RequestParam Long userId, @RequestBody AdminBlogRequest adminBlogRequest) {
        return new ResponseEntity<>(
                ResponseBodyDto.success("관리자 블로그 생성 성공",
                        adminService.createBlog(userId, adminBlogRequest)), HttpStatus.CREATED);
    }

    @GetMapping("/news")
    public ResponseEntity<ResponseBodyDto<PageResponse<AdminNewsResponse>>> retrieveNews(
            @RequestParam Long userId,
            @RequestParam NewsStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminNewsResponse> newsPage = adminService.retrieveNews(userId, status, page, size);
        PageResponse<AdminNewsResponse> newPageResponse = new PageResponse<>(
                newsPage.getContent(),
                newsPage.getNumber(),
                newsPage.getSize(),
                newsPage.getTotalPages()
        );
        return new ResponseEntity<>(
                ResponseBodyDto.success("관리자 뉴스 조회 성공", newPageResponse), HttpStatus.OK);
    }

    @GetMapping("/blogs")
    public ResponseEntity<ResponseBodyDto<PageResponse<AdminBlogResponse>>> retrieveBlogs(
            @RequestParam Long userId,
            @RequestParam BlogStatus status,
            @RequestParam Interest interest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminBlogResponse> blogPage = adminService.retrieveBlog(userId, status, interest, page, size);
        PageResponse<AdminBlogResponse> blogPageResponse = new PageResponse<>(
                blogPage.getContent(),
                blogPage.getNumber(),
                blogPage.getSize(),
                blogPage.getTotalPages()
        );
        return new ResponseEntity<>(
                ResponseBodyDto.success("관리자 뉴스 조회 성공", blogPageResponse), HttpStatus.OK);
    }
}
