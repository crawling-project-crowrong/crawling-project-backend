package itcast.news.application;

import itcast.ai.Message;
import itcast.ai.application.GPTService;
import itcast.ai.dto.request.GPTSummaryRequest;
import itcast.domain.news.News;
import itcast.exception.ItCastApplicationException;
import itcast.news.dto.request.CreateNewsRequest;
import itcast.news.repository.NewsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static itcast.exception.ErrorCodes.*;

@Service
@RequiredArgsConstructor
public class NewsService {

    private static final int LINK_SIZE = 10;
    private static final int HOUR = 12;
    private static final int YESTERDAY = 2;
    private static final int ALARM_HOUR = 7;
    private static final int ALARM_DAY = 2;

    @Value("${spring.crawler.naver-it-url}")
    private String url;

    private final NewsRepository newsRepository;
    private final GPTService gptService;

    public void newsCrawling() throws IOException {
        List<String> links = findLinks(url);
        links = isValidLinks(links);

        links.forEach(link -> {
            try {
                Document url = Jsoup.connect(link).get();
                String titles = url.select("#title_area").text();
                String content = url.select("#dic_area").text();
                String date =
                        url.select(".media_end_head_info_datestamp_bunch").text();
                String thumbnail =
                        url.selectFirst("meta[property=og:image]").attr("content");

                titles = cleanContent(titles);
                content = cleanContent(content);
                LocalDateTime publishedAt = convertDateTime(date);

                if (thumbnail.isEmpty()) {
                    throw new ItCastApplicationException(INVALID_NEWS_CONTENT);
                }

                CreateNewsRequest newsRequest = new CreateNewsRequest(titles, content, link, thumbnail, publishedAt);
                News news = newsRepository.save(newsRequest.toEntity(titles, content, link, thumbnail, publishedAt));
                Message message = new Message("user", content);
                GPTSummaryRequest request = new GPTSummaryRequest("gpt-4o-mini", message, 0.7f);
                gptService.updateNewsBySummaryContent(request, news.getId());
            } catch (IOException e) {
                throw new ItCastApplicationException(CRAWLING_PARSE_ERROR);
            }
        });
    }

    public List<String> findLinks(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements articles = document.select(".sa_thumb_inner");

        List<String> links = new ArrayList<>();
        articles.forEach(article -> {
            if (links.size() >= LINK_SIZE) {
                return;
            }
            String link = article.select("a").attr("href");
            links.add(link);
        });
        return links;
    }

    List<String> isValidLinks(List<String> links) {
        List<String> isValidLinks = newsRepository.findAllLinks();
        if (isValidLinks.isEmpty()) {
            throw new ItCastApplicationException(INVALID_NEWS_CONTENT);
        }
        List<String> validLinks = links
                .stream()
                .filter(link -> !isValidLinks.contains(link))
                .distinct()
                .toList();
        return validLinks;
    }

    @Transactional
    public void newsAlarm() {
        LocalDate yesterday = LocalDate.now().minusDays(YESTERDAY);
        List<News> createdAlarm = newsRepository.findAllByCreatedAt(yesterday);

        if (createdAlarm.isEmpty()) {
            throw new ItCastApplicationException(INVALID_NEWS_CONTENT);
        }
        LocalDateTime sendAt = LocalDateTime.now().plusDays(ALARM_DAY).plusHours(ALARM_HOUR);
        createdAlarm.forEach(alarm -> {
            if (alarm == null) {
                throw new ItCastApplicationException(INVALID_NEWS_CONTENT);
            }
            alarm.newsUpdate(sendAt);
        });
    }

    @Transactional
    public void deleteOldData() {
        newsRepository.deleteOldNews();
    }

    LocalDateTime convertDateTime(String info) {
        if (info == null || info.trim().isEmpty()) {
            throw new ItCastApplicationException(INVALID_NEWS_CONTENT);
        }
        String[] parts = info.split(" ");
        if (parts.length != 4) {
            throw new ItCastApplicationException(INVALID_NEWS_DATE);
        }
        String date = parts[1];
        String ampm = parts[2];
        String time = parts[3];

        date = date.replaceAll("입력", "");
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);

        if (ampm.equals("오후") && hour != HOUR) {
            hour += HOUR;
        }
        if (ampm.equals("오전") && hour == HOUR) {
            hour = 0;
        }

        String timeDate = date + " " + String.format("%02d", hour) + ":" + timeParts[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(timeDate, formatter);
    }

    public String cleanContent(String info) {
        if (info == null || info.trim().isEmpty()) {
            throw new ItCastApplicationException(INVALID_NEWS_CONTENT);
        }

        info = info.replaceAll("\\[.*?\\]", "")
                .replaceAll("\\(.*?\\)", "")
                .trim();
        return info;
    }

}
