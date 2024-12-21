package itcast.news.common.schedule;

import itcast.exception.ItCastApplicationException;
import itcast.news.application.NewsService;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsSchedule {

    private final NewsService newsService;

    @Scheduled(cron = "${scheduler.news.crawling}")
    public void scheduleNewsCrawling() throws IOException {
        log.info("crawling....");

        final String requestId = UUID.randomUUID().toString();
        MDC.put("request_id", requestId);

        try {
            newsService.newsCrawling();
            log.info("crawled Finish");
        } catch (ItCastApplicationException e) {
            log.error("뉴스를 크롤랑할 때 에러가 발생하였습니다. ErrorCode: {}, Message: {}",
                    e.getErrorCodes(),
                    e.getMessage(),
                    e);
        } finally {
            MDC.clear();
        }
    }
}
