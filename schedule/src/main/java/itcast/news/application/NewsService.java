package itcast.news.application;

import itcast.news.dto.request.CreateNewsRequest;
import itcast.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    @Value("${spring.crawler.naver-it-url}")
    private String url;
    private final int LINK_SIZE = 10;
    private final int HOUR = 12;

    private final NewsRepository newsRepository;

    public void newsCrawling() throws IOException {
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
        List<String> isValidLinks = newsRepository.findAllLinks();

        List<String> validLinks = links
                .stream()
                .filter(link -> !isValidLinks.contains(link))
                .distinct()
                .collect(Collectors.toList());

        if(validLinks.isEmpty()) {
           throw new RuntimeException("No links found");
        }

        validLinks.forEach(link -> {
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

                // dto 저장
                if (thumbnail.isEmpty()) {
                    System.out.println("썸네일이 없습니다");
                }
                CreateNewsRequest newsRequest = new CreateNewsRequest(titles, content, link, thumbnail, publishedAt);
                newsRepository.save(newsRequest.toEntity(titles, content, link, thumbnail, publishedAt));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private LocalDateTime convertDateTime(String info) {
        String[] parts = info.split(" ");
        String date = parts[0];
        String ampm = parts[1];
        String time = parts[2];

        date = date.replaceAll("입력", "");
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);

        if (ampm.equals("오후") && hour != HOUR) {
            hour += HOUR;
        }

        String timeDate = date + " " + String.format("%02d", hour) + ":" + timeParts[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(timeDate, formatter);
        return localDateTime;
    }

    private String cleanContent(String info) {
        info = info.replaceAll("\\[.*?\\]", "")
                .replaceAll("\\(.*?\\)", "")
                .trim();
        return info;
    }
}
