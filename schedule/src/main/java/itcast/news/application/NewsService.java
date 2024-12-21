package itcast.news.application;

import static itcast.exception.ErrorCodes.INVALID_NEWS_CONTENT;
import static itcast.exception.ErrorCodes.NEWS_CRAWLING_ERROR;

import itcast.ai.application.GPTService;
import itcast.ai.dto.request.GPTSummaryRequest;
import itcast.ai.dto.request.Message;
import itcast.domain.news.News;
import itcast.exception.ItCastApplicationException;
import itcast.news.dto.request.CreateNewsRequest;
import itcast.news.repository.NewsRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsService {

    private static final int LINK_SIZE = 10;
    private static final int HOUR = 12;
    private static final String URL = "https://news.naver.com/breakingnews/section/105/283";

    private final NewsRepository newsRepository;
    private final GPTService gptService;

    public void newsCrawling() throws IOException {
        List<String> links = findLinks(URL);
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
                    log.error("썸네일이 존재하지 않습니다. {}", link);
                    throw new ItCastApplicationException(INVALID_NEWS_CONTENT);
                }

                CreateNewsRequest newsRequest = new CreateNewsRequest(titles, content, link, thumbnail, publishedAt);
                News news = newsRepository.save(newsRequest.toEntity(titles, content, link, thumbnail, publishedAt));
                Message message = new Message("user", content);
                GPTSummaryRequest request = new GPTSummaryRequest("gpt-4o-mini", message, 0.7f);
                gptService.updateNewsBySummaryContent(request, news.getId());
            } catch (IOException e) {
                throw new ItCastApplicationException(NEWS_CRAWLING_ERROR);
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

    public List<String> isValidLinks(List<String> links) {
        List<String> isValidLinks = newsRepository.findAllLinks();
        return links.stream()
                .filter(link -> !isValidLinks.contains(link))
                .distinct()
                .toList();
    }

    @Transactional
    public void deleteOldData() {
        newsRepository.deleteOldNews();
    }

    public LocalDateTime convertDateTime(String info) {
        if (info == null || info.trim().isEmpty()) {
            throw new ItCastApplicationException(INVALID_NEWS_CONTENT);
        }
        String[] parts = info.replaceAll("입력", "").split(" ");

        String date = parts[0];
        String ampm = parts[1];
        String time = parts[2];

        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);

        if (ampm.equals("오후") && hour != HOUR) {
            hour += HOUR;
        }
        if (ampm.equals("오전") && hour == HOUR) {
            hour = 0;
        }

        String timeDate = date + " " + String.format("%02d", hour) + ":" + timeParts[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
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
