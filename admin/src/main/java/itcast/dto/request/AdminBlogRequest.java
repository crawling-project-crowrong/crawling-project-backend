package itcast.dto.request;

import itcast.domain.blog.Blog;
import itcast.domain.blog.enums.BlogStatus;
import itcast.domain.blog.enums.Platform;
import itcast.domain.user.enums.Interest;

import java.time.LocalDate;

public record AdminBlogRequest(
        Platform platform,
        String title,
        String content,
        String originalContent,
        Interest interest,
        LocalDate publishedAt,
        int rating,
        String link,
        String thumbnail,
        BlogStatus status,
        LocalDate sendAt
) {
    public static Blog toEntity(AdminBlogRequest adminBlogRequest) {
        return Blog.adminBuilder()
                .platform(adminBlogRequest.platform())
                .title(adminBlogRequest.title())
                .content(adminBlogRequest.content())
                .originalContent(adminBlogRequest.originalContent())
                .interest(adminBlogRequest.interest())
                .publishedAt(adminBlogRequest.publishedAt())
                .rating(adminBlogRequest.rating())
                .link(adminBlogRequest.link())
                .thumbnail(adminBlogRequest.thumbnail())
                .status(adminBlogRequest.status())
                .sendAt(adminBlogRequest.sendAt())
                .build();
    }
}
