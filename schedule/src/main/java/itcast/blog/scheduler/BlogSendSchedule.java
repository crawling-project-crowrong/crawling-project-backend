package itcast.blog.scheduler;

import itcast.blog.application.BlogSendService;
import itcast.exception.ItCastApplicationException;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j(topic = "블로그 전송 스케쥴")
@Component
@RequiredArgsConstructor
public class BlogSendSchedule {

    private final BlogSendService blogSendService;

    @Scheduled(cron = "${scheduler.blog.sending}")
    public void sendEmail() {
        log.info("Blog Send Start ...");

        LocalDate today = LocalDate.now();
        final String requestId = UUID.randomUUID().toString();
        MDC.put("request_id", requestId);

        try {
            blogSendService.sendBlogForEmail(today);
            log.info("Blog Send Finished !!");
        } catch (ItCastApplicationException e) {
            log.error("이메일을 보낼 때 오류가 발생하였습니다. ErrorCode: {}, Message: {}",
                    e.getErrorCodes(),
                    e.getMessage(),
                    e);
        } finally {
            MDC.clear();
        }
    }

/*    @Scheduled
    public void sendKakaoTalk(){
        log.info("Blog Send Start ...");
        log.info("Blog Send Finished !!");
    }*/
}
