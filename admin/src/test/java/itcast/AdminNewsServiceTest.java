package itcast;

import itcast.application.AdminNewsService;
import itcast.domain.news.News;
import itcast.domain.news.enums.NewsStatus;
import itcast.domain.user.User;
import itcast.domain.user.enums.Interest;
import itcast.dto.request.AdminNewsRequest;
import itcast.dto.response.AdminNewsResponse;
import itcast.jwt.repository.UserRepository;
import itcast.repository.AdminRepository;
import itcast.repository.NewsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdminNewsServiceTest {

    @Mock
    private NewsRepository newsRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AdminNewsService adminNewsService;

    @Test
    @DisplayName("뉴스 생성 성공")
    public void SuccessNewsCreate() {
        //given
        Long userId = 1L;
        LocalDateTime fixedTime = LocalDateTime.of(2024, 12, 1, 12, 0);
        LocalDate fixedDate2 = LocalDate.of(2024, 12, 1);
        User user = User.builder()
                .id(1L)
                .kakaoEmail("kakao@kakao.com")
                .build();

        News news = News.builder()
                .title("제목")
                .content("수정본")
                .originalContent("원본")
                .interest(Interest.NEWS)
                .publishedAt(fixedTime)
                .rating(5)
                .link("http://example.com")
                .thumbnail("http://thumbnail.com")
                .status(NewsStatus.SUMMARY)
                .sendAt(fixedDate2)
                .build();
        AdminNewsRequest adminNewsRequest = new AdminNewsRequest(
                "제목",
                "수정본",
                "원본",
                Interest.NEWS,
                fixedTime,
                5,
                "http://example.com",
                "http://thumbnail.com",
                NewsStatus.SUMMARY,
                fixedDate2
                );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(adminRepository.existsByEmail(user.getKakaoEmail())).willReturn(true);
        given(newsRepository.save(any(News.class))).willReturn(news);

        // When
        AdminNewsResponse response = adminNewsService.createNews(userId, adminNewsRequest);

        // Then
        assertEquals("제목", response.title());
        assertEquals(NewsStatus.SUMMARY, response.status());
        verify(newsRepository).save(any(News.class));
    }

    @Test
    @DisplayName("뉴스 조회 성공")
    public void SuccessNewsRetrieve() {
        // Given
        Long userId = 1L;
        LocalDate sendAt = LocalDate.of(2024, 12, 1);
        NewsStatus status = NewsStatus.SUMMARY;
        int page = 0;
        int size = 10;

        User user = User.builder()
                .id(userId)
                .kakaoEmail("admin@kakao.com")
                .build();

        List<AdminNewsResponse> responses = List.of(
                new AdminNewsResponse(
                        1L,
                        "뉴스1",
                        "요약내용1",
                        "원본내용1",
                        Interest.NEWS,
                        LocalDateTime.now(),
                        4,
                        "http://link1.com",
                        "http:thumb1.com",
                        NewsStatus.SUMMARY,
                        LocalDate.of(2024, 12, 1)),
                new AdminNewsResponse(
                        2L,
                        "뉴스2",
                        "요약내용2",
                        "원본내용2",
                        Interest.NEWS,
                        LocalDateTime.now(),
                        3,
                        "http://link2.com",
                        "http:thumb2.com",
                        NewsStatus.SUMMARY,
                        LocalDate.of(2024, 12, 1))
        );

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminNewsResponse> newsPage = new PageImpl<>(responses, pageable, responses.size());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(adminRepository.existsByEmail(user.getKakaoEmail())).willReturn(true);
        given(newsRepository.findNewsByCondition(status, sendAt, pageable)).willReturn(newsPage);

        // When
        Page<AdminNewsResponse> responsePage = adminNewsService.retrieveNews(userId, status, sendAt, page, size);

        // Then
        assertEquals(2, responsePage.getContent().size());
        assertEquals("뉴스1", responsePage.getContent().get(0).title());
        assertEquals("뉴스2", responsePage.getContent().get(1).title());
        assertEquals(page, responsePage.getNumber());
        assertEquals(size, responsePage.getSize());
        verify(newsRepository).findNewsByCondition(status, sendAt, pageable);
    }

    @Test
    @DisplayName("뉴스 수정 성공")
    public void SuccessNewsUpdate() {
        //given
        Long userId = 1L;
        Long newsId = 1L;
        LocalDateTime fixedTime = LocalDateTime.of(2024, 12, 1, 12, 0);
        LocalDate fixedDate2 = LocalDate.of(2024, 12, 1);

        User user = User.builder()
                .id(userId)
                .kakaoEmail("admin@kakao.com")
                .build();

        News news = News.builder()
                .id(1L)
                .title("제목")
                .content("수정본")
                .originalContent("원본")
                .interest(Interest.NEWS)
                .publishedAt(fixedTime)
                .rating(5)
                .link("http://example.com")
                .thumbnail("http://thumbnail.com")
                .status(NewsStatus.SUMMARY)
                .sendAt(fixedDate2)
                .build();
        AdminNewsRequest adminNewsRequest = new AdminNewsRequest(
                "제목2",
                "수정본2",
                "원본2",
                Interest.NEWS,
                fixedTime,
                3,
                "http://example2.com",
                "http://thumbnail2.com",
                NewsStatus.ORIGINAL,
                fixedDate2
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(adminRepository.existsByEmail(user.getKakaoEmail())).willReturn(true);
        given(newsRepository.findById(newsId)).willReturn(Optional.of(news));

        // When
        AdminNewsResponse response = adminNewsService.updateNews(userId, newsId, adminNewsRequest);

        // Then
        assertEquals("제목2", response.title());
        assertEquals(NewsStatus.ORIGINAL, response.status());
    }

    @Test
    @DisplayName("뉴스 삭제 성공")
    public void successDeleteNews() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 12, 1, 12, 0);
        LocalDate sendAt = LocalDate.of(2024, 12, 1);
        // Given
        Long userId = 1L;
        Long newsId = 1L;
        User adminUser = User.builder()
                .id(userId)
                .kakaoEmail("admin@kakao.com")
                .build();
        News news = News.builder()
                .id(1L)
                .title("제목")
                .content("수정본")
                .originalContent("원본")
                .interest(Interest.NEWS)
                .publishedAt(fixedTime)
                .rating(5)
                .link("http://example.com")
                .thumbnail("http://thumbnail.com")
                .status(NewsStatus.SUMMARY)
                .sendAt(sendAt)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        given(adminRepository.existsByEmail(adminUser.getKakaoEmail())).willReturn(true);
        given(newsRepository.findById(newsId)).willReturn(Optional.of(news));

        // When
        AdminNewsResponse response = adminNewsService.deleteNews(userId, newsId);

        // Then
        assertEquals(news.getId(), response.id());
        assertEquals(news.getTitle(), response.title());
        verify(newsRepository).findById(newsId);
        verify(newsRepository).delete(news);
    }
}
