package itcast.application;

import itcast.domain.blog.Blog;
import itcast.domain.blog.enums.BlogStatus;
import itcast.domain.news.News;
import itcast.domain.news.enums.NewsStatus;
import itcast.domain.user.enums.Interest;
import itcast.dto.request.AdminBlogRequest;
import itcast.dto.request.AdminNewsRequest;
import itcast.dto.request.BlogMapper;
import itcast.dto.request.NewsMapper;
import itcast.dto.response.AdminBlogResponse;
import itcast.dto.response.AdminNewsResponse;
import itcast.repository.AdminRepository;
import itcast.repository.BlogRepository;
import itcast.repository.NewsRepository;
import itcast.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final NewsRepository newsRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public AdminNewsResponse createNews(Long userId, AdminNewsRequest adminNewsRequest) {
        if(!identifyAdmin(userId)) {
            throw new IllegalArgumentException("접근할 수 없는 유저입니다.");
        }

        News news = NewsMapper.toEntity(adminNewsRequest);
        News savedNews = newsRepository.save(news);

        return new AdminNewsResponse(savedNews);
    }

    public AdminBlogResponse createBlog(Long userId, AdminBlogRequest adminBlogRequest) {
        if(!identifyAdmin(userId)) {
            throw new IllegalArgumentException("접근할 수 없는 유저입니다.");
        }

        Blog blog = BlogMapper.toEntity(adminBlogRequest);
        Blog savedBlogs = blogRepository.save(blog);

        return new AdminBlogResponse(savedBlogs);
    }

    public Page<AdminNewsResponse> retrieveNews(Long userId, NewsStatus status, int page, int size) {
        if(!identifyAdmin(userId)) {
            throw new IllegalArgumentException("접근할 수 없는 유저입니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        return newsRepository.findAllByStatusOrderBySendAtDesc(status, pageable);
    }

    public Page<AdminBlogResponse> retrieveBlog(Long userId, BlogStatus status, Interest interest, int page, int size) {
        if(!identifyAdmin(userId)) {
            throw new IllegalArgumentException("접근할 수 없는 유저입니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        return blogRepository.findAllByStatusAndInterestOrderBySendAtDesc(status, interest, pageable);
    }

    private boolean identifyAdmin(Long id){
        String email = userRepository.findById(id).get().getKakaoEmail();
        return adminRepository.existsByEmail(email);
    }
}
