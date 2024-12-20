package itcast.news.common.schedule;

import itcast.news.application.NewsService;
import itcast.news.application.SendNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmSchedule {
    private final NewsService newsService;
    private final SendNewsService sendNewsService;

    @Scheduled(cron = "${scheduler.news.select-news}")
    public void selectNewsSchedule() {
        log.info("Selecting schedule....");
        sendNewsService.selectNews();
        log.info("Selecting schedule Finish");
    }

    @Scheduled(cron = "${scheduler.news.send-alarm}")
    public void sendAlarmSchedule() {
        log.info("Sending schedule....");
        sendNewsService.sendNews();
        log.info("Sending schedule Finish");
    }
}
