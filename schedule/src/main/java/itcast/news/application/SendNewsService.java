package itcast.news.application;

import itcast.domain.news.News;
import itcast.domain.user.User;
import itcast.domain.user.enums.Interest;
import itcast.exception.ErrorCodes;
import itcast.exception.ItCastApplicationException;
import itcast.jwt.repository.UserRepository;
import itcast.mail.application.MailService;
import itcast.mail.dto.request.MailContent;
import itcast.mail.dto.request.SendMailRequest;
import itcast.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SendNewsService {


    private static final int YESTERDAY = 1;
    private static final int ALARM_DAY = 2;

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Transactional
    public void selectNews() {
        LocalDate yesterday = LocalDate.now().minusDays(YESTERDAY);
        List<News> newsList = newsRepository.findRatingTot3ByCreatedAtOrdarByRating(yesterday);

        if (newsList == null || newsList.isEmpty()) {
            throw new ItCastApplicationException(ErrorCodes.INVALID_NEWS_CONTENT);
        }

        LocalDate sendAt = LocalDate.now().plusDays(ALARM_DAY);

        newsList.forEach(news -> {
            news.newsUpdate(sendAt);
        });
    }

    public void sendNews() {
        List<News> sendNews = newsRepository.findAllBySendAt();

        if (sendNews == null || sendNews.isEmpty()) {
            throw new ItCastApplicationException(ErrorCodes.NOT_FOUND_SEND_DATA);
        }

        List<MailContent> mailContents = sendNews.stream()
                .map(news ->
                        MailContent.of(
                                news.getTitle(),
                                news.getContent(),
                                news.getLink(),
                                news.getThumbnail()))
                .toList();

        List<String> emails = retrieveUserEmails(Interest.NEWS);

        if (emails == null || emails.isEmpty()) {
            throw new ItCastApplicationException(ErrorCodes.NOT_FOUND_EMAIL);
        }

        SendMailRequest mailRequest = new SendMailRequest(emails, mailContents);
        mailService.send(mailRequest);
    }

    public List<String> retrieveUserEmails(Interest interest) {
        if (interest != Interest.NEWS) {
            throw new ItCastApplicationException(ErrorCodes.INVALID_INTEREST_TYPE_ERROR);
        }
        return userRepository.findAllByInterest(interest)
                .stream()
                .map(User::getEmail)
                .toList();
    }
    public void createNewsHistory(String email) {

    }
}
