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
public class OldDataSchedule {
    private final NewsService newsService;

    @Scheduled(cron = "${scheduler.news.old-delete-data}")
    public void scheduleNewsCrawling() throws IOException {
        log.info("deleting old data....");
        final String requestId = UUID.randomUUID().toString();
        MDC.put("request_id", requestId);

        try {
            newsService.deleteOldData();
            log.info("deleting old data....");
        } catch (ItCastApplicationException e) {
            log.error("오래된 데이터를 삭제할 때 에러가 발생하였습니다.. ErrorCode: {}, Message: {}",
                    e.getErrorCodes(),
                    e.getMessage(),
                    e);
        } finally {
            MDC.clear();
        }
    }
}
