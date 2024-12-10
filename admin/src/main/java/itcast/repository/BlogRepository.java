package itcast.repository;

import itcast.domain.blog.Blog;
import itcast.domain.blog.enums.BlogStatus;
import itcast.domain.user.enums.Interest;
import itcast.dto.response.AdminBlogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    @Query("SELECT b FROM Blog b WHERE b.status = :status and b.interest = :interest ORDER BY b.sendAt DESC")
    Page<AdminBlogResponse> findAllByStatusAndInterestOrderBySendAtDesc(BlogStatus status, Interest interest, Pageable pageable);
}
