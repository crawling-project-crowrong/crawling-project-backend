package itcast.domain.news;

import itcast.domain.BaseEntity;
import itcast.domain.news.enums.NewsStatus;
import itcast.domain.user.enums.Interest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Lob
    @Column(nullable = false,columnDefinition = "TEXT")
    private String originalContent;

    @Enumerated(EnumType.STRING)
    private Interest interest;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    private int rating;

    @Column(nullable = false)
    private String link;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    private NewsStatus status;

    private LocalDateTime sendAt;

    @Builder
    public News(
            String title,
            String content,
            String originalContent,
            Interest interest,
            LocalDateTime publishedAt,
            int rating,
            String link,
            String thumbnail,
            NewsStatus status,
            LocalDateTime sendAt
    ) {
        this.title = title;
        this.content = content;
        this.originalContent = originalContent;
        this.interest = interest;
        this.publishedAt = publishedAt;
        this.rating = rating;
        this.link = link;
        this.thumbnail = thumbnail;
        this.status = status;
        this.sendAt = sendAt;
    }

    @Builder
    public News(
            String title,
            String originalContent,
            String link,
            Interest interest,
            NewsStatus status,
            String thumbnail,
            LocalDateTime publishedAt) {
        this.title = title;
        this.originalContent = originalContent;
        this.link = link;
        this.interest = interest;
        this.status = status;
        this.thumbnail = thumbnail;
        this.publishedAt = publishedAt;
    }
}
