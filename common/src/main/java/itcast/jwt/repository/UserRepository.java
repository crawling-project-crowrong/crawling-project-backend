package itcast.jwt.repository;

import java.util.List;
import java.util.Optional;

import itcast.domain.user.enums.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import itcast.domain.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByKakaoEmail(String kakaoEmail);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.interest = :interest")
    List<User> findAllByInterest(@Param("interest") Interest interest);

    User findByEmail(String email);
}
