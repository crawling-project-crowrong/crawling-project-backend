package itcast.user.application;

import org.springframework.stereotype.Service;

import itcast.domain.user.User;
import itcast.domain.user.enums.ArticleType;
import itcast.domain.user.enums.Interest;
import itcast.user.dto.request.ProfileCreateRequest;
import itcast.user.dto.response.ProfileCreateResponse;
import itcast.user.exception.EmailAlreadyExistsException;
import itcast.user.exception.NicknameAlreadyExistsException;
import itcast.user.exception.UserNotFoundException;
import itcast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public ProfileCreateResponse createProfile(ProfileCreateRequest request, Long id) {
		User existingUser = userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
		if (userRepository.existsByNickname(request.nickname())) {
			throw new NicknameAlreadyExistsException("이미 사용 중인 닉네임입니다.");
		}
		if (userRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException("이미 사용 중인 이메일입니다.");
		}
		if (ArticleType.NEWS.equals(request.articleType())) {
			request = new ProfileCreateRequest(
				request.nickname(),
				request.articleType(),
				Interest.NEWS,
				request.sendingType(),
				request.email()
			);
		}
		User updatedUser = request.toEntity(existingUser);
		User savedUser = userRepository.save(updatedUser);
		return ProfileCreateResponse.fromEntity(savedUser);
	}
}
