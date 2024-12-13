package itcast.application;

import itcast.domain.news.News;
import itcast.domain.user.User;
import itcast.dto.request.AdminNewsRequest;
import itcast.dto.response.AdminNewsResponse;
import itcast.exception.IdNotFoundException;
import itcast.exception.NotAdminException;
import itcast.repository.AdminRepository;
import itcast.repository.NewsRepository;
import itcast.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public AdminNewsResponse createNews(Long userId, News news) {
        isAdmin(userId);
        News savedNews = newsRepository.save(news);

        return new AdminNewsResponse(savedNews);
    }

    @Transactional
    public AdminNewsResponse updateNews(Long userId, Long newsId, AdminNewsRequest adminNewsRequest) {
        isAdmin(userId);
        News news = newsRepository.findById(newsId).orElseThrow(()->
                new IdNotFoundException("해당 뉴스가 존재하지 않습니다"));

        news.update(adminNewsRequest.title(),
                adminNewsRequest.content(),
                adminNewsRequest.originalContent(),
                adminNewsRequest.interest(),
                adminNewsRequest.publishedAt(),
                adminNewsRequest.rating(),
                adminNewsRequest.link(),
                adminNewsRequest.thumbnail(),
                adminNewsRequest.status(),
                adminNewsRequest.sendAt()
        );

        return new AdminNewsResponse(news);
    }

    private void isAdmin(Long id){
        User user = userRepository.findById(id).orElseThrow(()->
                new IdNotFoundException("해당 유저가 존재하지 않습니다."));
        String email = user.getKakaoEmail();
        if(!adminRepository.existsByEmail(email)){
            throw new NotAdminException("접근할 수 없는 유저입니다.");
        }
    }
}
