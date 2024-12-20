package itcast.news.application;

import itcast.domain.news.News;
import itcast.domain.newsHistory.NewsHistory;
import itcast.domain.user.User;
import itcast.domain.user.enums.Interest;
import itcast.jwt.repository.UserRepository;
import itcast.mail.application.MailService;
import itcast.news.repository.NewsHistoryRepository;
import itcast.news.repository.NewsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SelectNewsServiceTest {
    private static final int YESTERDAY = 1;
    private static final int ALARM_HOUR = 2;
    private static final int ALARM_DAY = 2;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NewsRepository newsRepository;
    @Mock
    private NewsHistoryRepository newsHistoryRepository;

    @InjectMocks
    private SendNewsService sendNewsService;

    @Mock
    private MailService mailService;

    @Test
    @DisplayName("retrieveUserEmails 메소드 테스트")
    public void retrieveUserEmailsTest() {
        // give
        Interest validInterest = Interest.NEWS;

        User user1 = User.builder()
                .email("test1@example.com")
                .interest(Interest.NEWS)
                .build();
        User user2 = User.builder()
                .email("test2@example.com")
                .interest(Interest.NEWS)
                .build();

        List<User> mockUsers = List.of(user1, user2);
        when(userRepository.findAllByInterest(validInterest)).thenReturn(mockUsers);

        // when
        List<String> result = sendNewsService.retrieveUserEmails(validInterest);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("test1@example.com"));
        assertTrue(result.contains("test2@example.com"));
        verify(userRepository, times(1)).findAllByInterest(validInterest);
    }

    @Test
    @DisplayName("selectNews 메소드 테스트")
    public void selectNewsTest() {
        // give
        LocalDate yesterday = LocalDate.now().minusDays(YESTERDAY);
        LocalDateTime expectedSendAt = LocalDateTime.now().plusDays(ALARM_DAY).plusHours(ALARM_HOUR);

        News mockNews1 = mock(News.class);
        News mockNews2 = mock(News.class);
        List<News> mockNewsList = List.of(mockNews1, mockNews2);

        when(newsRepository.findRatingTop3ByCreatedAt(yesterday)).thenReturn(mockNewsList);

        // when
        sendNewsService.selectNews();

        // then
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        verify(mockNews1, times(1)).newsUpdate(captor.capture());
        verify(mockNews2, times(1)).newsUpdate(captor.capture());

    }

    @Test
    @DisplayName("createNewsHistory 메소드 테스트")
    public void createNewsHistoryTest() {
        // give
        News news1 = News.builder()
                .id(1L)
                .title("Test News 1")
                .content("Test Content 1")
                .sendAt(LocalDate.now())
                .build();

        News news2 = News.builder()
                .id(2L)
                .title("Test News 2")
                .content("Test Content 2")
                .sendAt(LocalDate.now())
                .build();

        User user1 = User.builder().id(1L).email("test1@example.com").build();
        User user2 = User.builder().id(2L).email("test2@example.com").build();

        List<News> sendNews = List.of(news1, news2);
        List<User> users = List.of(user1, user2);
        when(userRepository.findAllByInterest(Interest.NEWS)).thenReturn(users);

        // when
        sendNewsService.createNewsHistory(sendNews);

        // then
        ArgumentCaptor<List<NewsHistory>> captor = ArgumentCaptor.forClass(List.class);
        verify(newsHistoryRepository, times(1)).saveAll(captor.capture());

        List<NewsHistory> savedNewsHistories = captor.getValue();

        assertEquals(4, savedNewsHistories.size());
        assertTrue(savedNewsHistories.stream()
                .anyMatch(nh -> nh.getUser().equals(user1) && nh.getNews().equals(news1)));
        assertTrue(savedNewsHistories.stream()
                .anyMatch(nh -> nh.getUser().equals(user1) && nh.getNews().equals(news2)));
        assertTrue(savedNewsHistories.stream()
                .anyMatch(nh -> nh.getUser().equals(user2) && nh.getNews().equals(news1)));
        assertTrue(savedNewsHistories.stream()
                .anyMatch(nh -> nh.getUser().equals(user2) && nh.getNews().equals(news2)));

    }


}
