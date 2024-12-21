package itcast.blog.scheduler;

import itcast.blog.application.BlogSelectService;
import itcast.exception.ItCastApplicationException;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j(topic = "블로그 선택 스케쥴")
@Component
@RequiredArgsConstructor
public class BlogSelectSchedule {

    private final BlogSelectService blogSelectService;

    @Scheduled(cron = "${scheduler.blog.selecting}")
    public void selectForSend() {
        log.info("Blog Select Start ...");

        final String requestId = UUID.randomUUID().toString();
        MDC.put("request_id", requestId);

        try {
            LocalDate today = LocalDate.now();
            blogSelectService.selectBlogs(today);
            log.info("Blog Select Finished!!");
        } catch (ItCastApplicationException e) {
            log.error("블로그를 선택할 때 에러가 발생하였습니다. ErrorCode: {}, Message: {}",
                    e.getErrorCodes(),
                    e.getMessage(),
                    e);
        } finally {
            MDC.clear();
        }
    }
}
