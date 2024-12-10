package itcast.repository;

import itcast.domain.news.News;
import itcast.domain.news.enums.NewsStatus;
import itcast.dto.response.AdminNewsResponse;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT n FROM News n WHERE n.status = :status ORDER BY n.sendAt DESC")
    Page<AdminNewsResponse> findAllByStatusOrderBySendAtDesc(@Param("status") NewsStatus status, Pageable pageable);
}
