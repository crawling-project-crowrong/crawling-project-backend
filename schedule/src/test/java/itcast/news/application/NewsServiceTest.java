package itcast.news.application;

import itcast.ai.application.GPTService;
import itcast.domain.news.News;
import itcast.news.repository.NewsRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsServiceTest {

    @InjectMocks
    private NewsService newsService;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private GPTService gptService;

    @Test
    @DisplayName("크롤링 테스트")
    void SuccessCrawling() throws IOException {
        // give
        String url = "https://news.naver.com/breakingnews/section/105/283";
        Document document = Jsoup.connect(url).get();

        // when
        Elements articles = document.select(".sa_thumb_inner");

        // then
        assertFalse(articles.isEmpty(), "링크가 없음");
    }

    @Test
    @DisplayName("중복 된 메소드 체크 테스트")
    void isValidLinksTest() {
        // give
        List<String> existingLinks = Arrays.asList(
                "https://example.com/1",
                "https://example.com/2",
                "https://example.com/3");
        when(newsRepository.findAllLinks()).thenReturn(existingLinks);

        List<String> linksToCheck = Arrays.asList(
                "https://example.com/1",  // 존재하는 링크
                "https://example.com/4",  // 새로운 링크
                "https://example.com/5"   // 새로운 링크
        );

        // when
        List<String> validLinks = newsService.isValidLinks(linksToCheck);

        // then
        assertEquals(2, validLinks.size(), "Valid links 크기가 2이 아닙니다");
        assertTrue(validLinks.contains("https://example.com/4"),
                "Valid links에 'https://example.com/4'가 포함되지 않았습니다");
        assertTrue(validLinks.contains("https://example.com/5"),
                "Valid links should contain 'https://example.com/5'가 포함되지 않았습니다");
    }

    @Test
    @DisplayName("convertDateTime 메서드 테스트")
    void convertDateTimeTest() {
        // give
        String testPmDate = "입력2020.01.01. 오후 01:01";
        String testAmDate = "입력2020.01.01. 오전 01:01";
        String testNoonDate = "입력2020.01.01. 오후 12:01";
        String testMidnighDate = "입력2020.01.01. 오전 12:01";

        // when
        LocalDateTime convertDatePm = newsService.convertDateTime(testPmDate);
        LocalDateTime convertDateAm = newsService.convertDateTime(testAmDate);
        LocalDateTime convertDateNoon = newsService.convertDateTime(testNoonDate);
        LocalDateTime convertDateMidnight = newsService.convertDateTime(testMidnighDate);

        // then
        assertTrue(convertDatePm.isEqual(LocalDateTime.of(2020, 01, 01, 13, 01)),
                "오후 시간이 올바르지 않습니다.");
        assertTrue(convertDateAm.isEqual(LocalDateTime.of(2020, 01, 01, 01, 01)),
                "오전 시간이 올바르지 않습니다.");
        assertTrue(convertDateNoon.isEqual(LocalDateTime.of(2020, 01, 01, 12, 01)),
                "오후 12시 시간이 올바르지 않습니다.");
        assertTrue(convertDateMidnight.isEqual(LocalDateTime.of(2020, 1, 1, 0, 1)),
                "오전 12시 시간이 올바르지 않습니다.");
    }

    @Test
    @DisplayName("cleanContent 메소드 테스트")
    void cleanContent() {
        // give
        String testWord = "[아니] (이건) 안녕";

        // when
        String cleanWord = newsService.cleanContent(testWord);

        // then
        assertTrue(cleanWord.equals("안녕"), "문장이 올바르지 않습니다");
    }

    @Test
    @DisplayName("오레된 데이터 삭제 메소드 테스트")
    void testDeleteOldNews() {
        // When
        newsService.deleteOldData();

        // Then
        // newsRepository.deleteOldNews 호출 여부 검증
        verify(newsRepository,times(1)).deleteOldNews();
    }

    @Test
    @DisplayName("링크를 찾아 저장하는 메소드 테스트")
    void findLinksTest() throws IOException {
        // give
        String url = "http://example.com";
        Document document = mock(Document.class);
        Elements articles = new Elements();

        Element mockElement1 = mock(Element.class);
        Elements elements1 = mock(Elements.class);
        when(mockElement1.select("a")).thenReturn(elements1);
        when(elements1.attr("href")).thenReturn("http://example.com/link1");

        Element mockElement2 = mock(Element.class);
        Elements elements2 = mock(Elements.class);
        when(mockElement2.select("a")).thenReturn(elements2);
        when(elements2.attr("href")).thenReturn("http://example.com/link2");

        articles.add(mockElement1);
        articles.add(mockElement2);
        when(document.select(".sa_thumb_inner")).thenReturn(articles);

        Connection connection = mock(Connection.class);
        when(connection.get()).thenReturn(document);

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(url)).thenReturn(connection);

            // when
            List<String> links = newsService.findLinks(url);

            // then
            assertNotNull(links);
            assertEquals(2, links.size());
            assertEquals("http://example.com/link1", links.get(0));
            assertEquals("http://example.com/link2", links.get(1));
        }
    }

}
