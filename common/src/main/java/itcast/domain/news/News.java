package itcast.domain.news;

import itcast.domain.BaseEntity;
import itcast.domain.news.enums.NewsStatus;
import itcast.domain.user.enums.Interest;
import itcast.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Lob;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalContent;

    @Enumerated(EnumType.STRING)
    private Interest interest;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    private Integer rating;

    @Column(nullable = false)
    private String link;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    private NewsStatus status;

    private LocalDate sendAt;

    @Builder
    public News(
            Long id,
            String title,
            String content,
            String originalContent,
            Interest interest,
            LocalDateTime publishedAt,
            Integer rating,
            String link,
            String thumbnail,
            NewsStatus status,
            LocalDate sendAt
    ) {
        this.id = id;
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
            String thumbnail,
            Interest interest,
            NewsStatus status,
            LocalDateTime publishedAt) {
        this.title = title;
        this.originalContent = originalContent;
        this.link = link;
        this.interest = interest;
        this.status = status;
        this.thumbnail = thumbnail;
        this.publishedAt = publishedAt;
    }

    public void update(
            String title,
            String content,
            String originalContent,
            Interest interest,
            LocalDateTime publishedAt,
            Integer rating,
            String link,
            String thumbnail,
            NewsStatus status,
            LocalDate sendAt
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

    public void newsUpdate(LocalDate sendAt) {
        this.sendAt = sendAt;
    }

    public void applySummaryUpdate(
            final String content,
            final Interest interest,
            final Integer rating,
            final NewsStatus status
    ) {
        this.content = content;
        this.interest = interest;
        this.rating = rating;
        this.status = status;
    }
}
