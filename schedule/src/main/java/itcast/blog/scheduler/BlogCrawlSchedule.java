package itcast.blog.scheduler;

import itcast.blog.application.BlogCrawlService;
import itcast.exception.ItCastApplicationException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j(topic = "블로그 크롤링 스케쥴")
@Component
@RequiredArgsConstructor
public class BlogCrawlSchedule {

    private final BlogCrawlService blogCrawlService;

    @Scheduled(cron = "${scheduler.blog.crawling}")
    public void velogCrawling() {
        log.info("Velog Crawling Start ...");

        final String requestId = UUID.randomUUID().toString();
        MDC.put("request_id", requestId);

        try {
            blogCrawlService.crawlVelog();
            log.info("Velog Crawling & Save!");
        } catch (ItCastApplicationException e) {
            log.error("블로그를 크롤랑할 때 에러가 발생하였습니다. ErrorCode: {}, Message: {}",
                    e.getErrorCodes(),
                    e.getMessage(),
                    e);
        } finally {
            MDC.clear();
        }
    }

    @Scheduled(cron = "${scheduler.blog.crawling}")
    public void yozmCrawling() {
        log.info("Yozm Crawling Start ...");

        blogCrawlService.crawlYozm();

        log.info("Yozm Crawling & Save!");
    }
}
