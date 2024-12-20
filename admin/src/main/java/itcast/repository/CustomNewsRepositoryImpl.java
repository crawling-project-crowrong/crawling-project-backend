package itcast.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import itcast.domain.news.QNews;
import itcast.domain.news.enums.NewsStatus;
import itcast.dto.response.AdminNewsResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static itcast.domain.news.QNews.news;

@Repository
@RequiredArgsConstructor
public class CustomNewsRepositoryImpl implements CustomNewsRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminNewsResponse> findNewsByCondition(NewsStatus status, LocalDate sendAt, Pageable pageable) {
        QNews news = QNews.news;

        JPQLQuery<AdminNewsResponse> query = queryFactory
                .select(Projections.constructor(AdminNewsResponse.class,
                        news.id,
                        news.title,
                        news.content,
                        news.originalContent,
                        news.interest,
                        news.publishedAt,
                        news.rating,
                        news.link,
                        news.thumbnail,
                        news.status,
                        news.sendAt
                ))
                .from(news)
                .where(statusEq(status), sendAtEq(sendAt))
                .orderBy(news.sendAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<AdminNewsResponse> content = query.fetch();

        JPQLQuery<Long> countQuery = queryFactory
                .select(news.count())
                .from(news)
                .where(statusEq(status), sendAtEq(sendAt));

        return new PageImpl<>(content, pageable, countQuery.fetchOne());
    }

    private BooleanExpression statusEq(NewsStatus status) {
        if (status == null) {
            return null;
        }
        return news.status.eq(status);
    }

    private BooleanExpression sendAtEq(LocalDate sendAt) {
        if (sendAt == null) {
            return null;
        }

        LocalDate endAt = sendAt.plusDays(1);

        return news.sendAt.between(sendAt, endAt);
    }
}
